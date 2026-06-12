import request from '@/api'
import type { PageResult } from '@/api'

// ============================================================================
// 枚举
// ============================================================================

/** 分类状态 */
export enum CategoryStatus {
  /** 禁用 */
  Disabled = 0,
  /** 启用 */
  Enabled = 1,
}

/** 分类类型 */
export enum CategoryType {
  /** 菜品 */
  Dish = 1,
  /** 套餐 */
  Setmeal = 2,
}

// ============================================================================
// 实体
// ============================================================================

/** 分类实体 */
export interface Category {
  /** 主键 ID */
  id: number
  /** 分类类型 */
  type: CategoryType
  /** 分类名称 */
  name: string
  /** 排序值 */
  sort: number
  /** 状态 */
  status: CategoryStatus
  /** 关联菜品数量 */
  dishCount: number
  /** 关联套餐数量 */
  setmealCount: number
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

/** 分类分页查询参数 */
export interface CategoryPageParams {
  /** 分类名称（模糊查询） */
  name?: string
  /** 分类类型 */
  type?: CategoryType
  /** 页码 */
  page?: number
  /** 每页记录数 */
  pageSize?: number
}

/** 新增分类请求体 */
export interface CreateCategoryBody {
  /** 分类类型 */
  type: CategoryType
  /** 分类名称 */
  name: string
  /** 排序值 */
  sort: number
  /** 状态 */
  status: CategoryStatus
}

/** 修改分类请求体 */
export interface UpdateCategoryBody extends CreateCategoryBody {
  /** 分类 ID */
  id: number
}

// ============================================================================
// 响应体
// ============================================================================

/** 分类分页响应 */
export type CategoryPageResp = PageResult<Category>

/** 分类分页查询 */
export function getCategoryPage(params: CategoryPageParams) {
  return request<PageResult<Category>>({
    url: '/admin/category/page',
    method: 'GET',
    params,
  })
}

/** 根据 ID 查询分类 */
export function getCategoryById(id: number) {
  return request<Category>({
    url: `/admin/category/${id}`,
    method: 'GET',
  })
}

/** 新增分类 */
export function createCategory(data: CreateCategoryBody) {
  return request<null>({
    url: '/admin/category',
    method: 'POST',
    data,
  })
}

/** 修改分类信息 */
export function updateCategory(data: UpdateCategoryBody) {
  return request<null>({
    url: '/admin/category',
    method: 'PUT',
    data,
  })
}

/** 启用或禁用分类 */
export function updateCategoryStatus(id: number, status: CategoryStatus) {
  return request<null>({
    url: `/admin/category/status/${status}`,
    method: 'POST',
    params: { id },
  })
}

/** 删除分类 */
export function deleteCategory(id: number) {
  return request<null>({
    url: `/admin/category/${id}`,
    method: 'DELETE',
  })
}
