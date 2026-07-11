import api from '../config/axiosConfig'

const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  getMe: () => api.get('/users/me'),
}

export default authService