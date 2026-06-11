import { Icon } from '@iconify/react'
import { useNavigate } from 'react-router-dom'
import type { ColumnsType } from 'antd/es/table'
import {
  deleteDictType,
  DictStatus,
  getDictTypePage,
  updateDictTypeStatus,
  type DictType,
} from '@/api/dict'
import { useTableScroll } from '@/hooks/useTableScroll'
import DictTypeModal from './components/DictTypeModal'
import './index.scss'

const statusLabels: Record<DictStatus, string> = {
  [DictStatus.Disabled]: '禁用',
  [DictStatus.Enabled]: '启用',
}

export default function DictPage() {
  const navigate = useNavigate()
  const [messageApi, messageContextHolder] = message.useMessage()
  const [records, setRecords] = useState<DictType[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create')
  const [currentId, setCurrentId] = useState<number | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const { tableWrapRef, scrollY } = useTableScroll()

  const loadDictTypes = useCallback(async (page: number, size?: number) => {
    setLoading(true)
    try {
      const ps = size ?? pageSize
      const response = await getDictTypePage({ page, pageSize: ps })
      setRecords(response.data.records)
      setTotal(response.data.total)
      setCurrentPage(response.data.pageNum || page)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '字典类型列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi, pageSize])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    void loadDictTypes(1)
  }, [loadDictTypes])

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
    void loadDictTypes(currentPage, pageSize)
  }

  function handleDelete(dictType: DictType) {
    Modal.confirm({
      title: '确认删除该字典类型？',
      content: `删除后字典类型「${dictType.name}」及其所有字典数据将被移除，此操作不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteDictType(dictType.id)
          messageApi.success('字典类型已删除')
          void loadDictTypes(currentPage, pageSize)
        } catch (error) {
          messageApi.error(error instanceof Error ? error.message : '字典类型删除失败')
        }
      },
    })
  }

  function navigateToData(dictType: DictType) {
    navigate(`/dict/${dictType.id}`)
  }

  async function handleToggleStatus(dictType: DictType) {
    try {
      const nextStatus =
        dictType.status === DictStatus.Enabled ? DictStatus.Disabled : DictStatus.Enabled
      await updateDictTypeStatus(dictType.id, nextStatus)
      messageApi.success(nextStatus === DictStatus.Enabled ? '字典类型已启用' : '字典类型已禁用')
      void loadDictTypes(currentPage, pageSize)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '字典类型状态更新失败')
    }
  }

  const columns: ColumnsType<DictType> = [
    {
      title: '字典名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
      render: (value: string) => (
        <span className="dict-page__type-name">{value}</span>
      ),
    },
    {
      title: '字典编码',
      dataIndex: 'code',
      key: 'code',
      width: 200,
      render: (value: string) => (
        <span className="dict-page__type-code">{value}</span>
      ),
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
      render: (value: DictStatus) => (
        <Tag
          className={`dict-page__tag ${
            value === DictStatus.Enabled
              ? 'dict-page__tag--enabled'
              : 'dict-page__tag--disabled'
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
      width: 260,
      fixed: 'right',
      render: (_, record) => (
        <Space size={8}>
          <Button
            aria-label={`查看${record.name}的字典数据`}
            className="dict-page__icon-button"
            icon={<Icon icon="lucide:eye" />}
            onClick={() => navigateToData(record)}
            type="text"
          />
          <Popconfirm
            cancelText="取消"
            okButtonProps={{ danger: record.status === DictStatus.Enabled }}
            okText={record.status === DictStatus.Enabled ? '确认禁用' : '确认启用'}
            title={record.status === DictStatus.Enabled ? '确认禁用该字典类型？' : '确认启用该字典类型？'}
            onConfirm={() => handleToggleStatus(record)}
          >
            <Button
              aria-label={
                record.status === DictStatus.Enabled ? `禁用${record.name}` : `启用${record.name}`
              }
              className="dict-page__icon-button"
              icon={
                <Icon
                  icon={
                    record.status === DictStatus.Enabled ? 'lucide:ban' : 'lucide:check-circle'
                  }
                />
              }
              type="text"
            />
          </Popconfirm>
          <Button
            aria-label={`编辑${record.name}`}
            className="dict-page__icon-button"
            icon={<Icon icon="lucide:square-pen" />}
            onClick={() => openEditModal(record.id)}
            type="text"
          />
          <Button
            aria-label={`删除${record.name}`}
            className="dict-page__icon-button"
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
      <section className="dict-page">
        <div className="dict-page__card">
          <div className="dict-page__header">
            <Button
              className="dict-page__create-button"
              icon={<Icon icon="lucide:plus" />}
              onClick={openCreateModal}
              type="primary"
            >
              新增字典
            </Button>
          </div>

          <div ref={tableWrapRef} className="dict-page__table-wrap">
            <Spin spinning={loading}>
              <Table<DictType>
                className="dict-page__table"
                columns={columns}
                dataSource={records}
                pagination={{
                  current: currentPage,
                  pageSize,
                  total,
                  onChange: (page, size) => {
                    setPageSize(size)
                    void loadDictTypes(page, size)
                  },
                  pageSizeOptions: ['10', '20', '50'],
                  showSizeChanger: true,
                  showTotal: (total) => `共 ${Math.ceil(total / pageSize)} 页 / ${total} 条`,
                }}
                rowKey="id"
                scroll={{ y: scrollY, x: 760 }}
              />
            </Spin>
          </div>
        </div>

        <DictTypeModal
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
