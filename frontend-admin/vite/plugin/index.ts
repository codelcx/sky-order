import react from '@vitejs/plugin-react'
import autoImport from './auto-import'

export default function plugins() {
  return [
    react(),
    autoImport(),
  ]
}
