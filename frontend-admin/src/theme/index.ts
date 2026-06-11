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
      headerBg: '#fafbfc',
      headerColor: '#595959',
      headerBorderRadius: 0,
      borderColor: '#F0F0F0',
      rowHoverBg: '#FAFAFA',
      cellFontSize: 14,
      cellPaddingBlock: 8,
      cellPaddingInline: 8,
    },
    Button: {
      primaryShadow: 'none',
      textHoverBg: '#FFF2EC',
    },
    Modal: {
      headerBg: '#FFFFFF',
      titleColor: '#1F1F1F',
      titleFontSize: 18,
      contentBg: '#FFFFFF',
    },
    Form: {
      labelColor: '#595959',
      labelFontSize: 13,
    },
    Input: {
      paddingInline: 14,
      hoverBorderColor: '#FFD3BF',
      activeBorderColor: '#FF6B2C',
      activeShadow: '0 0 0 3px rgba(255, 102, 52, 0.12)',
      inputFontSize: 13,
    },
    Select: {
      selectorBg: '#FFFFFF',
      hoverBorderColor: '#FFD3BF',
      activeBorderColor: '#FF6B2C',
      activeOutlineColor: 'rgba(255, 102, 52, 0.12)',
      optionSelectedBg: '#FFF2EC',
      optionSelectedColor: '#FF6B2C',
      optionActiveBg: '#FFF2EC',
    },
    Radio: {
      buttonBg: '#FFFFFF',
      buttonColor: '#8C8C8C',
      buttonCheckedBg: '#FFF2EC',
    },
    Tag: {
      defaultBg: '#F5F5F5',
      defaultColor: '#8C8C8C',
    },
  },
}

export default theme
