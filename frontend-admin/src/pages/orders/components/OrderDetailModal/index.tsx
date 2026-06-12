import { Icon } from '@iconify/react'
import type { ColumnsType } from 'antd/es/table'
import {
  type OrderVO,
  type OrderDetail,
  OrderStatus,
  PayMethod,
  PayStatus,
} from '@/api/order'
interface OrderDetailModalProps {
  open: boolean
  order: OrderVO | null
  loading: boolean
  afterClose: () => void
  onConfirm?: () => void
  onReject?: () => void
  onCancel?: () => void
  onDelivery?: () => void
  onComplete?: () => void
}

const statusLabels: Record<OrderStatus, string> = {
  [OrderStatus.PENDING_PAYMENT]: '待付款',
  [OrderStatus.TO_BE_CONFIRMED]: '待接单',
  [OrderStatus.CONFIRMED]: '已接单',
  [OrderStatus.DELIVERY_IN_PROGRESS]: '派送中',
  [OrderStatus.COMPLETED]: '已完成',
  [OrderStatus.CANCELLED]: '已取消',
}

const payMethodLabels: Record<PayMethod, string> = {
  [PayMethod.WeChat]: '微信支付',
  [PayMethod.Alipay]: '支付宝',
}

const payStatusLabels: Record<PayStatus, string> = {
  [PayStatus.Unpaid]: '未支付',
  [PayStatus.Paid]: '已支付',
  [PayStatus.Refund]: '退款',
}

