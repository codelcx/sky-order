import { Icon } from '@iconify/react'
import type { ColumnsType } from 'antd/es/table'
import {
  CategoryStatus,
  CategoryType,
  deleteCategory,
  getCategoryPage,
  type Category,
} from '@/api/category'
import { useTableScroll } from '@/hooks/useTableScroll'
import CategoryModal from './components/CategoryModal'
import './index.scss'

const statusLabels: Record<CategoryStatus, string> = {
  [CategoryStatus.Disabled]: '禁用',
  [CategoryStatus.Enabled]: '启用',
}

export default function CategoriesPage() {
  const [messageApi, messageContextHolder] = message.useMessage()
  const [records, setRecords] = useState<Category[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create')
  const [currentId, setCurrentId] = useState<number | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const { tableWrapRef, scrollY } = useTableScroll()

  const loadCategories = useCallback(async (page: number, size?: number) => {
    setLoading(true)
    try {
      const ps = size ?? pageSize
      const response = await getCategoryPage({ page, pageSize: ps })
      setRecords(response.data.records)
      setTotal(response.data.total)
      setCurrentPage(response.data.pageNum || page)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '分类列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi, pageSize])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    void loadCategories(1)
  }, [loadCategories])

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
    void loadCategories(currentPage, pageSize)
  }

  function handleDelete(category: Category) {
    Modal.confirm({
      title: '确认删除该分类？',
      content: `删除后分类「${category.name}」的数据将被移除，此操作不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteCategory(category.id)
          messageApi.success('分类已删除')
          void loadCategories(currentPage, pageSize)
        } catch (error) {
          messageApi.error(error instanceof Error ? error.message : '分类删除失败')
        }
      },
    })
  }

  const columns: ColumnsType<Category> = [
    {
      title: '分类名称',
      dataIndex: 'name',
      key: 'name',
      width: 300,
      render: (_, record) => {
        const isDish = record.type === CategoryType.Dish
        const count = isDish ? record.dishCount : record.setmealCount
        const unit = isDish ? '道菜品' : '种套餐'
        const emptyText = isDish ? '暂无关联菜品' : '暂无关联套餐'
        const countsText = count > 0 ? `共${count}${unit}` : emptyText

        return (
          <div className="categories-page__name-cell">
            <div className="categories-page__name-row">
              <span className="categories-page__name">{record.name}</span>
              <Tag
                className={`categories-page__type-tag ${
                  isDish ? 'categories-page__type-tag--dish' : 'categories-page__type-tag--setmeal'
                }`}
                bordered={false}
              >
                {isDish ? '菜品' : '套餐'}
              </Tag>
            </div>
            <span className="categories-page__name-counts">{countsText}</span>
          </div>
        )
      },
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 120,
      align: 'center',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      align: 'center',
      render: (value: CategoryStatus) => (
        <Tag
          className={`categories-page__tag ${
            value === CategoryStatus.Enabled
              ? 'categories-page__tag--enabled'
              : 'categories-page__tag--disabled'
          }`}
          bordered={false}
        >
          {statusLabels[value]}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'actions',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space size={8}>
          <Button
            aria-label={`编辑${record.name}`}
            className="categories-page__icon-button"
            icon={<Icon icon="lucide:square-pen" />}
            onClick={() => openEditModal(record.id)}
            type="text"
          />
          <Button
            aria-label={`删除${record.name}`}
            className="categories-page__icon-button"
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
      <section className="categories-page">
        <div className="categories-page__card">
          <div className="categories-page__header">
            <Button
              className="categories-page__create-button"
              icon={<Icon icon="lucide:plus" />}
              onClick={openCreateModal}
              type="primary"
            >
              新增分类
            </Button>
          </div>

          <div ref={tableWrapRef} className="categories-page__table-wrap">
            <Spin spinning={loading}>
              <Table<Category>
                className="categories-page__table"
                columns={columns}
                dataSource={records}
                pagination={{
                  current: currentPage,
                  pageSize,
                  total,
                  onChange: (page, size) => {
                    setPageSize(size)
                    void loadCategories(page, size)
                  },
                  pageSizeOptions: ['10', '20', '50'],
                  showSizeChanger: true,
                  showTotal: (total) => `共 ${Math.ceil(total / pageSize)} 页 / ${total} 条`,
                }}
                rowKey="id"
                scroll={{ y: scrollY, x: 620 }}
              />
            </Spin>
          </div>
        </div>

        <CategoryModal
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
