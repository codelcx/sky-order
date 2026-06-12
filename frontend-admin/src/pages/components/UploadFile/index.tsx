import { Icon } from '@iconify/react'
import { Upload, message } from 'antd'
import type { UploadFile, UploadProps } from 'antd/es/upload/interface'
import { uploadFile } from '@/api/common'

interface UploadFileProps {
  value?: string
  onChange?: (url: string) => void
}

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    void message.error('仅支持上传图片文件')
    return Upload.LIST_IGNORE
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    void message.error('图片大小不能超过 5MB')
    return Upload.LIST_IGNORE
  }
  return true
}

export default function UploadFileComponent(props: UploadFileProps) {
  const { value, onChange } = props
  const [uploading, setUploading] = useState(false)

  const fileList: UploadFile[] = value
    ? [{ uid: '-1', name: 'image', status: 'done', url: value }]
    : []

  const customRequest: UploadProps['customRequest'] = async (options) => {
    const { file, onError } = options
    try {
      setUploading(true)
      const response = await uploadFile(file as File)
      onChange?.(response.data)
    } catch (error) {
      onError?.(error as Error)
    } finally {
      setUploading(false)
    }
  }

  function handleRemove() {
    onChange?.('')
  }

  return (
    <Upload
      accept="image/*"
      customRequest={customRequest}
      fileList={fileList}
      listType="picture-card"
      maxCount={1}
      onRemove={handleRemove}
      beforeUpload={beforeUpload}
    >
      {fileList.length === 0 && !uploading && (
        <div className="upload-file-placeholder">
          <Icon icon="lucide:plus" />
          <div className="upload-file-text">上传图片</div>
        </div>
      )}
    </Upload>
  )
}
