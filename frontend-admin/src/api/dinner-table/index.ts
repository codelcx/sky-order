import request from '@/api'
import type { PageResult } from '@/api'

// ============================================================================
// 枚举
// ============================================================================

/** 餐桌状态 */
export enum DinnerTableStatus {
  /** 空闲 */
  Idle = 0,
  /** 使用中 */
  InUse = 1,
  /** 已预订 */
  Reserved = 2,
}

// ============================================================================
// 实体
// ============================================================================

/** 餐桌实体 */
export interface DinnerTable {
  /** 主键 ID */
  id: number
  /** 桌号 */
  tableNumber: number
  /** 座位数 */
  capacity: number
  /** 状态 */
  status: DinnerTableStatus
  /** 二维码图片 URL */
  qrCodeUrl: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
  /** 创建人 ID */
  createUser: number
  /** 更新人 ID */
  updateUser: number
}

// ============================================================================
// 请求参数
// ============================================================================

/** 餐桌分页查询参数 */
export interface DinnerTablePageParams {
  /** 桌号（精确筛选） */
  tableNumber?: number
  /** 状态筛选 */
  status?: DinnerTableStatus
  /** 页码 */
  page?: number
  /** 每页记录数 */
  pageSize?: number
}

/** 新增餐桌请求体 */
export interface CreateDinnerTableBody {
  /** 桌号 */
  tableNumber: number
  /** 座位数 */
  capacity: number
}

/** 修改餐桌请求体 */
export interface UpdateDinnerTableBody {
  /** 餐桌 ID */
  id: number
  /** 桌号 */
  tableNumber?: number
  /** 座位数 */
  capacity?: number
}

// ============================================================================
// 响应体
// ============================================================================

/** 餐桌分页查询 */
export function getDinnerTablePage(params: DinnerTablePageParams) {
  return request<PageResult<DinnerTable>>({
    url: '/admin/dinnerTable/page',
    method: 'GET',
    params,
  })
}

/** 根据 ID 查询餐桌 */
export function getDinnerTableById(id: number) {
  return request<DinnerTable>({
    url: `/admin/dinnerTable/${id}`,
    method: 'GET',
  })
}

/** 新增餐桌 */
export function createDinnerTable(data: CreateDinnerTableBody) {
  return request<null>({
    url: '/admin/dinnerTable',
    method: 'POST',
    data,
  })
}

/** 修改餐桌 */
export function updateDinnerTable(data: UpdateDinnerTableBody) {
  return request<null>({
    url: '/admin/dinnerTable',
    method: 'PUT',
    data,
  })
}

/** 删除餐桌 */
export function deleteDinnerTable(id: number) {
  return request<null>({
    url: `/admin/dinnerTable/${id}`,
    method: 'DELETE',
  })
}

/** 修改餐桌状态 */
export function updateDinnerTableStatus(id: number, status: DinnerTableStatus) {
  return request<null>({
    url: `/admin/dinnerTable/status/${status}`,
    method: 'POST',
    params: { id },
  })
}
