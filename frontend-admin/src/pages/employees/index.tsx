import { Icon } from '@iconify/react'
import type { ColumnsType } from 'antd/es/table'
import {
  deleteEmployee,
  EmployeeStatus,
  getEmployeePage,
  Sex,
  updateEmployeeStatus,
  type Employee,
} from '@/api/employee'
import EmployeeModal from './components/EmployeeModal'
import './index.scss'

const sexLabels: Record<Sex, string> = {
  [Sex.Female]: '女',
  [Sex.Male]: '男',
}

function getInitial(name: string) {
  return name.trim().slice(0, 1) || '员'
}

function getAvatarColor(name: string) {
  const colors = ['#ff8a00', '#4a90e2', '#18c964', '#9b6bff', '#ff5ab3', '#14b8a6']
  const hash = Array.from(name).reduce((sum, char) => sum + char.charCodeAt(0), 0)
  return colors[hash % colors.length]
}

function formatDate(dateText: string) {
  return dateText.slice(0, 10) || '--'
}

function maskPhone(phone: string) {
  if (phone.length !== 11) {
    return phone
  }
  return `${phone.slice(0, 3)}****${phone.slice(7)}`
}

function maskIdNumber(idNumber: string) {
  if (idNumber.length === 18) {
    return `${idNumber.slice(0, 6)}********${idNumber.slice(14)}`
  }
  if (idNumber.length === 15) {
    return `${idNumber.slice(0, 6)}*****${idNumber.slice(11)}`
  }
  return idNumber
}

