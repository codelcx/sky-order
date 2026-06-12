import { Icon } from '@iconify/react'
import { Image, Input, Select } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import {
  deleteDish,
  DishStatus,
  getDishPage,
  updateDishStatus,
  type Dish,
} from '@/api/dish'
import { CategoryType, getCategoryPage, type Category } from '@/api/category'
import { useTableScroll } from '@/hooks/useTableScroll'
import DishModal from './components/DishModal'
import './index.scss'

const statusLabels: Record<DishStatus, string> = {
  [DishStatus.Disabled]: '停售',
  [DishStatus.Enabled]: '起售',
}

export default function DishesPage() {
  const [messageApi, messageContextHolder] = message.useMessage()
  const [records, setRecords] = useState<Dish[]>([])
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
  const [searchStatus, setSearchStatus] = useState<DishStatus | undefined>()
  const [categoryOptions, setCategoryOptions] = useState<{ label: string; value: number }[]>([])

  const { tableWrapRef, scrollY } = useTableScroll()
  const searchTimerRef = useRef<ReturnType<typeof setTimeout> | undefined>(undefined)

  useEffect(() => {
    getCategoryPage({ page: 1, pageSize: 999 })
      .then((response) => {
        setCategoryOptions(
          response.data.records
            .filter((item: Category) => item.type === CategoryType.Dish)
            .map((item: Category) => ({ label: item.name, value: item.id })),
        )
      })
      .catch(() => {})
  }, [])

  const loadDishes = useCallback(async (page: number, size?: number) => {
    setLoading(true)
    try {
      const ps = size ?? pageSize
      const response = await getDishPage({
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
      messageApi.error(error instanceof Error ? error.message : '菜品列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi, pageSize, searchName, searchCategoryId, searchStatus])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    void loadDishes(1)
  }, [loadDishes])

  useEffect(() => {
    if (fetchedRef.current) {
      if (searchTimerRef.current) clearTimeout(searchTimerRef.current)
      searchTimerRef.current = setTimeout(() => {
        void loadDishes(1)
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
    void loadDishes(currentPage, pageSize)
  }

  function handleDelete(dish: Dish) {
    Modal.confirm({
      title: '确认删除该菜品？',
      content: `删除后菜品「${dish.name}」的数据将被移除，此操作不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteDish([dish.id])
          messageApi.success('菜品已删除')
          void loadDishes(currentPage, pageSize)
        } catch (error) {
          messageApi.error(error instanceof Error ? error.message : '菜品删除失败')
        }
      },
    })
  }

  async function handleToggleStatus(dish: Dish) {
    try {
      const nextStatus =
        dish.status === DishStatus.Enabled ? DishStatus.Disabled : DishStatus.Enabled
      await updateDishStatus(dish.id, nextStatus)
      messageApi.success(nextStatus === DishStatus.Enabled ? '菜品已起售' : '菜品已停售')
      void loadDishes(currentPage, pageSize)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '菜品状态更新失败')
    }
  }

  const columns: ColumnsType<Dish> = [
    {
      title: '菜品名称',
      dataIndex: 'name',
      key: 'name',
      width: 280,
      render: (_, record) => (
        <div className="dishes-page__name-cell">
          <div className="dishes-page__thumb">
            <Image
              alt={record.name}
              fallback="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='48' height='48' viewBox='0 0 24 24' fill='none' stroke='%238C8C8C' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='3' width='18' height='18' rx='2' ry='2'/%3E%3Ccircle cx='8.5' cy='8.5' r='1.5'/%3E%3Cpolyline points='21 15 16 10 5 21'/%3E%3C/svg%3E"
              preview={false}
              src={record.image || undefined}
            />
          </div>
          <div className="dishes-page__name-info">
            <span className="dishes-page__name">{record.name}</span>
            {record.description && (
              <span className="dishes-page__description">{record.description}</span>
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
      render: (value: number) => <span className="dishes-page__price">¥{value.toFixed(2)}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      align: 'center',
      render: (value: DishStatus) => (
        <Tag
          className={`dishes-page__tag ${
            value === DishStatus.Enabled
              ? 'dishes-page__tag--enabled'
              : 'dishes-page__tag--disabled'
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
            okButtonProps={{ danger: record.status === DishStatus.Enabled }}
            okText={record.status === DishStatus.Enabled ? '确认停售' : '确认起售'}
            title={record.status === DishStatus.Enabled ? '确认将该菜品停售？' : '确认将该菜品起售？'}
            onConfirm={() => handleToggleStatus(record)}
          >
            <Button
              aria-label={
                record.status === DishStatus.Enabled ? `停售${record.name}` : `起售${record.name}`
              }
              className="dishes-page__icon-button"
              icon={
                <Icon
                  icon={
                    record.status === DishStatus.Enabled ? 'lucide:ban' : 'lucide:check-circle'
                  }
                />
              }
              type="text"
            />
          </Popconfirm>
          <Button
            aria-label={`编辑${record.name}`}
            className="dishes-page__icon-button"
            icon={<Icon icon="lucide:square-pen" />}
            onClick={() => openEditModal(record.id)}
            type="text"
          />
          <Button
            aria-label={`删除${record.name}`}
            className="dishes-page__icon-button"
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
      <section className="dishes-page">
        <div className="dishes-page__card">
          <div className="dishes-page__header">
            <div className="dishes-page__search">
              <Input
                allowClear
                className="dishes-page__search-input"
                placeholder="菜品名称"
                prefix={<Icon icon="lucide:search" />}
                value={searchName}
                onChange={(e) => setSearchName(e.target.value)}
              />
              <Select
                allowClear
                className="dishes-page__search-select"
                options={categoryOptions}
                placeholder="分类"
                value={searchCategoryId}
                onChange={(value) => setSearchCategoryId(value)}
              />
              <Select
                allowClear
                className="dishes-page__search-select"
                options={[
                  { label: '起售', value: DishStatus.Enabled },
                  { label: '停售', value: DishStatus.Disabled },
                ]}
                placeholder="状态"
                value={searchStatus}
                onChange={(value) => setSearchStatus(value)}
              />
              <Button
                className="dishes-page__reset-button"
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
              className="dishes-page__create-button"
              icon={<Icon icon="lucide:plus" />}
              onClick={openCreateModal}
              size="middle"
              type="primary"
            >
              新增菜品
            </Button>
          </div>

          <div ref={tableWrapRef} className="dishes-page__table-wrap">
            <Spin spinning={loading}>
              <Table<Dish>
                className="dishes-page__table"
                columns={columns}
                dataSource={records}
                pagination={{
                  current: currentPage,
                  pageSize,
                  total,
                  onChange: (page, size) => {
                    setPageSize(size)
                    void loadDishes(page, size)
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

        <DishModal
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
