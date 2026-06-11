import request from '@/api'
import type { PageResult } from '@/api'

// ============================================================================
// 枚举
// ============================================================================

/** 通用状态 */
export enum DictStatus {
  /** 禁用 */
  Disabled = 0,
  /** 启用 */
  Enabled = 1,
}

// ============================================================================
// 实体
// ============================================================================

/** 字典类型实体 */
export interface DictType {
  /** 主键 ID */
  id: number
  /** 字典名称 */
  name: string
  /** 字典编码 */
  code: string
  /** 描述 */
  description?: string
  /** 状态 */
  status: DictStatus
  /** 排序值 */
  sort: number
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
  /** 创建人 ID */
  createUser: number
  /** 修改人 ID */
  updateUser: number
}

/** 字典数据实体 */
export interface DictData {
  /** 主键 ID */
  id: number
  /** 字典类型 ID */
  dictTypeId: number
  /** 字典标签 */
  label: string
  /** 字典键值 */
  value: string
  /** 是否默认（0:否 1:是） */
  isDefault: number
  /** 状态 */
  status: DictStatus
  /** 排序值 */
  sort: number
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
  /** 创建人 ID */
  createUser: number
  /** 修改人 ID */
  updateUser: number
}

// ============================================================================
// 请求参数
// ============================================================================

/** 字典类型分页查询参数 */
export interface DictTypePageParams {
  /** 字典名称（模糊匹配） */
  name?: string
  /** 字典编码（模糊匹配） */
  code?: string
  /** 状态 */
  status?: DictStatus
  /** 页码 */
  page?: number
  /** 每页记录数 */
  pageSize?: number
}

/** 新增字典类型请求体 */
export interface CreateDictTypeBody {
  /** 字典名称 */
  name: string
  /** 字典编码 */
  code: string
  /** 描述 */
  description?: string
  /** 排序值 */
  sort: number
  /** 状态 */
  status?: DictStatus
}

/** 修改字典类型请求体 */
export interface UpdateDictTypeBody extends CreateDictTypeBody {
  /** 字典类型 ID */
  id: number
}

/** 字典数据分页查询参数 */
export interface DictDataPageParams {
  /** 字典类型编码（精确匹配） */
  dictTypeCode?: string
  /** 字典标签（模糊匹配） */
  label?: string
  /** 状态 */
  status?: DictStatus
  /** 页码 */
  page?: number
  /** 每页记录数 */
  pageSize?: number
}

/** 新增字典数据请求体 */
export interface CreateDictDataBody {
  /** 字典类型 ID */
  dictTypeId: number
  /** 字典标签 */
  label: string
  /** 字典键值 */
  value: string
  /** 是否默认 */
  isDefault?: number
  /** 排序值 */
  sort: number
  /** 备注 */
  remark?: string
  /** 状态 */
  status?: DictStatus
}

/** 修改字典数据请求体 */
export interface UpdateDictDataBody {
  /** 字典数据 ID */
  id: number
  /** 字典类型 ID */
  dictTypeId?: number
  /** 字典标签 */
  label?: string
  /** 字典键值 */
  value?: string
  /** 是否默认 */
  isDefault?: number
  /** 排序值 */
  sort?: number
  /** 备注 */
  remark?: string
  /** 状态 */
  status?: DictStatus
}

// ============================================================================
// 响应体
// ============================================================================

/** 字典类型分页响应 */
export type DictTypePageResp = PageResult<DictType>

/** 字典数据分页响应 */
export type DictDataPageResp = PageResult<DictData>

// ============================================================================
// 字典类型 API
// ============================================================================

/** 字典类型分页查询 */
export function getDictTypePage(params: DictTypePageParams) {
  return request<PageResult<DictType>>({
    url: '/admin/dict/type/page',
    method: 'GET',
    params,
  })
}

/** 根据 ID 查询字典类型 */
export function getDictTypeById(id: number) {
  return request<DictType>({
    url: `/admin/dict/type/${id}`,
    method: 'GET',
  })
}

/** 新增字典类型 */
export function createDictType(data: CreateDictTypeBody) {
  return request<null>({
    url: '/admin/dict/type',
    method: 'POST',
    data,
  })
}

/** 修改字典类型 */
export function updateDictType(data: UpdateDictTypeBody) {
  return request<null>({
    url: '/admin/dict/type',
    method: 'PUT',
    data,
  })
}

/** 启用或禁用字典类型 */
export function updateDictTypeStatus(id: number, status: DictStatus) {
  return request<null>({
    url: `/admin/dict/type/status/${status}`,
    method: 'PUT',
    params: { id },
  })
}

/** 删除字典类型 */
export function deleteDictType(id: number) {
  return request<null>({
    url: `/admin/dict/type/${id}`,
    method: 'DELETE',
  })
}

// ============================================================================
// 字典数据 API
// ============================================================================

/** 字典数据分页查询 */
export function getDictDataPage(params: DictDataPageParams) {
  return request<PageResult<DictData>>({
    url: '/admin/dict/data/page',
    method: 'GET',
    params,
  })
}

/** 根据 ID 查询字典数据 */
export function getDictDataById(id: number) {
  return request<DictData>({
    url: `/admin/dict/data/${id}`,
    method: 'GET',
  })
}

/** 新增字典数据 */
export function createDictData(data: CreateDictDataBody) {
  return request<null>({
    url: '/admin/dict/data',
    method: 'POST',
    data,
  })
}

/** 修改字典数据 */
export function updateDictData(data: UpdateDictDataBody) {
  return request<null>({
    url: '/admin/dict/data',
    method: 'PUT',
    data,
  })
}

/** 启用或禁用字典数据 */
export function updateDictDataStatus(id: number, status: DictStatus) {
  return request<null>({
    url: `/admin/dict/data/status/${status}`,
    method: 'PUT',
    params: { id },
  })
}

/** 删除字典数据 */
export function deleteDictData(id: number) {
  return request<null>({
    url: `/admin/dict/data/${id}`,
    method: 'DELETE',
  })
}

/** 根据字典编码获取字典数据列表 */
export async function getDictDataListByCode(code: string) {
  const response = await getDictDataPage({ dictTypeCode: code, page: 1, pageSize: 999 })
  return response.data.records
}
