import { useState } from 'react'
import AuthContext from './AuthContext'
import websocketService from '../services/websocketService'
import presenceService from '../services/presenceService'

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token') || null)
  const [user, setUser] = useState(null)
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('token'))

  const login = (jwtToken, userData) => {
    localStorage.setItem('token', jwtToken)
    setToken(jwtToken)
    setUser(userData)
    setIsAuthenticated(true)
  }

  const logout = () => {
    websocketService.disconnect()
    presenceService.disconnect()
    localStorage.removeItem('token')
    setToken(null)
    setUser(null)
    setIsAuthenticated(false)
  }

  return (
    <AuthContext.Provider value={{ token, user, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthProvider