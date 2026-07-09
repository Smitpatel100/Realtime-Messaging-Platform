import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from '../pages/LoginPage'
import RegisterPage from '../pages/RegisterPage'
import ChatPage from '../pages/ChatPage'
import ProfilePage from '../pages/ProfilePage'
import ProtectedRoute from './ProtectedRoute'

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/chat"
        element={
          <ProtectedRoute>
            <ChatPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        }
      />
    </Routes>
  )
}

export default AppRoutes