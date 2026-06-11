import { Icon } from '@iconify/react'
import {
  createDictType,
  getDictTypeById,
  updateDictType,
  DictStatus,
  type CreateDictTypeBody,
  type UpdateDictTypeBody,
} from '@/api/dict'

interface DictTypeModalProps {
  open: boolean
  mode: 'create' | 'edit'
  currentId: number | null
  afterClose: () => void
  onRefresh: () => void
}

type DictTypeFormValues = CreateDictTypeBody

export default function DictTypeModal(props: DictTypeModalProps) {
  const { open, mode, currentId, afterClose, onRefresh } = props
  const [messageApi, messageContextHolder] = message.useMessage()
  const [form] = Form.useForm<DictTypeFormValues>()
  const [submitLoading, setSubmitLoading] = useState(false)

  const titleText = mode === 'create' ? '新增字典类型' : '编辑字典类型'

  useEffect(() => {
    if (!open) { return }

    if (mode === 'edit' && currentId !== null) {
      getDictTypeById(currentId)
        .then((response) => {
          const type = response.data
          form.setFieldsValue({
            name: type.name,
            code: type.code,
            description: type.description,
            sort: type.sort,
            status: type.status,
          })
        })
        .catch((error) => {
          messageApi.error(error instanceof Error ? error.message : '字典类型信息加载失败')
        })
    } else {
      form.resetFields()
    }
  }, [open, mode, currentId, form, messageApi])

  function handleCancel() {
    if (submitLoading) { return }
    afterClose()
  }

  async function handleSubmit() {
    try {
      const values = await form.validateFields()
      setSubmitLoading(true)

      if (mode === 'create') {
        await createDictType(values)
        messageApi.success('字典类型新增成功')
      } else if (currentId !== null) {
        const payload: UpdateDictTypeBody = { id: currentId, ...values }
        await updateDictType(payload)
        messageApi.success('字典类型已更新')
      }

      onRefresh()
      afterClose()
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
        className="dict-page__modal"
        closeIcon={<Icon icon="lucide:x" />}
        destroyOnClose
        footer={null}
        onCancel={handleCancel}
        open={open}
        title={titleText}
        width={520}
        wrapClassName="dict-page__modal-wrap"
      >
        <div className="dict-page__modal-body">
          <Form<DictTypeFormValues>
            className="dict-page__form"
            form={form}
            layout="vertical"
          >
            <Form.Item
              label="字典名称"
              name="name"
              rules={[
                { required: true, message: '请输入字典名称' },
                { max: 100, message: '字典名称不超过 100 个字符' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入字典名称" />
            </Form.Item>

            <Form.Item
              label="字典编码"
              name="code"
              rules={[
                { required: true, message: '请输入字典编码' },
                { max: 100, message: '字典编码不超过 100 个字符' },
                { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '字典编码必须以字母开头，且仅包含字母、数字和下划线' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入字典编码" />
            </Form.Item>

            <Form.Item
              label="排序值"
              name="sort"
              rules={[
                { required: true, message: '请输入排序值' },
                { type: 'number', min: 0, message: '排序值必须大于等于 0' },
              ]}
              getValueFromEvent={(e: React.ChangeEvent<HTMLInputElement>) => {
                const val = parseInt(e.target.value, 10)
                return Number.isNaN(val) ? undefined : val
              }}
              getValueProps={(value: number | undefined) => ({
                value: value ?? '',
              })}
            >
              <Input placeholder="请输入排序值" type="number" />
            </Form.Item>

            <Form.Item label="描述" name="description">
              <Input.TextArea
                autoComplete="off"
                placeholder="请输入描述（可选）"
                rows={3}
              />
            </Form.Item>

            <Form.Item
              label="状态"
              name="status"
              initialValue={DictStatus.Enabled}
              valuePropName="checked"
              getValueFromEvent={(checked: boolean) =>
                checked ? DictStatus.Enabled : DictStatus.Disabled
              }
              getValueProps={(value: DictStatus) => ({
                checked: value === DictStatus.Enabled,
              })}
            >
              <Switch checkedChildren="启用" unCheckedChildren="禁用" />
            </Form.Item>
          </Form>
        </div>

        <div className="dict-page__modal-actions">
          <Button className="dict-page__secondary-button" onClick={handleCancel}>
            取消
          </Button>
          <Button
            className="dict-page__primary-button"
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
