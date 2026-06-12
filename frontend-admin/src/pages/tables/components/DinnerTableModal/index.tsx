import { Icon } from '@iconify/react'
import {
  createDinnerTable,
  DinnerTableStatus,
  getDinnerTableById,
  updateDinnerTable,
  updateDinnerTableStatus,
  type CreateDinnerTableBody,
  type UpdateDinnerTableBody,
} from '@/api/dinner-table'

interface DinnerTableModalProps {
  open: boolean
  mode: 'create' | 'edit'
  currentTableId: number | null
  onClose: () => void
  onRefresh: () => void
}

const statusLabels: Record<DinnerTableStatus, string> = {
  [DinnerTableStatus.Idle]: '空闲',
  [DinnerTableStatus.InUse]: '使用中',
  [DinnerTableStatus.Reserved]: '已预订',
}

interface DinnerTableFormValues extends CreateDinnerTableBody {
  status?: DinnerTableStatus
}

export default function DinnerTableModal(props: DinnerTableModalProps) {
  const { open, mode, currentTableId, onClose, onRefresh } = props
  const [messageApi, messageContextHolder] = message.useMessage()
  const [form] = Form.useForm<DinnerTableFormValues>()
  const [submitLoading, setSubmitLoading] = useState(false)

  const titleText = mode === 'create' ? '新增餐桌' : '编辑餐桌'

  useEffect(() => {
    if (!open) { return }

    form.resetFields()

    if (mode === 'edit' && currentTableId !== null) {
      getDinnerTableById(currentTableId)
        .then((response) => {
          const table = response.data
          form.setFieldsValue({
            tableNumber: table.tableNumber,
            capacity: table.capacity,
            status: table.status,
          })
        })
        .catch((error) => {
          messageApi.error(error instanceof Error ? error.message : '餐桌信息加载失败')
        })
    }
  }, [open, mode, currentTableId, form, messageApi])

  function handleCancel() {
    if (submitLoading) { return }
    onClose()
  }

  async function handleSubmit() {
    try {
      const values = await form.validateFields()
      setSubmitLoading(true)

      if (mode === 'create') {
        await createDinnerTable(values)
        messageApi.success('餐桌新增成功')
      } else if (currentTableId !== null) {
        const { status, ...rest } = values
        const payload: UpdateDinnerTableBody = { id: currentTableId, ...rest }
        await updateDinnerTable(payload)
        if (status !== undefined) {
          await updateDinnerTableStatus(currentTableId, status)
        }
        messageApi.success('餐桌信息已更新')
      }

      onRefresh()
      onClose()
    } catch (error) {
      if (error instanceof Error) {
        messageApi.error(error.message)
      }
    } finally {
      setSubmitLoading(false)
    }
  }

  return (
    <>
      {messageContextHolder}
      <Modal
        centered
        className="tables-page__modal"
        closeIcon={<Icon icon="lucide:x" />}
        destroyOnHidden
        footer={null}
        onCancel={handleCancel}
        open={open}
        title={titleText}
        width={480}
        wrapClassName="tables-page__modal-wrap"
      >
        <div className="tables-page__modal-body">
          <Form<DinnerTableFormValues>
            className="tables-page__form"
            form={form}
            layout="vertical"
          >
            <Form.Item
              label="桌号"
              name="tableNumber"
              rules={[
                { required: true, message: '请输入桌号' },
                { type: 'number', min: 1, message: '桌号必须大于 0' },
              ]}
            >
              <InputNumber
                autoComplete="off"
                className="tables-page__number-input"
                min={1}
                placeholder="请输入桌号"
                precision={0}
              />
            </Form.Item>

            <Form.Item
              label="座位数"
              name="capacity"
              rules={[
                { required: true, message: '请输入座位数' },
                { type: 'number', min: 1, message: '座位数必须大于 0' },
              ]}
            >
              <InputNumber
                autoComplete="off"
                className="tables-page__number-input"
                min={1}
                placeholder="请输入座位数"
                precision={0}
              />
            </Form.Item>

            {mode === 'edit' && (
              <Form.Item label="状态" name="status">
                <Select placeholder="请选择状态">
                  <Select.Option value={DinnerTableStatus.Idle}>{statusLabels[DinnerTableStatus.Idle]}</Select.Option>
                  <Select.Option value={DinnerTableStatus.InUse}>{statusLabels[DinnerTableStatus.InUse]}</Select.Option>
                  <Select.Option value={DinnerTableStatus.Reserved}>{statusLabels[DinnerTableStatus.Reserved]}</Select.Option>
                </Select>
              </Form.Item>
            )}
          </Form>
        </div>

        <div className="tables-page__modal-actions">
          <Button className="tables-page__secondary-button" onClick={handleCancel}>
            取消
          </Button>
          <Button
            className="tables-page__primary-button"
            loading={submitLoading}
            onClick={() => void handleSubmit()}
            type="primary"
          >
            保存
          </Button>
        </div>
      </Modal>
    </>
  )
}
