import request from '@/api'
import type { PageResult } from '@/api'
// ============================================================================
// 枚举
// ============================================================================

/** 套餐状态 */
export enum SetmealStatus {
  /** 停售 */
  Disabled = 0,
  /** 起售 */
  Enabled = 1,
}

// ============================================================================
// 实体
// ============================================================================

/** 套餐-菜品关联 */
export interface SetmealDish {
  /** 主键 ID */
  id?: number
  /** 套餐 ID */
  setmealId?: number
  /** 菜品 ID */
  dishId: number
  /** 菜品名称（冗余） */
  name?: string
  /** 菜品原价（冗余） */
  price: number
  /** 份数 */
  copies: number
}

/** 套餐实体 */
export interface Setmeal {
  /** 主键 ID */
  id: number
  /** 分类 ID */
  categoryId: number
  /** 分类名称 */
  categoryName: string
  /** 套餐名称 */
  name: string
  /** 价格 */
  price: number
  /** 状态 */
  status: SetmealStatus
  /** 描述 */
  description: string
  /** 图片 URL */
  image: string
  /** 关联菜品列表 */
  setmealDishes: SetmealDish[]
  /** 更新时间 */
  updateTime: string
  /** 创建时间 */
  createTime: string
  /** 创建人 ID */
  createUser: number
  /** 修改人 ID */
  updateUser: number
}

// ============================================================================
// 请求参数
// ============================================================================

/** 套餐分页查询参数 */
export interface SetmealPageParams {
  /** 套餐名称（模糊匹配） */
  name?: string
  /** 分类 ID */
  categoryId?: number
  /** 状态 */
  status?: SetmealStatus
  /** 页码 */
  page?: number
  /** 每页记录数 */
  pageSize?: number
}

/** 新增套餐请求体 */
export interface CreateSetmealBody {
  /** 套餐名称 */
  name: string
  /** 分类 ID */
  categoryId: number
  /** 价格 */
  price: number
  /** 状态 */
  status: SetmealStatus
  /** 图片 URL */
  image: string
  /** 描述 */
  description?: string
  /** 关联菜品列表 */
  setmealDishes: SetmealDish[]
}

/** 修改套餐请求体 */
export interface UpdateSetmealBody extends CreateSetmealBody {
  /** 套餐 ID */
  id: number
}

// ============================================================================
// 响应体
// ============================================================================

/** 套餐分页响应 */
export type SetmealPageResp = PageResult<Setmeal>

/** 套餐分页查询 */
export function getSetmealPage(params: SetmealPageParams) {
  return request<PageResult<Setmeal>>({
    url: '/admin/setmeal/page',
    method: 'GET',
    params,
  })
}

/** 根据 ID 查询套餐 */
export function getSetmealById(id: number) {
  return request<Setmeal>({
    url: `/admin/setmeal/${id}`,
    method: 'GET',
  })
}

/** 新增套餐 */
export function createSetmeal(data: CreateSetmealBody) {
  return request<null>({
    url: '/admin/setmeal',
    method: 'POST',
    data,
  })
}

/** 修改套餐 */
export function updateSetmeal(data: UpdateSetmealBody) {
  return request<null>({
    url: '/admin/setmeal',
    method: 'PUT',
    data,
  })
}

/** 更新套餐状态 */
export function updateSetmealStatus(id: number, status: SetmealStatus) {
  return request<null>({
    url: `/admin/setmeal/status/${status}`,
    method: 'POST',
    params: { id },
  })
}

/** 删除套餐 */
export function deleteSetmeal(ids: number[]) {
  return request<null>({
    url: '/admin/setmeal',
    method: 'DELETE',
    params: { ids: ids.join(',') },
  })
}

/** 根据分类 ID 查询套餐列表 */
export function getSetmealListByCategoryId(categoryId: number) {
  return request<Setmeal[]>({
    url: '/admin/setmeal/list',
    method: 'GET',
    params: { categoryId },
  })
}
