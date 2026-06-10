import type { ThemeConfig } from 'antd'

const theme: ThemeConfig = {
  token: {
    colorPrimary: '#FF6B2C',
    colorSuccess: '#52C41A',
    colorWarning: '#FAAD14',
    colorError: '#FF4D4F',
    colorInfo: '#1890FF',
    colorTextBase: '#1F1F1F',
    colorTextSecondary: '#595959',
    colorTextTertiary: '#8C8C8C',
    colorBgLayout: '#F7F7F8',
    colorBgContainer: '#FFFFFF',
    colorBgElevated: '#FFFFFF',
    colorBorder: '#E8E8E8',
    colorBorderSecondary: '#F0F0F0',
    fontFamily: '"PingFang SC", "Inter", "Microsoft YaHei", sans-serif',
    fontSize: 14,
    borderRadius: 12,
    borderRadiusLG: 16,
    borderRadiusSM: 8,
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.04)',
    boxShadowSecondary: '0 6px 20px rgba(0, 0, 0, 0.08)',
  },
  components: {
    Layout: {
      headerBg: '#FFFFFF',
      siderBg: '#FFFFFF',
      bodyBg: '#F7F7F8',
      headerHeight: 72,
    },
    Table: {
      headerColor: '#595959',
      rowHoverBg: '#FAFAFA',
    },
  },
}

export default theme
