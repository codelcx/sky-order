import { Icon } from '@iconify/react'
import {
  CategoryStatus,
  CategoryType,
  createCategory,
  getCategoryById,
  updateCategory,
  type CreateCategoryBody,
  type UpdateCategoryBody,
} from '@/api/category'

interface CategoryModalProps {
  open: boolean
  mode: 'create' | 'edit'
  currentId: number | null
  afterClose: () => void
  onRefresh: () => void
}

type CategoryFormValues = CreateCategoryBody

export default function CategoryModal(props: CategoryModalProps) {
  const { open, mode, currentId, afterClose, onRefresh } = props
  const [messageApi, messageContextHolder] = message.useMessage()
  const [form] = Form.useForm<CategoryFormValues>()
  const [submitLoading, setSubmitLoading] = useState(false)

  const titleText = mode === 'create' ? '新增分类' : '编辑分类'

  useEffect(() => {
    if (!open) { return }

    if (mode === 'edit' && currentId !== null) {
      getCategoryById(currentId)
        .then((response) => {
          const category = response.data
          form.setFieldsValue({
            type: category.type,
            name: category.name,
            sort: category.sort,
            status: category.status,
          })
        })
        .catch((error) => {
          messageApi.error(error instanceof Error ? error.message : '分类信息加载失败')
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
        await createCategory(values)
        messageApi.success('分类新增成功')
      } else if (currentId !== null) {
        const payload: UpdateCategoryBody = { id: currentId, ...values }
        await updateCategory(payload)
        messageApi.success('分类已更新')
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
        className="categories-page__modal"
        closeIcon={<Icon icon="lucide:x" />}
        destroyOnClose
        footer={null}
        onCancel={handleCancel}
        open={open}
        title={titleText}
        width={520}
        wrapClassName="categories-page__modal-wrap"
      >
        <div className="categories-page__modal-body">
          <Form<CategoryFormValues>
            className="categories-page__form"
            form={form}
            layout="vertical"
          >
            <Form.Item
              className="categories-page__type-field"
              label="分类类型"
              name="type"
              initialValue={CategoryType.Dish}
              rules={[{ required: true, message: '请选择分类类型' }]}
            >
              <Radio.Group
                className="categories-page__type-group"
                optionType="button"
                buttonStyle="solid"
              >
                <Radio.Button value={CategoryType.Dish}>菜品</Radio.Button>
                <Radio.Button value={CategoryType.Setmeal}>套餐</Radio.Button>
              </Radio.Group>
            </Form.Item>

            <Form.Item
              label="分类名称"
              name="name"
              rules={[
                { required: true, message: '请输入分类名称' },
                { max: 50, message: '分类名称不超过 50 个字符' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入分类名称" />
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

            <Form.Item
              label="状态"
              name="status"
              initialValue={CategoryStatus.Enabled}
              valuePropName="checked"
              getValueFromEvent={(checked: boolean) =>
                checked ? CategoryStatus.Enabled : CategoryStatus.Disabled
              }
              getValueProps={(value: CategoryStatus) => ({
                checked: value === CategoryStatus.Enabled,
              })}
            >
              <Switch checkedChildren="启用" unCheckedChildren="禁用" />
            </Form.Item>
          </Form>
        </div>

        <div className="categories-page__modal-actions">
          <Button className="categories-page__secondary-button" onClick={handleCancel}>
            取消
          </Button>
          <Button
            className="categories-page__primary-button"
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
