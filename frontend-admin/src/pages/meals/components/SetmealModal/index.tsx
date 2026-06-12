import { Icon } from '@iconify/react'
import { InputNumber, Select } from 'antd'
import {
  SetmealStatus,
  createSetmeal,
  getSetmealById,
  updateSetmeal,
  type CreateSetmealBody,
  type SetmealDish,
  type UpdateSetmealBody,
} from '@/api/setmeal'
import { getCategoryPage, CategoryType } from '@/api/category'
import { getDishPage } from '@/api/dish'
import UploadFile from '@/pages/components/UploadFile'

interface SetmealModalProps {
  open: boolean
  mode: 'create' | 'edit'
  currentId: number | null
  afterClose: () => void
  onRefresh: () => void
}

interface SetmealFormValues {
  name: string
  categoryId: number
  price: number
  image: string
  description?: string
  status: SetmealStatus
  setmealDishes: {
    dishId: number
    name: string
    price: number
    copies: number
  }[]
}

export default function SetmealModal(props: SetmealModalProps) {
  const { open, mode, currentId, afterClose, onRefresh } = props
  const [messageApi, messageContextHolder] = message.useMessage()
  const [form] = Form.useForm<SetmealFormValues>()
  const [submitLoading, setSubmitLoading] = useState(false)
  const [categoryOptions, setCategoryOptions] = useState<{ label: string; value: number }[]>([])
  const [dishOptions, setDishOptions] = useState<{ label: string; value: number; price: number }[]>([])
  const watchedDishes = Form.useWatch('setmealDishes', form)

  const titleText = mode === 'create' ? '新增套餐' : '编辑套餐'

  useEffect(() => {
    if (!open) { return }
    getCategoryPage({ page: 1, pageSize: 999, type: CategoryType.Setmeal })
      .then((response) => {
        setCategoryOptions(
          response.data.records.map((item) => ({ label: item.name, value: item.id })),
        )
      })
      .catch(() => {})
    getDishPage({ page: 1, pageSize: 999 })
      .then((response) => {
        setDishOptions(
          response.data.records.map((item) => ({ label: item.name, value: item.id, price: item.price })),
        )
      })
      .catch(() => {})
  }, [open])

  useEffect(() => {
    if (!open) { return }

    form.resetFields()

    if (mode === 'edit' && currentId !== null) {
      getSetmealById(currentId)
        .then((response) => {
          const setmeal = response.data
          form.setFieldsValue({
            name: setmeal.name,
            categoryId: setmeal.categoryId,
            price: setmeal.price,
            image: setmeal.image,
            description: setmeal.description,
            status: setmeal.status,
            setmealDishes: (setmeal.setmealDishes || []).map((d: SetmealDish) => ({
              dishId: d.dishId,
              name: d.name ?? '',
              price: d.price,
              copies: d.copies,
            })),
          })
        })
        .catch((error) => {
          messageApi.error(error instanceof Error ? error.message : '套餐信息加载失败')
        })
    } else {
      form.setFieldsValue({ status: SetmealStatus.Enabled })
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

      const setmealDishes: SetmealDish[] = values.setmealDishes
        .filter((d) => d.dishId)
        .map((d) => ({
          dishId: d.dishId,
          name: d.name,
          price: d.price,
          copies: d.copies,
        }))

      if (mode === 'create') {
        const payload: CreateSetmealBody = { ...values, setmealDishes }
        await createSetmeal(payload)
        messageApi.success('套餐新增成功')
      } else if (currentId !== null) {
        const payload: UpdateSetmealBody = { id: currentId, ...values, setmealDishes }
        await updateSetmeal(payload)
        messageApi.success('套餐已更新')
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
        className="setmeals-page__modal"
        closeIcon={<Icon icon="lucide:x" />}
        destroyOnHidden
        footer={null}
        onCancel={handleCancel}
        open={open}
        title={titleText}
        width={720}
        wrapClassName="setmeals-page__modal-wrap"
      >
        <div className="setmeals-page__modal-body">
          <Form<SetmealFormValues>
            className="setmeals-page__form"
            form={form}
            layout="vertical"
          >
            <Form.Item
              label="套餐名称"
              name="name"
              rules={[
                { required: true, message: '请输入套餐名称' },
                { max: 32, message: '套餐名称不超过 32 个字符' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入套餐名称" />
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
                className="setmeals-page__price-input"
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
                placeholder="请输入套餐描述"
                rows={3}
                maxLength={255}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="状态"
              name="status"
              initialValue={SetmealStatus.Enabled}
              valuePropName="checked"
              getValueFromEvent={(checked: boolean) =>
                checked ? SetmealStatus.Enabled : SetmealStatus.Disabled
              }
              getValueProps={(value: SetmealStatus) => ({
                checked: value === SetmealStatus.Enabled,
              })}
            >
              <Switch checkedChildren="起售" unCheckedChildren="停售" />
            </Form.Item>

            <div className="setmeals-page__dishes-section">
              <div className="setmeals-page__dishes-header">
                <span className="setmeals-page__dishes-title">关联菜品</span>
              </div>

              <Form.List name="setmealDishes">
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }) => {
                      const selectedIds = watchedDishes
                        ?.map((d: { dishId?: number }) => d.dishId)
                        .filter(Boolean) ?? []
                      const filteredOptions = dishOptions.map((opt) => ({
                        ...opt,
                        disabled: selectedIds.includes(opt.value) && opt.value !== form.getFieldValue(['setmealDishes', name, 'dishId']),
                      }))

                      return (
                      <div key={key} className="setmeals-page__dish-item">
                        <Form.Item
                          {...restField}
                          label="菜品"
                          name={[name, 'dishId']}
                          rules={[{ required: true, message: '请选择菜品' }]}
                        >
                          <Select
                            allowClear
                            options={filteredOptions}
                            placeholder="请选择菜品"
                            showSearch
                            filterOption={(input, option) =>
                              (option?.label as string).toLowerCase().includes(input.toLowerCase())
                            }
                            onChange={(_value, option) => {
                              if (option && typeof option === 'object' && 'label' in option) {
                                const selected = option as typeof dishOptions[number]
                                form.setFieldValue(['setmealDishes', name, 'name'], selected.label)
                                form.setFieldValue(['setmealDishes', name, 'price'], selected.price)
                              }
                            }}
                          />
                        </Form.Item>
                        <Form.Item
                          {...restField}
                          label="份数"
                          name={[name, 'copies']}
                          rules={[{ required: true, message: '请输入份数' }]}
                        >
                          <InputNumber min={1} placeholder="1" style={{ width: '100%' }} />
                        </Form.Item>
                        <Button
                          aria-label="删除关联菜品"
                          className="setmeals-page__dish-remove"
                          danger
                          icon={<Icon icon="lucide:trash-2" />}
                          onClick={() => remove(name)}
                          type="text"
                        />
                      </div>
                    )})}
                    <Button
                      className="setmeals-page__add-dish"
                      icon={<Icon icon="lucide:plus" />}
                      onClick={() => add({ dishId: undefined, name: '', price: 0, copies: 1 })}
                      type="dashed"
                    >
                      添加菜品
                    </Button>
                  </>
                )}
              </Form.List>
            </div>
          </Form>
        </div>

        <div className="setmeals-page__modal-actions">
          <Button onClick={handleCancel}>
            取消
          </Button>
          <Button
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
