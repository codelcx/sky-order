import { Icon } from '@iconify/react'
import { getDinnerTableById, getDinnerTablePage, DinnerTableStatus } from '@/api/dinner-table'
import type { DinnerTable } from '@/api/dinner-table'
import DinnerTableModal from './components/DinnerTableModal'
import './index.scss'

const statusLabels: Record<DinnerTableStatus, string> = {
  [DinnerTableStatus.Idle]: '空闲',
  [DinnerTableStatus.InUse]: '使用中',
  [DinnerTableStatus.Reserved]: '已预订',
}

const statusColors: Record<DinnerTableStatus, string> = {
  [DinnerTableStatus.Idle]: '#18c964',
  [DinnerTableStatus.InUse]: '#1890ff',
  [DinnerTableStatus.Reserved]: '#ff8a00',
}

export default function TablesPage() {
  const [messageApi, messageContextHolder] = message.useMessage()
  const [records, setRecords] = useState<DinnerTable[]>([])
  const [loading, setLoading] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create')
  const [currentTableId, setCurrentTableId] = useState<number | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [searchTableNumber, setSearchTableNumber] = useState<number | undefined>(undefined)
  const [searchStatus, setSearchStatus] = useState<DinnerTableStatus | undefined>(undefined)
  const searchTimerRef = useRef<ReturnType<typeof setTimeout>>()

  const loadTables = useCallback(async (page: number, tableNumber?: number, status?: DinnerTableStatus) => {
    setLoading(true)
    try {
      const response = await getDinnerTablePage({
        page,
        pageSize: 50,
        tableNumber,
        status,
      })
      setRecords(response.data.records)
      setCurrentPage(response.data.pageNum || page)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '餐桌列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    void loadTables(1)
  }, [loadTables])

  useEffect(() => {
    if (fetchedRef.current) {
      if (searchTimerRef.current) { clearTimeout(searchTimerRef.current) }
      searchTimerRef.current = setTimeout(() => {
        void loadTables(1, searchTableNumber, searchStatus)
      }, 300)
    }
    return () => {
      if (searchTimerRef.current) { clearTimeout(searchTimerRef.current) }
    }
  }, [searchTableNumber, searchStatus, loadTables])

  function openCreateModal() {
    setModalMode('create')
    setCurrentTableId(null)
    setModalOpen(true)
  }

  function openEditModal(id: number) {
    setModalMode('edit')
    setCurrentTableId(id)
    setModalOpen(true)
  }

  function closeModal() {
    setModalOpen(false)
  }

  function handleRefresh() {
    void loadTables(currentPage, searchTableNumber, searchStatus)
  }

  function handleDownloadQr(table: DinnerTable) {
    getDinnerTableById(table.id)
      .then((res) => {
        const url = res.data.qrCodeUrl
        if (!url) {
          messageApi.warning('暂无二维码')
          return
        }
        const link = document.createElement('a')
        link.href = url
        link.download = `${table.tableNumber}号桌二维码.png`
        link.click()
      })
      .catch((error) => {
        messageApi.error(error instanceof Error ? error.message : '二维码下载失败')
      })
  }

  function renderCard(table: DinnerTable) {
    return (
      <div className="tables-page__card" key={table.id}>
        <div className="tables-page__card-header">
          <span className="tables-page__card-number">{table.tableNumber}号桌</span>
          <span
            className="tables-page__status"
            style={{ color: statusColors[table.status] }}
          >
            <span
              className="tables-page__status-dot"
              style={{ backgroundColor: statusColors[table.status] }}
            />
            {statusLabels[table.status]}
          </span>
        </div>

        <div className="tables-page__card-body">
          <div className="tables-page__card-info">
            <span>容纳{table.capacity}人</span>
            <span className="tables-page__card-dine-in">堂食</span>
          </div>

          {table.qrCodeUrl && (
            <div className="tables-page__card-qr">
              <img
                alt={`${table.tableNumber}号桌二维码`}
                className="tables-page__qr-image"
                src={table.qrCodeUrl}
              />
            </div>
          )}
        </div>

        <div className="tables-page__card-actions">
          <Button
            className="tables-page__action-btn"
            icon={<Icon icon="lucide:square-pen" />}
            onClick={() => openEditModal(table.id)}
            size="small"
            type="text"
          >
            编辑
          </Button>
          <Button
            className="tables-page__action-btn"
            icon={<Icon icon="lucide:download" />}
            onClick={() => handleDownloadQr(table)}
            size="small"
            type="text"
          >
            下载
          </Button>
        </div>
      </div>
    )
  }

  return (
    <>
      {messageContextHolder}
      <section className="tables-page">
        <div className="tables-page__card-container">
          <div className="tables-page__header">
            <div className="tables-page__filters">
              <InputNumber
                className="tables-page__filter-input"
                min={1}
                placeholder="桌号"
                precision={0}
                value={searchTableNumber}
                onChange={(value) => setSearchTableNumber(value ?? undefined)}
              />
              <Select
                allowClear
                className="tables-page__filter-select"
                onChange={(value) => setSearchStatus(value ?? undefined)}
                placeholder="状态"
                value={searchStatus}
              >
                <Select.Option value={DinnerTableStatus.Idle}>空闲</Select.Option>
                <Select.Option value={DinnerTableStatus.InUse}>使用中</Select.Option>
                <Select.Option value={DinnerTableStatus.Reserved}>已预订</Select.Option>
              </Select>
            </div>
            <Button
              className="tables-page__create-button"
              icon={<Icon icon="lucide:plus" />}
              onClick={openCreateModal}
              type="primary"
            >
              新增餐桌
            </Button>
          </div>

          <Spin spinning={loading}>
            {records.length > 0 ? (
              <div className="tables-page__grid">{records.map(renderCard)}</div>
            ) : (
              !loading && (
                <div className="tables-page__empty">
                  <Icon icon="lucide:utensils-crossed" />
                  <span>暂无餐桌数据</span>
                </div>
              )
            )}
          </Spin>
        </div>

        <DinnerTableModal
          currentTableId={currentTableId}
          mode={modalMode}
          onClose={closeModal}
          onRefresh={handleRefresh}
          open={modalOpen}
        />
      </section>
    </>
  )
}
