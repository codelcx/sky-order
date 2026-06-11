import AutoImport from 'unplugin-auto-import/vite'
import AntdResolver from 'unplugin-auto-import-antd'

export default function autoImport() {
  return AutoImport({
    imports: ['react', 'react-router-dom'],
    resolvers: [AntdResolver()],
    dts: 'typings/auto-imports.d.ts',
  })
}
