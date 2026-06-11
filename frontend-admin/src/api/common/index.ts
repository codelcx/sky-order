import request from '@/api'

/** 文件上传 */
export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request<string>({
    url: '/admin/common/upload',
    method: 'POST',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: formData,
  })
}