export default function OrderDetailModal(props: OrderDetailModalProps) {
  const { open, order, loading, afterClose, onConfirm, onReject, onCancel, onDelivery, onComplete } = props

  const productColumns: ColumnsType<OrderDetail> = [
    { title: '菜品名称', dataIndex: 'name', key: 'name', width: 180 },
    { title: '口味', dataIndex: 'dishFlavor', key: 'dishFlavor', width: 100 },
    { title: '数量', dataIndex: 'number', key: 'number', width: 80, align: 'center' },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      width: 100,
      align: 'center',
      render: (value: number) => `¥${value.toFixed(2)}`,
    },
  ]

  function showActions() {
    if (!order) return false
    return (
      order.status === OrderStatus.TO_BE_CONFIRMED ||
      order.status === OrderStatus.CONFIRMED ||
      order.status === OrderStatus.DELIVERY_IN_PROGRESS ||
      order.status === OrderStatus.COMPLETED
    )
  }

  return (
    <Modal
      centered
      className="orders-page__detail"
      closeIcon={<Icon icon="lucide:x" />}
      destroyOnHidden
      footer={null}
      onCancel={afterClose}
      open={open}
      title={order ? `订单详情 - ${order.number}` : '订单详情'}
      width={800}
      wrapClassName="orders-page__detail-wrap"
    >
      <div className="orders-page__detail-body">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px 0' }}>
            <Spin />
          </div>
        ) : order ? (
          <>
            <div className="orders-page__detail-section">
              <div className="orders-page__detail-section-title">订单信息</div>
              <div className="orders-page__detail-grid">
                <div className="orders-page__detail-item">
                  <span className="orders-page__detail-label">订单号：</span>
                  <span className="orders-page__detail-value">{order.number}</span>
                </div>
                <div className="orders-page__detail-item">
                  <span className="orders-page__detail-label">订单状态：</span>
                  <span className="orders-page__detail-value">
                    <Tag
                      className={`orders-page__tag orders-page__tag--${order.status}`}
                      variant="filled"
                    >
                      {statusLabels[order.status]}
                    </Tag>
                  </span>
                </div>
                <div className="orders-page__detail-item">
                  <span className="orders-page__detail-label">下单时间：</span>
                  <span className="orders-page__detail-value">{order.orderTime}</span>
                </div>
                {order.checkoutTime && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">结账时间：</span>
                    <span className="orders-page__detail-value">{order.checkoutTime}</span>
                  </div>
                )}
                {order.payMethod !== undefined && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">支付方式：</span>
                    <span className="orders-page__detail-value">
                      {payMethodLabels[order.payMethod as PayMethod]}
                    </span>
                  </div>
                )}
                {order.payStatus !== undefined && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">支付状态：</span>
                    <span className="orders-page__detail-value">
                      {payStatusLabels[order.payStatus as PayStatus]}
                    </span>
                  </div>
                )}
                <div className="orders-page__detail-item">
                  <span className="orders-page__detail-label">实收金额：</span>
                  <span className="orders-page__detail-value orders-page__amount">
                    ¥{order.amount.toFixed(2)}
                  </span>
                </div>
                {order.packAmount !== undefined && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">打包费：</span>
                    <span className="orders-page__detail-value">¥{order.packAmount.toFixed(2)}</span>
                  </div>
                )}
                {order.remark && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">备注：</span>
                    <span className="orders-page__detail-value">{order.remark}</span>
                  </div>
                )}
                {order.cancelReason && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">取消原因：</span>
                    <span className="orders-page__detail-value">{order.cancelReason}</span>
                  </div>
                )}
                {order.rejectionReason && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">拒绝原因：</span>
                    <span className="orders-page__detail-value">{order.rejectionReason}</span>
                  </div>
                )}
              </div>
            </div>

            <div className="orders-page__detail-section">
              <div className="orders-page__detail-section-title">客户信息</div>
              <div className="orders-page__detail-grid">
                {order.consignee && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">收货人：</span>
                    <span className="orders-page__detail-value">{order.consignee}</span>
                  </div>
                )}
                <div className="orders-page__detail-item">
                  <span className="orders-page__detail-label">手机号：</span>
                  <span className="orders-page__detail-value">{order.phone}</span>
                </div>
                {order.userName && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">用户名：</span>
                    <span className="orders-page__detail-value">{order.userName}</span>
                  </div>
                )}
                {order.address && (
                  <div className="orders-page__detail-item" style={{ gridColumn: '1 / -1' }}>
                    <span className="orders-page__detail-label">地址：</span>
                    <span className="orders-page__detail-value">{order.address}</span>
                  </div>
                )}
                {order.estimatedDeliveryTime && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">预计送达：</span>
                    <span className="orders-page__detail-value">{order.estimatedDeliveryTime}</span>
                  </div>
                )}
                {order.deliveryTime && (
                  <div className="orders-page__detail-item">
                    <span className="orders-page__detail-label">送达时间：</span>
                    <span className="orders-page__detail-value">{order.deliveryTime}</span>
                  </div>
                )}
              </div>
            </div>

            {order.orderDetailList && order.orderDetailList.length > 0 && (
              <div className="orders-page__detail-section">
                <div className="orders-page__detail-section-title">菜品明细</div>
                <Table<OrderDetail>
                  className="orders-page__detail-products"
                  columns={productColumns}
                  dataSource={order.orderDetailList}
                  pagination={false}
                  rowKey="id"
                  size="small"
                />
              </div>
            )}
          </>
        ) : null}
      </div>

      <div className="orders-page__detail-actions">
        {order && order.status === OrderStatus.TO_BE_CONFIRMED && (
          <>
            <Button onClick={afterClose}>关闭</Button>
            <Button type="primary" onClick={onConfirm}>接单</Button>
            <Button danger onClick={onReject}>拒单</Button>
          </>
        )}
        {order && order.status === OrderStatus.CONFIRMED && (
          <>
            <Button onClick={afterClose}>关闭</Button>
            <Button type="primary" onClick={onDelivery}>派送</Button>
            <Button danger onClick={onCancel}>取消</Button>
          </>
        )}
        {order && order.status === OrderStatus.DELIVERY_IN_PROGRESS && (
          <>
            <Button onClick={afterClose}>关闭</Button>
            <Button type="primary" onClick={onComplete}>完成</Button>
            <Button danger onClick={onCancel}>取消</Button>
          </>
        )}
        {order && order.status === OrderStatus.COMPLETED && (
          <>
            <Button onClick={afterClose}>关闭</Button>
            <Button danger onClick={onCancel}>取消</Button>
          </>
        )}
        {(!order || !showActions()) && (
          <Button onClick={afterClose}>关闭</Button>
        )}
      </div>
    </Modal>
  )
}
