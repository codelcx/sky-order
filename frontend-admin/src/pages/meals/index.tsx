import { Icon } from '@iconify/react'
import { Image, Input, Select } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import {
  deleteSetmeal,
  getSetmealPage,
  SetmealStatus,
  updateSetmealStatus,
  type Setmeal,
} from '@/api/setmeal'
import { CategoryType, getCategoryPage } from '@/api/category'
import { useTableScroll } from '@/hooks/useTableScroll'
import SetmealModal from './components/SetmealModal'
import './index.scss'

const statusLabels: Record<SetmealStatus, string> = {
  [SetmealStatus.Disabled]: '停售',
  [SetmealStatus.Enabled]: '起售',
}

export default function MealsPage() {
  const [messageApi, messageContextHolder] = message.useMessage()
  const [records, setRecords] = useState<Setmeal[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create')
  const [currentId, setCurrentId] = useState<number | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)

  // search
  const [searchName, setSearchName] = useState('')
  const [searchCategoryId, setSearchCategoryId] = useState<number | undefined>()
  const [searchStatus, setSearchStatus] = useState<SetmealStatus | undefined>()
  const [categoryOptions, setCategoryOptions] = useState<{ label: string; value: number }[]>([])

  const { tableWrapRef, scrollY } = useTableScroll()
  const searchTimerRef = useRef<ReturnType<typeof setTimeout> | undefined>(undefined)

  useEffect(() => {
    getCategoryPage({ page: 1, pageSize: 999, type: CategoryType.Setmeal })
      .then((response) => {
        setCategoryOptions(
          response.data.records.map((item) => ({ label: item.name, value: item.id })),
        )
      })
      .catch(() => {})
  }, [])

  const loadSetmeals = useCallback(async (page: number, size?: number) => {
    setLoading(true)
    try {
      const ps = size ?? pageSize
      const response = await getSetmealPage({
        page,
        pageSize: ps,
        name: searchName || undefined,
        categoryId: searchCategoryId,
        status: searchStatus,
      })
      setRecords(response.data.records)
      setTotal(response.data.total)
      setCurrentPage(response.data.pageNum || page)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '套餐列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi, pageSize, searchName, searchCategoryId, searchStatus])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    void loadSetmeals(1)
  }, [loadSetmeals])

  useEffect(() => {
    if (fetchedRef.current) {
      if (searchTimerRef.current) clearTimeout(searchTimerRef.current)
      searchTimerRef.current = setTimeout(() => {
        void loadSetmeals(1)
      }, 300)
    }
    return () => {
      if (searchTimerRef.current) clearTimeout(searchTimerRef.current)
    }
  }, [searchName, searchCategoryId, searchStatus])

  function openCreateModal() {
    setModalMode('create')
    setCurrentId(null)
    setModalOpen(true)
  }

  function openEditModal(id: number) {
    setModalMode('edit')
    setCurrentId(id)
    setModalOpen(true)
  }

  function closeModal() {
    setModalOpen(false)
  }

  function handleRefresh() {
    void loadSetmeals(currentPage, pageSize)
  }

  function handleDelete(setmeal: Setmeal) {
    Modal.confirm({
      title: '确认删除该套餐？',
      content: `删除后套餐「${setmeal.name}」的数据将被移除，此操作不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteSetmeal([setmeal.id])
          messageApi.success('套餐已删除')
          void loadSetmeals(currentPage, pageSize)
        } catch (error) {
          messageApi.error(error instanceof Error ? error.message : '套餐删除失败')
        }
      },
    })
  }

  async function handleToggleStatus(setmeal: Setmeal) {
    try {
      const nextStatus =
        setmeal.status === SetmealStatus.Enabled ? SetmealStatus.Disabled : SetmealStatus.Enabled
      await updateSetmealStatus(setmeal.id, nextStatus)
      messageApi.success(nextStatus === SetmealStatus.Enabled ? '套餐已起售' : '套餐已停售')
      void loadSetmeals(currentPage, pageSize)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '套餐状态更新失败')
    }
  }

  const columns: ColumnsType<Setmeal> = [
    {
      title: '套餐名称',
      dataIndex: 'name',
      key: 'name',
      width: 280,
      render: (_, record) => (
        <div className="setmeals-page__name-cell">
          <div className="setmeals-page__thumb">
            <Image
              alt={record.name}
              fallback="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='48' height='48' viewBox='0 0 24 24' fill='none' stroke='%238C8C8C' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='3' width='18' height='18' rx='2' ry='2'/%3E%3Ccircle cx='8.5' cy='8.5' r='1.5'/%3E%3Cpolyline points='21 15 16 10 5 21'/%3E%3C/svg%3E"
              preview={false}
              src={record.image || undefined}
            />
          </div>
          <div className="setmeals-page__name-info">
            <span className="setmeals-page__name">{record.name}</span>
            {record.description && (
              <span className="setmeals-page__description">{record.description}</span>
            )}
          </div>
        </div>
      ),
    },
    {
      title: '分类',
      dataIndex: 'categoryName',
      key: 'categoryName',
      width: 120,
      align: 'center',
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      align: 'center',
      render: (value: number) => <span className="setmeals-page__price">¥{value.toFixed(2)}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      align: 'center',
      render: (value: SetmealStatus) => (
        <Tag
          className={`setmeals-page__tag ${
            value === SetmealStatus.Enabled
              ? 'setmeals-page__tag--enabled'
              : 'setmeals-page__tag--disabled'
          }`}
          variant="filled"
        >
          {statusLabels[value]}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'actions',
      width: 180,
      fixed: 'right',
      render: (_, record) => (
        <Space size={8}>
          <Popconfirm
            cancelText="取消"
            okButtonProps={{ danger: record.status === SetmealStatus.Enabled }}
            okText={record.status === SetmealStatus.Enabled ? '确认停售' : '确认起售'}
            title={record.status === SetmealStatus.Enabled ? '确认将该套餐停售？' : '确认将该套餐起售？'}
            onConfirm={() => handleToggleStatus(record)}
          >
            <Button
              aria-label={
                record.status === SetmealStatus.Enabled ? `停售${record.name}` : `起售${record.name}`
              }
              className="setmeals-page__icon-button"
              icon={
                <Icon
                  icon={
                    record.status === SetmealStatus.Enabled ? 'lucide:ban' : 'lucide:check-circle'
                  }
                />
              }
              type="text"
            />
          </Popconfirm>
          <Button
            aria-label={`编辑${record.name}`}
            className="setmeals-page__icon-button"
            icon={<Icon icon="lucide:square-pen" />}
            onClick={() => openEditModal(record.id)}
            type="text"
          />
          <Button
            aria-label={`删除${record.name}`}
            className="setmeals-page__icon-button"
            icon={<Icon icon="lucide:trash-2" />}
            onClick={() => handleDelete(record)}
            type="text"
          />
        </Space>
      ),
    },
  ]

  return (
    <>
      {messageContextHolder}
      <section className="setmeals-page">
        <div className="setmeals-page__card">
          <div className="setmeals-page__header">
            <div className="setmeals-page__search">
              <Input
                allowClear
                className="setmeals-page__search-input"
                placeholder="套餐名称"
                prefix={<Icon icon="lucide:search" />}
                value={searchName}
                onChange={(e) => setSearchName(e.target.value)}
              />
              <Select
                allowClear
                className="setmeals-page__search-select"
                options={categoryOptions}
                placeholder="分类"
                value={searchCategoryId}
                onChange={(value) => setSearchCategoryId(value)}
              />
              <Select
                allowClear
                className="setmeals-page__search-select"
                options={[
                  { label: '起售', value: SetmealStatus.Enabled },
                  { label: '停售', value: SetmealStatus.Disabled },
                ]}
                placeholder="状态"
                value={searchStatus}
                onChange={(value) => setSearchStatus(value)}
              />
              <Button
                className="setmeals-page__reset-button"
                icon={<Icon icon="lucide:rotate-ccw" />}
                onClick={() => {
                  setSearchName('')
                  setSearchCategoryId(undefined)
                  setSearchStatus(undefined)
                }}
              >
                重置
              </Button>
            </div>
            <Button
              className="setmeals-page__create-button"
              icon={<Icon icon="lucide:plus" />}
              onClick={openCreateModal}
              size="middle"
              type="primary"
            >
              新增套餐
            </Button>
          </div>

          <div ref={tableWrapRef} className="setmeals-page__table-wrap">
            <Spin spinning={loading}>
              <Table<Setmeal>
                className="setmeals-page__table"
                columns={columns}
                dataSource={records}
                pagination={{
                  current: currentPage,
                  pageSize,
                  total,
                  onChange: (page, size) => {
                    setPageSize(size)
                    void loadSetmeals(page, size)
                  },
                  pageSizeOptions: ['10', '20', '50'],
                  showSizeChanger: true,
                  showTotal: (total) => `共 ${Math.ceil(total / pageSize)} 页 / ${total} 条`,
                }}
                rowKey="id"
                scroll={{ y: scrollY, x: 800 }}
              />
            </Spin>
          </div>
        </div>

        <SetmealModal
          open={modalOpen}
          mode={modalMode}
          currentId={currentId}
          afterClose={closeModal}
          onRefresh={handleRefresh}
        />
      </section>
    </>
  )
}
