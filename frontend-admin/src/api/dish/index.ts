import request from '@/api'
import type { PageResult } from '@/api'

// ============================================================================
// 枚举
// ============================================================================

/** 菜品状态 */
export enum DishStatus {
  /** 停售 */
  Disabled = 0,
  /** 起售 */
  Enabled = 1,
}

// ============================================================================
// 实体
// ============================================================================

/** 菜品口味 */
export interface DishFlavor {
  /** 主键 ID */
  id?: number
  /** 菜品 ID */
  dishId?: number
  /** 口味名称 */
  name: string
  /** 口味数据 JSON 数组 */
  value: string
}

/** 菜品实体 */
export interface Dish {
  /** 主键 ID */
  id: number
  /** 菜品名称 */
  name: string
  /** 分类 ID */
  categoryId: number
  /** 分类名称 */
  categoryName: string
  /** 价格 */
  price: number
  /** 图片 URL */
  image: string
  /** 描述 */
  description: string
  /** 状态 */
  status: DishStatus
  /** 口味列表 */
  flavors: DishFlavor[]
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

/** 菜品分页查询参数 */
export interface DishPageParams {
  /** 菜品名称（模糊匹配） */
  name?: string
  /** 分类 ID */
  categoryId?: number
  /** 状态 */
  status?: DishStatus
  /** 页码 */
  page?: number
  /** 每页记录数 */
  pageSize?: number
}

/** 新增菜品请求体 */
export interface CreateDishBody {
  /** 菜品名称 */
  name: string
  /** 分类 ID */
  categoryId: number
  /** 价格 */
  price: number
  /** 图片 URL */
  image: string
  /** 描述 */
  description?: string
  /** 状态 */
  status?: DishStatus
  /** 口味列表 */
  flavors: DishFlavor[]
}

/** 修改菜品请求体 */
export interface UpdateDishBody extends CreateDishBody {
  /** 菜品 ID */
  id: number
}

// ============================================================================
// 响应体
// ============================================================================

/** 菜品分页响应 */
export type DishPageResp = PageResult<Dish>

/** 菜品分页查询 */
export function getDishPage(params: DishPageParams) {
  return request<PageResult<Dish>>({
    url: '/admin/dish/page',
    method: 'GET',
    params,
  })
}

/** 根据 ID 查询菜品 */
export function getDishById(id: number) {
  return request<Dish>({
    url: `/admin/dish/${id}`,
    method: 'GET',
  })
}

/** 新增菜品 */
export function createDish(data: CreateDishBody) {
  return request<null>({
    url: '/admin/dish',
    method: 'POST',
    data,
  })
}

/** 修改菜品 */
export function updateDish(data: UpdateDishBody) {
  return request<null>({
    url: '/admin/dish',
    method: 'PUT',
    data,
  })
}

/** 更新菜品状态 */
export function updateDishStatus(id: number, status: DishStatus) {
  return request<null>({
    url: `/admin/dish/status/${status}`,
    method: 'POST',
    params: { id },
  })
}

/** 删除菜品 */
export function deleteDish(ids: number[]) {
  return request<null>({
    url: '/admin/dish',
    method: 'DELETE',
    params: { ids: ids.join(',') },
  })
}

/** 根据分类 ID 查询菜品列表 */
export function getDishListByCategoryId(categoryId: number) {
  return request<Dish[]>({
    url: '/admin/dish/list',
    method: 'GET',
    params: { categoryId },
  })
}

