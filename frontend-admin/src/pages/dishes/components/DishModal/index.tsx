import { Icon } from '@iconify/react'
import { InputNumber, Select } from 'antd'
import {
  createDish,
  DishStatus,
  getDishById,
  updateDish,
  type CreateDishBody,
  type DishFlavor,
  type UpdateDishBody,
} from '@/api/dish'
import { getCategoryPage, CategoryType, type Category } from '@/api/category'
import UploadFile from '@/pages/components/UploadFile'

interface DishModalProps {
  open: boolean
  mode: 'create' | 'edit'
  currentId: number | null
  afterClose: () => void
  onRefresh: () => void
}

interface DishFormValues {
  name: string
  categoryId: number
  price: number
  image: string
  description?: string
  status: DishStatus
  flavors: { name: string; value: string }[]
}

const FLAVOR_INPUT_SEPARATOR = '\n'

export default function DishModal(props: DishModalProps) {
  const { open, mode, currentId, afterClose, onRefresh } = props
  const [messageApi, messageContextHolder] = message.useMessage()
  const [form] = Form.useForm<DishFormValues>()
  const [submitLoading, setSubmitLoading] = useState(false)
  const [categoryOptions, setCategoryOptions] = useState<{ label: string; value: number }[]>([])

  const titleText = mode === 'create' ? '新增菜品' : '编辑菜品'

  useEffect(() => {
    if (!open) { return }
    getCategoryPage({ page: 1, pageSize: 999 })
      .then((response) => {
        setCategoryOptions(
          response.data.records
            .filter((item: Category) => item.type === CategoryType.Dish)
            .map((item: Category) => ({ label: item.name, value: item.id })),
        )
      })
      .catch(() => {})
  }, [open])

  useEffect(() => {
    if (!open) { return }

    form.resetFields()

    if (mode === 'edit' && currentId !== null) {
      getDishById(currentId)
        .then((response) => {
          const dish = response.data
          form.setFieldsValue({
            name: dish.name,
            categoryId: dish.categoryId,
            price: dish.price,
            image: dish.image,
            description: dish.description,
            status: dish.status,
            flavors: (dish.flavors || []).map((f: DishFlavor) => ({
              name: f.name,
              value: (JSON.parse(f.value) as string[]).join(FLAVOR_INPUT_SEPARATOR),
            })),
          })
        })
        .catch((error) => {
          messageApi.error(error instanceof Error ? error.message : '菜品信息加载失败')
        })
    } else {
      form.setFieldsValue({ status: DishStatus.Enabled })
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

      const flavors: DishFlavor[] = values.flavors
        ? values.flavors
            .filter((f) => f.name.trim())
            .map((f) => ({
              name: f.name.trim(),
              value: JSON.stringify(
                f.value
                  .split(FLAVOR_INPUT_SEPARATOR)
                  .map((v) => v.trim())
                  .filter(Boolean),
              ),
            }))
        : []

      if (mode === 'create') {
        const payload: CreateDishBody = { ...values, flavors }
        await createDish(payload)
        messageApi.success('菜品新增成功')
      } else if (currentId !== null) {
        const payload: UpdateDishBody = { id: currentId, ...values, flavors }
        await updateDish(payload)
        messageApi.success('菜品已更新')
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
        className="dishes-page__modal"
        closeIcon={<Icon icon="lucide:x" />}
        destroyOnHidden
        footer={null}
        onCancel={handleCancel}
        open={open}
        title={titleText}
        width={680}
        wrapClassName="dishes-page__modal-wrap"
      >
        <div className="dishes-page__modal-body">
          <Form<DishFormValues>
            className="dishes-page__form"
            form={form}
            layout="vertical"
          >
            <Form.Item
              label="菜品名称"
              name="name"
              rules={[
                { required: true, message: '请输入菜品名称' },
                { max: 32, message: '菜品名称不超过 32 个字符' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入菜品名称" />
            </Form.Item>

            <Form.Item
              label="分类"
              name="categoryId"
              rules={[{ required: true, message: '请选择分类' }]}
            >
              <Select
                allowClear
                options={categoryOptions}
                placeholder="请选择分类"
                showSearch
                filterOption={(input, option) =>
                  (option?.label as string).toLowerCase().includes(input.toLowerCase())
                }
              />
            </Form.Item>

            <Form.Item
              label="价格"
              name="price"
              rules={[
                { required: true, message: '请输入价格' },
                { type: 'number', min: 0, message: '价格必须大于等于 0' },
              ]}
            >
              <InputNumber
                className="dishes-page__price-input"
                min={0}
                placeholder="请输入价格"
                precision={2}
                prefix="¥"
              />
            </Form.Item>

            <Form.Item label="图片" name="image" rules={[{ required: true, message: '请上传图片' }]}>
              <UploadFile />
            </Form.Item>

            <Form.Item label="描述" name="description">
              <Input.TextArea
                autoComplete="off"
                placeholder="请输入菜品描述"
                rows={3}
                maxLength={255}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="状态"
              name="status"
              initialValue={DishStatus.Enabled}
              valuePropName="checked"
              getValueFromEvent={(checked: boolean) =>
                checked ? DishStatus.Enabled : DishStatus.Disabled
              }
              getValueProps={(value: DishStatus) => ({
                checked: value === DishStatus.Enabled,
              })}
            >
              <Switch checkedChildren="起售" unCheckedChildren="停售" />
            </Form.Item>

            <div className="dishes-page__flavors-section">
              <div className="dishes-page__flavors-header">
                <span className="dishes-page__flavors-title">口味配置</span>
                <span className="dishes-page__flavors-hint">每行输入一个选项，换行分隔</span>
              </div>

              <Form.List name="flavors">
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }) => (
                      <div key={key} className="dishes-page__flavor-item">
                        <Form.Item
                          {...restField}
                          className="dishes-page__flavor-name"
                          label="口味名称"
                          name={[name, 'name']}
                          rules={[{ required: true, message: '请输入口味名称' }]}
                        >
                          <Input autoComplete="off" placeholder="如：辣度、忌口" />
                        </Form.Item>
                        <Form.Item
                          {...restField}
                          className="dishes-page__flavor-values"
                          label="口味选项"
                          name={[name, 'value']}
                          rules={[{ required: true, message: '请输入口味选项' }]}
                        >
                          <Input.TextArea
                            autoComplete="off"
                            placeholder="每行一个选项&#10;如：&#10;不辣&#10;微辣&#10;中辣"
                            rows={6}
                            style={{ resize: 'none' }}
                          />
                        </Form.Item>
                        <Button
                          aria-label="删除口味"
                          className="dishes-page__flavor-remove"
                          danger
                          icon={<Icon icon="lucide:trash-2" />}
                          onClick={() => remove(name)}
                          type="text"
                        />
                      </div>
                    ))}
                    <Button
                      className="dishes-page__add-flavor"
                      icon={<Icon icon="lucide:plus" />}
                      onClick={() => add({ name: '', value: '' })}
                      type="dashed"
                    >
                      添加口味
                    </Button>
                  </>
                )}
              </Form.List>
            </div>
          </Form>
        </div>

        <div className="dishes-page__modal-actions">
          <Button className="dishes-page__secondary-button" onClick={handleCancel}>
            取消
          </Button>
          <Button
            className="dishes-page__primary-button"
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
