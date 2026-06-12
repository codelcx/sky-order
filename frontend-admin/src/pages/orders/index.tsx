import { Icon } from '@iconify/react'
import { Button, DatePicker, Input, message, Modal, Space, Spin, Table, Tag } from 'antd'
import dayjs from 'dayjs'
import type { ColumnsType } from 'antd/es/table'
import {
  cancelOrder,
  completeOrder,
  conditionSearch,
  confirmOrder,
  deliveryOrder,
  getOrderStatistics,
  rejectOrder,
  type OrderVO,
  OrderStatus,
} from '@/api/order'
import { useTableScroll } from '@/hooks/useTableScroll'
import OrderDetailModal from './components/OrderDetailModal'
import './index.scss'

const statusLabels: Record<OrderStatus, string> = {
  [OrderStatus.PENDING_PAYMENT]: '待付款',
  [OrderStatus.TO_BE_CONFIRMED]: '待接单',
  [OrderStatus.CONFIRMED]: '已接单',
  [OrderStatus.DELIVERY_IN_PROGRESS]: '派送中',
  [OrderStatus.COMPLETED]: '已完成',
  [OrderStatus.CANCELLED]: '已取消',
}

const statusTabs: { key: OrderStatus | undefined; label: string; count?: number }[] = [
  { key: undefined, label: '全部' },
  { key: OrderStatus.PENDING_PAYMENT, label: '待付款' },
  { key: OrderStatus.TO_BE_CONFIRMED, label: '待接单' },
  { key: OrderStatus.CONFIRMED, label: '已接单' },
  { key: OrderStatus.DELIVERY_IN_PROGRESS, label: '派送中' },
  { key: OrderStatus.COMPLETED, label: '已完成' },
  { key: OrderStatus.CANCELLED, label: '已取消' },
]

