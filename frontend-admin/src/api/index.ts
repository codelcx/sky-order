import axios, { type AxiosRequestConfig, type AxiosError, AxiosHeaders } from 'axios'

export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface PageResult<T> {
  total: number
  records: T[]
  pageSize: number
  pageNum: number
}

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string | undefined,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      (config.headers as AxiosHeaders).set('Authorization', `Bearer ${token}`)
    }
    return config
  },
  (error) => Promise.reject(error),
)

instance.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse<unknown>
    if (res.code !== 0) {
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return response
  },
  (error: AxiosError) => {
    if (error.response) {
      const status = error.response.status
      const messages: Record<number, string> = {
        401: '未登录或登录已过期',
        403: '无权限访问',
        404: '请求的资源不存在',
        500: '服务器错误',
      }
      return Promise.reject(new Error(messages[status] || `请求失败 (${status})`))
    }
    if (error.code === 'ECONNABORTED') {
      return Promise.reject(new Error('请求超时'))
    }
    return Promise.reject(new Error('网络错误'))
  },
)

async function request<T>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
  const response = await instance(config)
  return response.data as ApiResponse<T>
}

export default request