export default function EmployeesPage() {
  const [messageApi, messageContextHolder] = message.useMessage()
  const [records, setRecords] = useState<Employee[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create')
  const [currentEmployeeId, setCurrentEmployeeId] = useState<number | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)

  const loadEmployees = useCallback(async (page: number, size?: number) => {
    setLoading(true)
    try {
      const ps = size ?? pageSize
      const response = await getEmployeePage({ page, pageSize: ps })
      setRecords(response.data.records)
      setTotal(response.data.total)
      setCurrentPage(response.data.pageNum || page)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '员工列表加载失败')
    } finally {
      setLoading(false)
    }
  }, [messageApi, pageSize])

  const fetchedRef = useRef(false)
  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    void loadEmployees(1)
  }, [loadEmployees])

  function openCreateModal() {
    setModalMode('create')
    setCurrentEmployeeId(null)
    setModalOpen(true)
  }

  function openEditModal(id: number) {
    setModalMode('edit')
    setCurrentEmployeeId(id)
    setModalOpen(true)
  }

  function closeModal() {
    setModalOpen(false)
  }

  function handleRefresh(resetPage: boolean) {
    void loadEmployees(resetPage ? 1 : currentPage, pageSize)
  }

  async function handleToggleStatus(employee: Employee) {
    try {
      const nextStatus =
        employee.status === EmployeeStatus.Enabled ? EmployeeStatus.Disabled : EmployeeStatus.Enabled
      await updateEmployeeStatus(employee.id, nextStatus)
      messageApi.success(nextStatus === EmployeeStatus.Enabled ? '员工已设置为在职' : '员工已设置为离职')
      void loadEmployees(currentPage, pageSize)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '员工状态更新失败')
    }
  }

  function handleDelete(employee: Employee) {
    Modal.confirm({
      title: '确认删除该员工？',
      content: `删除后员工「${employee.name}」的数据将被移除，此操作不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteEmployee(employee.id)
          messageApi.success('员工已删除')
          void loadEmployees(currentPage, pageSize)
        } catch (error) {
          messageApi.error(error instanceof Error ? error.message : '员工删除失败')
        }
      },
    })
  }

  const columns: ColumnsType<Employee> = [
    {
      title: '员工',
      dataIndex: 'name',
      key: 'name',
      width: 240,
      render: (_, record) => (
        <div className="employees-page__employee-cell">
          <span
            className="employees-page__employee-avatar"
            style={{ backgroundColor: getAvatarColor(record.name) }}
          >
            {getInitial(record.name)}
          </span>
          <div className="employees-page__employee-meta">
            <span className="employees-page__employee-name">{record.name}</span>
            <span className="employees-page__employee-username">{record.username}</span>
          </div>
        </div>
      ),
    },
    {
      title: '职业',
      dataIndex: 'job',
      key: 'job',
      width: 160,
      render: (value: string) => <span>{value || '--'}</span>,
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      key: 'phone',
      width: 180,
      render: (value: string) => <span>{maskPhone(value)}</span>,
    },
    {
      title: '身份证号',
      dataIndex: 'idNumber',
      key: 'idNumber',
      width: 220,
      render: (value: string) => <span>{maskIdNumber(value)}</span>,
    },
    {
      title: '入职日期',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160,
      render: (value: string) => <span>{formatDate(value)}</span>,
    },
    {
      title: '性别',
      dataIndex: 'sex',
      key: 'sex',
      width: 120,
      render: (value: Sex) => (
        <Tag className="employees-page__tag employees-page__tag--sex" bordered={false}>
          {sexLabels[value]}
        </Tag>
      ),
    },
    {
      title: '地址',
      dataIndex: 'address',
      key: 'address',
      width: 200,
      render: (value: string) => <span>{value || '--'}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (value: EmployeeStatus) => (
        <Tag
          className={`employees-page__tag ${
            value === EmployeeStatus.Enabled
              ? 'employees-page__tag--enabled'
              : 'employees-page__tag--resigned'
          }`}
          bordered={false}
        >
          {value === EmployeeStatus.Enabled ? '在职' : '离职'}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size={8}>
          <Button
            aria-label={`编辑${record.name}`}
            className="employees-page__icon-button"
            icon={<Icon icon="lucide:square-pen" />}
            onClick={() => openEditModal(record.id)}
            type="text"
          />
          <Popconfirm
            cancelText="取消"
            okButtonProps={{ danger: true }}
            okText={record.status === EmployeeStatus.Enabled ? '确认离职' : '确认在职'}
            title={record.status === EmployeeStatus.Enabled ? '确认将该员工设为离职？' : '确认将该员工设为在职？'}
            description={
              record.status === EmployeeStatus.Enabled
                ? '离职后该员工将无法继续登录系统。'
                : '在职后该员工将重新获得系统使用权限。'
            }
            onConfirm={() => handleToggleStatus(record)}
          >
            <Button
              aria-label={
                record.status === EmployeeStatus.Enabled ? `离职${record.name}` : `在职${record.name}`
              }
              className="employees-page__icon-button"
              icon={
                <Icon
                  icon={
                    record.status === EmployeeStatus.Enabled ? 'lucide:ban' : 'lucide:check-circle'
                  }
                />
              }
              type="text"
            />
          </Popconfirm>
          <Button
            aria-label={`删除${record.name}`}
            className="employees-page__icon-button"
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
      <section className="employees-page">
        <div className="employees-page__card">
          <div className="employees-page__header">
            <Button
              className="employees-page__create-button"
              icon={<Icon icon="lucide:plus" />}
              onClick={openCreateModal}
              type="primary"
            >
              新增员工
            </Button>
          </div>

          <Spin spinning={loading}>
            <Table<Employee>
              className="employees-page__table"
              columns={columns}
              dataSource={records}
              pagination={{
                current: currentPage,
                pageSize,
                total,
                onChange: (page, size) => {
                  setPageSize(size)
                  void loadEmployees(page, size)
                },
                pageSizeOptions: ['10', '20', '50'],
                showSizeChanger: true,
                showTotal: (total) => `共 ${Math.ceil(total / pageSize)} 页 / ${total} 条`,
              }}
              rowKey="id"
              scroll={{ x: 980 }}
            />
          </Spin>
        </div>

        <EmployeeModal
          open={modalOpen}
          mode={modalMode}
          currentEmployeeId={currentEmployeeId}
          records={records}
          onClose={closeModal}
          onRefresh={handleRefresh}
        />
      </section>
    </>
  )
}
