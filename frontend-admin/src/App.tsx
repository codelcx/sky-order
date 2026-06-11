import { RouterProvider } from 'react-router-dom'
import router from '@/router'
import theme from '@/theme'
import zhCN from 'antd/locale/zh_CN'

export default function App() {
  return (
    <ConfigProvider locale={zhCN} theme={theme}>
      <RouterProvider router={router} />
    </ConfigProvider>
  )
}
