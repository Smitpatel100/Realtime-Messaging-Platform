import api from '../config/axiosConfig'

const authService = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
  getMe: () => api.get('/api/users/me'),
}

export default authService