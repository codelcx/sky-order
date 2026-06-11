import request from '@/api'
import type { PageResult } from '@/api'

// ============================================================================
// 枚举
// ============================================================================

/** 性别 */
export enum Sex {
  /** 女 */
  Female = '0',
  /** 男 */
  Male = '1',
}

/** 员工状态 */
export enum EmployeeStatus {
  /** 禁用 */
  Disabled = 0,
  /** 启用 */
  Enabled = 1,
}

// ============================================================================
// 实体
// ============================================================================

/** 员工实体 */
export interface Employee {
  /** 主键 ID */
  id: number
  /** 用户名 */
  username: string
  /** 姓名 */
  name: string
  /** 手机号 */
  phone: string
  /** 性别 */
  sex: Sex
  /** 身份证号 */
  idNumber: string
  /** 职业 */
  job?: string
  /** 地址 */
  address?: string
  /** 状态 */
  status: EmployeeStatus
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

/** 员工分页查询参数 */
export interface EmployeePageParams {
  /** 员工姓名（模糊查询） */
  name?: string
  /** 页码 */
  page?: number
  /** 每页记录数 */
  pageSize?: number
}

/** 新增员工请求体 */
export interface CreateEmployeeBody {
  /** 登录账号 */
  username: string
  /** 姓名 */
  name: string
  /** 手机号码 */
  phone: string
  /** 性别 */
  sex: Sex
  /** 身份证号 */
  idNumber: string
  /** 职业 */
  job?: string
  /** 地址 */
  address?: string
}

/** 修改员工请求体 */
export interface UpdateEmployeeBody extends CreateEmployeeBody {
  /** 员工 ID */
  id: number
}

// ============================================================================
// 响应体
// ============================================================================

/** 员工分页响应 */
export type EmployeePageResp = PageResult<Employee>

/** 员工分页查询 */
export function getEmployeePage(params: EmployeePageParams) {
  return request<PageResult<Employee>>({
    url: '/admin/employee/page',
    method: 'GET',
    params,
  })
}

/** 根据 ID 查询员工 */
export function getEmployeeById(id: number) {
  return request<Employee>({
    url: `/admin/employee/${id}`,
    method: 'GET',
  })
}

/** 新增员工 */
export function createEmployee(data: CreateEmployeeBody) {
  return request<null>({
    url: '/admin/employee',
    method: 'POST',
    data,
  })
}

/** 修改员工信息 */
export function updateEmployee(data: UpdateEmployeeBody) {
  return request<null>({
    url: '/admin/employee',
    method: 'PUT',
    data,
  })
}

/** 启用或禁用员工 */
export function updateEmployeeStatus(id: number, status: EmployeeStatus) {
  return request<null>({
    url: `/admin/employee/status/${status}`,
    method: 'POST',
    params: { id },
  })
}

/** 删除员工 */
export function deleteEmployee(id: number) {
  return request<null>({
    url: `/admin/employee/${id}`,
    method: 'DELETE',
  })
}
