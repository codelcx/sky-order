import { Icon } from '@iconify/react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import './AdminLayout.scss'

type NavigationItem = {
  path: string
  icon: string
  label: string
}

const navigationItems: NavigationItem[] = [
  {
    path: '/orders',
    icon: 'lucide:shopping-bag',
    label: '订单中心',
  },
  {
    path: '/dishes',
    icon: 'lucide:utensils',
    label: '菜品管理',
  },
  {
    path: '/meals',
    icon: 'lucide:package-open',
    label: '套餐管理',
  },
  {
    path: '/categories',
    icon: 'lucide:grid-2x2',
    label: '分类管理',
  },
  {
    path: '/tables',
    icon: 'lucide:table-2',
    label: '桌号管理',
  },
  {
    path: '/employees',
    icon: 'lucide:users-round',
    label: '员工管理',
  },
]

function formatToday() {
  return new Intl.DateTimeFormat('en-CA', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date())
}

export default function AdminLayout() {
  const navigate = useNavigate()
  const location = useLocation()
  const currentItem =
    navigationItems.find((item) => location.pathname.startsWith(item.path)) ?? navigationItems[0]

  return (
    <div className="admin-shell">
      <aside className="admin-sidebar">
        <div className="admin-brand" aria-label="味来餐饮">
          <span className="admin-brand__mark">味</span>
          <span className="admin-brand__name">味来餐饮</span>
        </div>

        <nav className="admin-nav" aria-label="后台导航">
          {navigationItems.map((item) => {
            const isActive = currentItem.path === item.path

            return (
              <button
                className={`admin-nav__item${isActive ? ' admin-nav__item--active' : ''}`}
                key={item.path}
                onClick={() => navigate(item.path)}
                type="button"
              >
                <Icon className="admin-nav__icon" icon={item.icon} />
                <span>{item.label}</span>
              </button>
            )
          })}
        </nav>
      </aside>

      <div className="admin-workspace">
        <header className="admin-header">
          <div className="admin-header__actions">
            <time className="admin-header__date" dateTime={formatToday()}>
              {formatToday()}
            </time>

            <button className="admin-header__bell" type="button" aria-label="通知">
              <Icon icon="lucide:bell" />
              <span />
            </button>

            <div className="admin-header__user">
              <span className="admin-header__avatar">
                <Icon icon="lucide:user-round" />
              </span>
              <span>管理员</span>
            </div>
          </div>
        </header>

        <main className="admin-content">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
