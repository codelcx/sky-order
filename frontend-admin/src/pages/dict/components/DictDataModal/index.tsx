import { Icon } from '@iconify/react'
import {
  createDictData,
  getDictDataById,
  updateDictData,
  DictStatus,
  type CreateDictDataBody,
  type UpdateDictDataBody,
} from '@/api/dict'

interface DictDataModalProps {
  open: boolean
  mode: 'create' | 'edit'
  currentId: number | null
  dictTypeId: number
  afterClose: () => void
  onRefresh: () => void
}

type DictDataFormValues = Omit<CreateDictDataBody, 'dictTypeId'>

export default function DictDataModal(props: DictDataModalProps) {
  const { open, mode, currentId, dictTypeId, afterClose, onRefresh } = props
  const [messageApi, messageContextHolder] = message.useMessage()
  const [form] = Form.useForm<DictDataFormValues>()
  const [submitLoading, setSubmitLoading] = useState(false)

  const titleText = mode === 'create' ? '新增字典数据' : '编辑字典数据'

  useEffect(() => {
    if (!open) { return }

    if (mode === 'edit' && currentId !== null) {
      getDictDataById(currentId)
        .then((response) => {
          const data = response.data
          form.setFieldsValue({
            label: data.label,
            value: data.value,
            sort: data.sort,
            remark: data.remark,
            status: data.status,
          })
        })
        .catch((error) => {
          messageApi.error(error instanceof Error ? error.message : '字典数据加载失败')
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
      if (!dictTypeId || Number.isNaN(dictTypeId)) {
        messageApi.error('字典类型ID不能为空')
        return
      }

      const values = await form.validateFields()
      setSubmitLoading(true)

      if (mode === 'create') {
        const payload: CreateDictDataBody = { ...values, dictTypeId }
        await createDictData(payload)
        messageApi.success('字典数据新增成功')
      } else if (currentId !== null) {
        const payload: UpdateDictDataBody = { id: currentId, ...values, dictTypeId }
        await updateDictData(payload)
        messageApi.success('字典数据已更新')
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
          <Form<DictDataFormValues>
            className="dict-page__form"
            form={form}
            layout="vertical"
          >
            <Form.Item
              label="字典标签"
              name="label"
              rules={[
                { required: true, message: '请输入字典标签' },
                { max: 100, message: '字典标签不超过 100 个字符' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入字典标签" />
            </Form.Item>

            <Form.Item
              label="字典键值"
              name="value"
              rules={[
                { required: true, message: '请输入字典键值' },
                { max: 100, message: '字典键值不超过 100 个字符' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入字典键值" />
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

            <Form.Item label="备注" name="remark">
              <Input.TextArea
                autoComplete="off"
                placeholder="请输入备注（可选）"
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
