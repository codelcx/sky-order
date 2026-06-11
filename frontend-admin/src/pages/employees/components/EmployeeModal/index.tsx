import { Icon } from '@iconify/react'
import { Select } from 'antd'
import {
  createEmployee,
  EmployeeStatus,
  getEmployeeById,
  Sex,
  updateEmployee,
  updateEmployeeStatus,
  type CreateEmployeeBody,
  type Employee,
  type UpdateEmployeeBody,
} from '@/api/employee'
import { DICT_CODE } from '@/constants'
import { getDictDataListByCode } from '@/api/dict'

interface EmployeeModalProps {
  open: boolean
  mode: 'create' | 'edit'
  currentEmployeeId: number | null
  records: Employee[]
  onClose: () => void
  onRefresh: (resetPage: boolean) => void
}

type EmployeeFormValues = CreateEmployeeBody

export default function EmployeeModal(props: EmployeeModalProps) {
  const { open, mode, currentEmployeeId, records, onClose, onRefresh } = props
  const [messageApi, messageContextHolder] = message.useMessage()
  const [form] = Form.useForm<EmployeeFormValues>()
  const [submitLoading, setSubmitLoading] = useState(false)
  const [occupationOptions, setOccupationOptions] = useState<{ label: string; value: string }[]>([])

  useEffect(() => {
    if (!open) { return }
    getDictDataListByCode(DICT_CODE.OCCUPATION)
      .then((list) => setOccupationOptions(list.map((item) => ({ label: item.label, value: item.value }))))
      .catch(() => {})
  }, [open])

  const titleText = mode === 'create' ? '新增员工' : '编辑员工'

  useEffect(() => {
    if (!open) { return }

    if (mode === 'edit' && currentEmployeeId !== null) {
      getEmployeeById(currentEmployeeId)
        .then((response) => {
          const employee = response.data
          form.setFieldsValue({
            username: employee.username,
            name: employee.name,
            phone: employee.phone,
            sex: employee.sex,
            idNumber: employee.idNumber,
            job: employee.job,
            address: employee.address,
          })
        })
        .catch((error) => {
          messageApi.error(error instanceof Error ? error.message : '员工信息加载失败')
        })
    } else {
      form.resetFields()
      form.setFieldsValue({ sex: Sex.Male })
    }
  }, [open, mode, currentEmployeeId, form, messageApi])

  function handleCancel() {
    if (submitLoading) { return }
    onClose()
  }

  async function handleSubmit() {
    try {
      const values = await form.validateFields()
      setSubmitLoading(true)

      if (mode === 'create') {
        await createEmployee(values)
        messageApi.success('员工新增成功')
        onRefresh(true)
      } else if (currentEmployeeId !== null) {
        const payload: UpdateEmployeeBody = { id: currentEmployeeId, ...values }
        await updateEmployee(payload)
        messageApi.success('员工信息已更新')
        onRefresh(false)
      }

      onClose()
    } catch (error) {
      if (error instanceof Error) {
        messageApi.error(error.message)
      }
    } finally {
      setSubmitLoading(false)
    }
  }

  async function handleToggleStatus(employee: Employee) {
    try {
      const nextStatus =
        employee.status === EmployeeStatus.Enabled ? EmployeeStatus.Disabled : EmployeeStatus.Enabled
      await updateEmployeeStatus(employee.id, nextStatus)
      messageApi.success(nextStatus === EmployeeStatus.Enabled ? '员工已设置为在职' : '员工已设置为离职')
      onRefresh(false)
    } catch (error) {
      messageApi.error(error instanceof Error ? error.message : '员工状态更新失败')
    }
  }

  return (
    <>
      {messageContextHolder}
      <Modal
        centered
        className="employees-page__modal"
        closeIcon={<Icon icon="lucide:x" />}
        destroyOnClose
        footer={null}
        onCancel={handleCancel}
        open={open}
        title={titleText}
        width={640}
        wrapClassName="employees-page__modal-wrap"
      >
        <div className="employees-page__modal-body">
          <Form<EmployeeFormValues>
            className="employees-page__form"
            form={form}
            layout="vertical"
          >
            <Form.Item
              label="登录账号"
              name="username"
              rules={[
                { required: true, message: '请输入登录账号' },
                { min: 3, max: 8, message: '登录账号长度需为 3 到 8 位' },
              ]}
            >
              <Input autoComplete="off" placeholder="请输入登录账号" />
            </Form.Item>

            <Form.Item
              label="员工姓名"
              name="name"
              rules={[
                { required: true, message: '请输入员工姓名' },
                { min: 2, max: 4, message: '员工姓名长度需为 2 到 4 位' },
              ]}
            >
              <Input autoComplete="no" placeholder="请输入员工姓名" />
            </Form.Item>

            <Form.Item
              label="联系电话"
              name="phone"
              rules={[
                { required: true, message: '请输入联系电话' },
                { pattern: /^1\d{10}$/, message: '请输入正确的 11 位手机号' },
              ]}
            >
              <Input autoComplete="off" maxLength={11} placeholder="请输入联系电话" />
            </Form.Item>

            <Form.Item
              label="身份证号"
              name="idNumber"
              rules={[
                { required: true, message: '请输入身份证号' },
                { pattern: /^\d{17}[\dX]$/, message: '请输入正确的 18 位身份证号' },
              ]}
            >
              <Input autoComplete="off" maxLength={18} placeholder="请输入身份证号" />
            </Form.Item>

            <Form.Item label="职业" name="job">
              <Select
                allowClear
                options={occupationOptions}
                placeholder="请选择职业"
              />
            </Form.Item>

            <Form.Item label="地址" name="address">
              <Input autoComplete="off" placeholder="请输入地址" />
            </Form.Item>

            <Form.Item label="性别" name="sex" rules={[{ required: true, message: '请选择性别' }]}>
              <Radio.Group className="employees-page__sex-group" optionType="button" buttonStyle="solid">
                <Radio.Button className="employees-page__sex-option" value={Sex.Male}>
                  男
                </Radio.Button>
                <Radio.Button className="employees-page__sex-option" value={Sex.Female}>
                  女
                </Radio.Button>
              </Radio.Group>
            </Form.Item>

            {mode === 'edit' && currentEmployeeId !== null ? (
              <Form.Item className="employees-page__status-field" label="在职状态">
                <Switch
                  checked={
                    records.find((item) => item.id === currentEmployeeId)?.status === EmployeeStatus.Enabled
                  }
                  checkedChildren="在职"
                  unCheckedChildren="离职"
                  onChange={() => {
                    const target = records.find((item) => item.id === currentEmployeeId)
                    if (!target) { return }
                    void handleToggleStatus(target)
                  }}
                />
              </Form.Item>
            ) : null}
          </Form>
        </div>

        <div className="employees-page__modal-actions">
          <Button className="employees-page__secondary-button" onClick={handleCancel}>
            取消
          </Button>
          <Button
            className="employees-page__primary-button"
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
