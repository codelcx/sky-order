import request from '@/api'
import type { PageResult } from '@/api'

// ============================================================================
// 枚举
// ============================================================================

/** 订单状态 */
export enum OrderStatus {
  /** 待付款 */
  PENDING_PAYMENT = 1,
  /** 待接单 */
  TO_BE_CONFIRMED = 2,
  /** 已接单 */
  CONFIRMED = 3,
  /** 派送中 */
  DELIVERY_IN_PROGRESS = 4,
  /** 已完成 */
  COMPLETED = 5,
  /** 已取消 */
  CANCELLED = 6,
}

/** 支付方式 */
export enum PayMethod {
  /** 微信支付 */
  WeChat = 1,
  /** 支付宝 */
  Alipay = 2,
}

/** 支付状态 */
export enum PayStatus {
  /** 未支付 */
  Unpaid = 0,
  /** 已支付 */
  Paid = 1,
  /** 退款 */
  Refund = 2,
}

// ============================================================================
// 实体
// ============================================================================

/** 订单明细 */
export interface OrderDetail {
  /** 明细 ID */
  id: number
  /** 名称 */
  name: string
  /** 订单 ID */
  orderId: number
  /** 菜品 ID */
  dishId?: number
  /** 套餐 ID */
  setmealId?: number
  /** 口味 */
  dishFlavor?: string
  /** 数量 */
  number: number
  /** 金额 */
  amount: number
  /** 图片 */
  image?: string
}

/** 订单实体 */
export interface Orders {
  /** 订单 ID */
  id: number
  /** 订单号 */
  number: string
  /** 订单状态 */
  status: OrderStatus
  /** 用户 ID */
  userId: number
  /** 地址 ID */
  addressBookId: number
  /** 下单时间 */
  orderTime: string
  /** 结账时间 */
  checkoutTime?: string
  /** 支付方式 */
  payMethod?: PayMethod
  /** 支付状态 */
  payStatus?: PayStatus
  /** 实收金额 */
  amount: number
  /** 备注 */
  remark?: string
  /** 用户名 */
  userName?: string
  /** 手机号 */
  phone?: string
  /** 地址 */
  address?: string
  /** 收货人 */
  consignee?: string
  /** 取消原因 */
  cancelReason?: string
  /** 拒绝原因 */
  rejectionReason?: string
  /** 取消时间 */
  cancelTime?: string
  /** 预计送达时间 */
  estimatedDeliveryTime?: string
  /** 配送状态 */
  deliveryStatus?: number
  /** 送达时间 */
  deliveryTime?: string
  /** 打包费 */
  packAmount?: number
  /** 餐具数量 */
  tablewareNumber?: number
  /** 餐具数量状态 */
  tablewareStatus?: number
}

/** 订单视图（含菜品信息） */
export interface OrderVO extends Orders {
  /** 订单菜品信息 */
  orderDishes?: string
  /** 订单详情列表 */
  orderDetailList?: OrderDetail[]
}

/** 订单统计 */
export interface OrderStatisticsVO {
  /** 待接单数量 */
  toBeConfirmed: number
  /** 已接单数量 */
  confirmed: number
  /** 派送中数量 */
  deliveryInProgress: number
}

// ============================================================================
// 请求参数
// ============================================================================

/** 订单搜索参数 */
export interface OrderSearchParams {
  /** 页码 */
  page?: number
  /** 每页条数 */
  pageSize?: number
  /** 订单号 */
  number?: string
  /** 手机号码 */
  phone?: string
  /** 订单状态 */
  status?: OrderStatus
  /** 开始时间 */
  beginTime?: string
  /** 结束时间 */
  endTime?: string
  /** 用户 ID */
  userId?: number
}

/** 接单请求体 */
export interface ConfirmOrderBody {
  /** 订单 ID */
  id: number
  /** 订单状态 */
  status: OrderStatus
}

/** 拒单请求体 */
export interface RejectOrderBody {
  /** 订单 ID */
  id: number
  /** 拒绝原因 */
  rejectionReason: string
}

/** 取消订单请求体 */
export interface CancelOrderBody {
  /** 订单 ID */
  id: number
  /** 取消原因 */
  cancelReason: string
}

// ============================================================================
// 响应体
// ============================================================================

/** 订单搜索分页响应 */
export type OrderSearchResp = PageResult<OrderVO>

/** 条件分页查询订单 */
export function conditionSearch(params: OrderSearchParams) {
  return request<PageResult<OrderVO>>({
    url: '/admin/order/conditionSearch',
    method: 'GET',
    params,
  })
}

/** 查询订单详情 */
export function getOrderDetail(id: number) {
  return request<OrderVO>({
    url: `/admin/order/details/${id}`,
    method: 'GET',
  })
}

/** 接单 */
export function confirmOrder(data: ConfirmOrderBody) {
  return request<null>({
    url: '/admin/order/confirm',
    method: 'PUT',
    data,
  })
}

/** 订单状态数量统计 */
export function getOrderStatistics() {
  return request<OrderStatisticsVO>({
    url: '/admin/order/statistics',
    method: 'GET',
  })
}

/** 拒单 */
export function rejectOrder(data: RejectOrderBody) {
  return request<null>({
    url: '/admin/order/rejection',
    method: 'PUT',
    data,
  })
}

/** 取消订单 */
export function cancelOrder(data: CancelOrderBody) {
  return request<null>({
    url: '/admin/order/cancel',
    method: 'PUT',
    data,
  })
}

/** 派送订单 */
export function deliveryOrder(id: number) {
  return request<null>({
    url: `/admin/order/delivery/${id}`,
    method: 'PUT',
  })
}

/** 完成订单 */
export function completeOrder(id: number) {
  return request<null>({
    url: `/admin/order/complete/${id}`,
    method: 'PUT',
  })
}