export default function OrdersPage() {
  const [messageApi, messageContextHolder] = message.useMessage()
  const [records, setRecords] = useState<OrderVO[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)

  // search
  const [searchNumber, setSearchNumber] = useState('')
  const [searchPhone, setSearchPhone] = useState('')
  const [searchStatus, setSearchStatus] = useState<OrderStatus | undefined>()
  const [searchDateRange, setSearchDateRange] = useState<[string, string] | null>(null)

  // statistics
  const [statistics, setStatistics] = useState({ toBeConfirmed: 0, confirmed: 0, deliveryInProgress: 0 })

  // detail modal
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailOrder, setDetailOrder] = useState<OrderVO | null>(null)
  const [detailLoading, setDetailLoading] = useState(false)

  // reason modal (for reject / cancel)
  const [reasonModalOpen, setReasonModalOpen] = useState(false)
  const [reasonModalType, setReasonModalType] = useState<'reject' | 'cancel'>('reject')
  const [currentActionOrderId, setCurrentActionOrderId] = useState<number | null>(null)
  const [reasonText, setReasonText] = useState('')
  const [reasonSubmitting, setReasonSubmitting] = useState(false)

  const { tableWrapRef, scrollY } = useTableScroll()
  const searchTimerRef = useRef<ReturnType<typeof setTimeout> | undefined>(undefined)

  const loadData = useCallback(async (page: number, size?: number) => {
    setLoading(true)
    try {
      const ps = size ?? pageSize
      const params: Record<string, unknown> = { page, pageSize: ps }
      if (searchNumber) params.number = searchNumber
      if (searchPhone) params.phone = searchPhone
      if (searchStatus !== undefined) params.status = searchStatus
      if (searchDateRange) {
        params.beginTime = searchDateRange[0]
        params.endTime = searchDateRange[1]
      }
      const response = await conditionSearch(params as OrderSearchParams)
      setRecords(response.data.records)
      setTotal(response.data.total)
      setCurrentPage(response.data.pageNum || page)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '订单列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi, pageSize, searchNumber, searchPhone, searchStatus, searchDateRange])

  const loadStatistics = useCallback(async () => {
    try {
      const response = await getOrderStatistics()
      setStatistics(response.data)
    } catch {
      // silently fail for statistics
    }
  }, [])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    void loadData(1)
    void loadStatistics()
  }, [loadData, loadStatistics])

  useEffect(() => {
    if (fetchedRef.current) {
      if (searchTimerRef.current) clearTimeout(searchTimerRef.current)
      searchTimerRef.current = setTimeout(() => {
        void loadData(1)
      }, 300)
    }
    return () => {
      if (searchTimerRef.current) clearTimeout(searchTimerRef.current)
    }
  }, [searchNumber, searchPhone, searchStatus, searchDateRange])

  // Detail
  async function openDetail(order: OrderVO) {
    setDetailLoading(true)
    setDetailOpen(true)
    try {
      const response = await conditionSearch({ page: 1, pageSize: 1, number: order.number })
      const found = response.data.records.find((r) => r.id === order.id)
      setDetailOrder(found || order)
    } catch {
      setDetailOrder(order)
    } finally {
      setDetailLoading(false)
    }
  }

  function closeDetail() {
    setDetailOpen(false)
  }

  function handleRefresh() {
    void loadData(currentPage, pageSize)
    void loadStatistics()
  }

  // Actions
  async function handleConfirm(order: OrderVO) {
    try {
      await confirmOrder({ id: order.id, status: order.status })
      messageApi.success('已接单')
      handleRefresh()
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '接单失败')
    }
  }

  function openRejectModal(order: OrderVO) {
    setReasonModalType('reject')
    setCurrentActionOrderId(order.id)
    setReasonText('')
    setReasonModalOpen(true)
  }

  function openCancelModal(order: OrderVO) {
    setReasonModalType('cancel')
    setCurrentActionOrderId(order.id)
    setReasonText('')
    setReasonModalOpen(true)
  }

  async function handleSubmitReason() {
    if (!reasonText.trim()) {
      messageApi.warning(reasonModalType === 'reject' ? '请输入拒绝原因' : '请输入取消原因')
      return
    }
    if (currentActionOrderId === null) return
    setReasonSubmitting(true)
    try {
      if (reasonModalType === 'reject') {
        await rejectOrder({ id: currentActionOrderId, rejectionReason: reasonText.trim() })
        messageApi.success('已拒单')
      } else {
        await cancelOrder({ id: currentActionOrderId, cancelReason: reasonText.trim() })
        messageApi.success('已取消订单')
      }
      setReasonModalOpen(false)
      handleRefresh()
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '操作失败')
    } finally {
      setReasonSubmitting(false)
    }
  }

  async function handleDelivery(order: OrderVO) {
    try {
      await deliveryOrder(order.id)
      messageApi.success('已派送')
      handleRefresh()
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '派送失败')
    }
  }

  async function handleComplete(order: OrderVO) {
    try {
      await completeOrder(order.id)
      messageApi.success('已完成')
      handleRefresh()
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '完成订单失败')
    }
  }

  async function handleCancelDirect(order: OrderVO) {
    Modal.confirm({
      title: '确认取消该订单？',
      content: `订单 ${order.number} 将被取消，此操作不可恢复。`,
      okText: '确认取消',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await cancelOrder({ id: order.id, cancelReason: '管理员取消' })
          messageApi.success('订单已取消')
          handleRefresh()
        } catch (error) {
          messageApi.error(error instanceof Error ? error.message : '取消订单失败')
        }
      },
    })
  }

  const columns: ColumnsType<OrderVO> = [
    {
      title: '订单号',
      dataIndex: 'number',
      key: 'number',
      width: 200,
      render: (value: string, record) => (
        <span className="orders-page__number" onClick={() => openDetail(record)}>
          {value}
        </span>
      ),
    },
    {
      title: '客户信息',
      key: 'customer',
      width: 180,
      render: (_, record) => (
        <div className="orders-page__customer">
          <span className="orders-page__customer-name">{record.consignee || record.userName || '-'}</span>
          <span className="orders-page__customer-phone">{record.phone || '-'}</span>
        </div>
      ),
    },
    {
      title: '菜品',
      dataIndex: 'orderDishes',
      key: 'orderDishes',
      width: 260,
      render: (value: string | undefined) => (
        <span className="orders-page__order-dishes" title={value}>
          {value || '-'}
        </span>
      ),
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      width: 100,
      align: 'center',
      render: (value: number) => <span className="orders-page__amount">¥{value.toFixed(2)}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      align: 'center',
      render: (value: OrderStatus) => (
        <Tag className={`orders-page__tag orders-page__tag--${value}`} variant="filled">
          {statusLabels[value]}
        </Tag>
      ),
    },
    {
      title: '下单时间',
      dataIndex: 'orderTime',
      key: 'orderTime',
      width: 170,
    },
    {
      title: '操作',
      key: 'actions',
      width: 300,
      fixed: 'right',
      render: (_, record) => {
        const actions: React.ReactNode[] = []
        switch (record.status) {
          case OrderStatus.TO_BE_CONFIRMED:
            actions.push(
              <Button key="confirm" className="orders-page__text-button" type="primary" onClick={() => handleConfirm(record)}>接单</Button>,
              <Button key="reject" className="orders-page__text-button" danger onClick={() => openRejectModal(record)}>拒单</Button>,
              <Button key="cancel" className="orders-page__text-button" onClick={() => openCancelModal(record)}>取消</Button>,
            )
            break
          case OrderStatus.CONFIRMED:
            actions.push(
              <Button key="delivery" className="orders-page__text-button" type="primary" onClick={() => handleDelivery(record)}>派送</Button>,
              <Button key="cancel" className="orders-page__text-button" danger onClick={() => openCancelModal(record)}>取消</Button>,
            )
            break
          case OrderStatus.DELIVERY_IN_PROGRESS:
            actions.push(
              <Button key="complete" className="orders-page__text-button" type="primary" onClick={() => handleComplete(record)}>完成</Button>,
              <Button key="cancel" className="orders-page__text-button" danger onClick={() => openCancelModal(record)}>取消</Button>,
            )
            break
          case OrderStatus.COMPLETED:
            actions.push(
              <Button key="cancel" className="orders-page__text-button" danger onClick={() => openCancelModal(record)}>取消</Button>,
            )
            break
          case OrderStatus.PENDING_PAYMENT:
            actions.push(
              <Button key="cancel" className="orders-page__text-button" onClick={() => handleCancelDirect(record)}>取消</Button>,
            )
            break
        }
        actions.push(
          <Button key="detail" className="orders-page__text-button" type="link" onClick={() => openDetail(record)}>详情</Button>,
        )
        return <Space size={4}>{actions}</Space>
      },
    },
  ]

  return (
    <>
      {messageContextHolder}
      <section className="orders-page">
        <div className="orders-page__card">
          <div className="orders-page__tabs">
            {statusTabs.map((tab) => {
              let count: number | undefined
              if (tab.key === OrderStatus.TO_BE_CONFIRMED) count = statistics.toBeConfirmed
              else if (tab.key === OrderStatus.CONFIRMED) count = statistics.confirmed
              else if (tab.key === OrderStatus.DELIVERY_IN_PROGRESS) count = statistics.deliveryInProgress
              return (
                <div
                  key={tab.key ?? 'all'}
                  className={`orders-page__tab${searchStatus === tab.key ? ' orders-page__tab--active' : ''}`}
                  onClick={() => {
                    setSearchStatus(tab.key)
                    setCurrentPage(1)
                  }}
                >
                  <span className="orders-page__tab-label">{tab.label}</span>
                  {count !== undefined && <span className="orders-page__tab-count">{count}</span>}
                </div>
              )
            })}
          </div>

          <div className="orders-page__header">
            <div className="orders-page__search">
              <Input
                allowClear
                className="orders-page__search-input"
                placeholder="订单号"
                prefix={<Icon icon="lucide:search" />}
                value={searchNumber}
                onChange={(e) => setSearchNumber(e.target.value)}
              />
              <Input
                allowClear
                className="orders-page__search-input"
                placeholder="手机号"
                prefix={<Icon icon="lucide:phone" />}
                value={searchPhone}
                onChange={(e) => setSearchPhone(e.target.value)}
              />
              <DatePicker.RangePicker
                className="orders-page__search-date"
                format="YYYY-MM-DD HH:mm:ss"
                showTime
                value={
                  searchDateRange
                    ? [dayjs(searchDateRange[0]), dayjs(searchDateRange[1])]
                    : null
                }
                onChange={(dates) => {
                  if (dates && dates[0] && dates[1]) {
                    setSearchDateRange([dates[0].format('YYYY-MM-DD HH:mm:ss'), dates[1].format('YYYY-MM-DD HH:mm:ss')])
                  } else {
                    setSearchDateRange(null)
                  }
                }}
              />
              <Button
                icon={<Icon icon="lucide:rotate-ccw" />}
                onClick={() => {
                  setSearchNumber('')
                  setSearchPhone('')
                  setSearchStatus(undefined)
                  setSearchDateRange(null)
                }}
              >
                重置
              </Button>
            </div>
          </div>

          <div ref={tableWrapRef} className="orders-page__table-wrap">
            <Spin spinning={loading}>
              <Table<OrderVO>
                className="orders-page__table"
                columns={columns}
                dataSource={records}
                pagination={{
                  current: currentPage,
                  pageSize,
                  total,
                  onChange: (page, size) => {
                    setPageSize(size)
                    void loadData(page, size)
                  },
                  pageSizeOptions: ['10', '20', '50'],
                  showSizeChanger: true,
                  showTotal: (total) => `共 ${Math.ceil(total / pageSize)} 页 / ${total} 条`,
                }}
                rowKey="id"
                scroll={{ y: scrollY, x: 1200 }}
              />
            </Spin>
          </div>
        </div>

        <OrderDetailModal
          open={detailOpen}
          order={detailOrder}
          loading={detailLoading}
          afterClose={closeDetail}
          onConfirm={() => {
            if (detailOrder) {
              handleConfirm(detailOrder)
              closeDetail()
            }
          }}
          onReject={() => {
            if (detailOrder) {
              closeDetail()
              openRejectModal(detailOrder)
            }
          }}
          onCancel={() => {
            if (detailOrder) {
              closeDetail()
              openCancelModal(detailOrder)
            }
          }}
          onDelivery={() => {
            if (detailOrder) {
              handleDelivery(detailOrder)
              closeDetail()
            }
          }}
          onComplete={() => {
            if (detailOrder) {
              handleComplete(detailOrder)
              closeDetail()
            }
          }}
        />

        <Modal
          centered
          className="orders-page__reason-modal"
          closeIcon={<Icon icon="lucide:x" />}
          destroyOnHidden
          footer={null}
          onCancel={() => setReasonModalOpen(false)}
          open={reasonModalOpen}
          title={reasonModalType === 'reject' ? '拒单' : '取消订单'}
          width={480}
        >
          <div className="orders-page__reason-body">
            <Input.TextArea
              autoComplete="off"
              placeholder={reasonModalType === 'reject' ? '请输入拒绝原因' : '请输入取消原因'}
              rows={4}
              value={reasonText}
              onChange={(e) => setReasonText(e.target.value)}
              maxLength={200}
              showCount
            />
          </div>
          <div className="orders-page__reason-actions">
            <Button onClick={() => setReasonModalOpen(false)}>取消</Button>
            <Button
              loading={reasonSubmitting}
              onClick={() => void handleSubmitReason()}
              type="primary"
            >
              确认
            </Button>
          </div>
        </Modal>
      </section>
    </>
  )
}
