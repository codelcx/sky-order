import { Icon } from '@iconify/react'
import { useNavigate, useParams } from 'react-router-dom'
import type { ColumnsType } from 'antd/es/table'
import {
  deleteDictData,
  DictStatus,
  getDictDataPage,
  getDictTypeById,
  type DictData,
  type DictType,
} from '@/api/dict'
import { useTableScroll } from '@/hooks/useTableScroll'
import DictDataModal from '../components/DictDataModal'
import '../index.scss'

const statusLabels: Record<DictStatus, string> = {
  [DictStatus.Disabled]: '禁用',
  [DictStatus.Enabled]: '启用',
}

export default function DictDataPage() {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const dictTypeId = Number(id)
  const [messageApi, messageContextHolder] = message.useMessage()
  const [dictType, setDictType] = useState<DictType | null>(null)
  const [records, setRecords] = useState<DictData[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create')
  const [currentDataId, setCurrentDataId] = useState<number | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const { tableWrapRef, scrollY } = useTableScroll()

  const loadDictData = useCallback(async (code: string, page: number, size?: number) => {
    setLoading(true)
    try {
      const ps = size ?? pageSize
      const response = await getDictDataPage({ dictTypeCode: code, page, pageSize: ps })
      setRecords(response.data.records)
      setTotal(response.data.total)
      setCurrentPage(response.data.pageNum || page)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '字典数据列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi, pageSize])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true

    if (!dictTypeId || Number.isNaN(dictTypeId)) {
      messageApi.error('无效的字典类型 ID')
      return
    }

    getDictTypeById(dictTypeId)
      .then((response) => {
        setDictType(response.data)
        return loadDictData(response.data.code, 1)
      })
      .catch((error) => {
        messageApi.error(error instanceof Error ? error.message : '字典数据加载失败')
      })
  }, [dictTypeId, messageApi, loadDictData])

  function openCreateModal() {
    setModalMode('create')
    setCurrentDataId(null)
    setModalOpen(true)
  }

  function openEditModal(id: number) {
    setModalMode('edit')
    setCurrentDataId(id)
    setModalOpen(true)
  }

  function closeModal() {
    setModalOpen(false)
  }

  function handleRefresh() {
    if (dictType) {
      void loadDictData(dictType.code, currentPage, pageSize)
    }
  }

  function handleDelete(data: DictData) {
    Modal.confirm({
      title: '确认删除该字典数据？',
      content: `删除后字典数据「${data.label}」将被移除，此操作不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteDictData(data.id)
          messageApi.success('字典数据已删除')
          if (dictType) {
            void loadDictData(dictType.code, currentPage, pageSize)
          }
        } catch (error) {
          messageApi.error(error instanceof Error ? error.message : '字典数据删除失败')
        }
      },
    })
  }

  const columns: ColumnsType<DictData> = [
    {
      title: '标签',
      dataIndex: 'label',
      key: 'label',
      width: 200,
    },
    {
      title: '键值',
      dataIndex: 'value',
      key: 'value',
      width: 160,
      align: 'center',
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 100,
      align: 'center',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      align: 'center',
      render: (value: DictStatus) => (
        <Tag
          className={`dict-page__tag ${
            value === DictStatus.Enabled
              ? 'dict-page__tag--enabled'
              : 'dict-page__tag--disabled'
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
      width: 160,
      fixed: 'right',
      render: (_, record) => (
        <Space size={8}>
          <Button
            aria-label={`编辑${record.label}`}
            className="dict-page__icon-button"
            icon={<Icon icon="lucide:square-pen" />}
            onClick={() => openEditModal(record.id)}
            type="text"
          />
          <Button
            aria-label={`删除${record.label}`}
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
          <div className="dict-page__data-header">
            <div className="dict-page__data-header-left">
              <Button
                className="dict-page__back-button"
                icon={<Icon icon="lucide:arrow-left" />}
                onClick={() => navigate('/dict')}
                type="text"
              >
                返回
              </Button>

              {dictType && (
                <div className="dict-page__data-header-info">
                  <span className="dict-page__data-header-name">{dictType.name}</span>
                  <span className="dict-page__data-header-code">{dictType.code}</span>
                  <Tag
                    className={`dict-page__tag ${
                      dictType.status === DictStatus.Enabled
                        ? 'dict-page__tag--enabled'
                        : 'dict-page__tag--disabled'
                    }`}
                    bordered={false}
                  >
                    {statusLabels[dictType.status]}
                  </Tag>
                </div>
              )}
            </div>

            <Button
              className="dict-page__create-button"
              icon={<Icon icon="lucide:plus" />}
              onClick={openCreateModal}
              type="primary"
            >
              新增数据
            </Button>
          </div>

          <div ref={tableWrapRef} className="dict-page__table-wrap">
            <Spin spinning={loading}>
              <Table<DictData>
                className="dict-page__table"
                columns={columns}
                dataSource={records}
                pagination={{
                  current: currentPage,
                  pageSize,
                  total,
                  onChange: (page, size) => {
                    setPageSize(size)
                    if (dictType) {
                      void loadDictData(dictType.code, page, size)
                    }
                  },
                  pageSizeOptions: ['10', '20', '50'],
                  showSizeChanger: true,
                  showTotal: (total) => `共 ${Math.ceil(total / pageSize)} 页 / ${total} 条`,
                }}
                rowKey="id"
                scroll={{ y: scrollY, x: 960 }}
              />
            </Spin>
          </div>
        </div>

        <DictDataModal
          open={modalOpen}
          mode={modalMode}
          currentId={currentDataId}
          dictTypeId={dictTypeId}
          afterClose={closeModal}
          onRefresh={handleRefresh}
        />
      </section>
    </>
  )
}
