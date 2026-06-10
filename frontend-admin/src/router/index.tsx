import { createBrowserRouter, Navigate } from 'react-router-dom'
import AdminLayout from '@/layouts/AdminLayout'
import LoginPage from '@/pages/login'
import OrdersPage from '@/pages/orders'
import DishesPage from '@/pages/dishes'
import MealsPage from '@/pages/meals'
import CategoriesPage from '@/pages/categories'
import TablesPage from '@/pages/tables'
import EmployeesPage from '@/pages/employees'

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <AdminLayout />,
    children: [
      {
        index: true,
        element: <Navigate to="/orders" replace />,
      },
      {
        path: 'orders',
        element: <OrdersPage />,
      },
      {
        path: 'dishes',
        element: <DishesPage />,
      },
      {
        path: 'meals',
        element: <MealsPage />,
      },
      {
        path: 'categories',
        element: <CategoriesPage />,
      },
      {
        path: 'tables',
        element: <TablesPage />,
      },
      {
        path: 'employees',
        element: <EmployeesPage />,
      },
    ],
  },
])

export default router
