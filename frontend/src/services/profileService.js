import api from '../config/axiosConfig'

const profileService = {
  getProfile: () => api.get('/api/users/profile'),
  updateProfile: (data) => api.put('/api/users/profile', data),
  getPublicProfile: (email) => api.get(`/api/users/profile/${encodeURIComponent(email)}`),
  uploadProfileImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/api/users/profile/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}

export default profileService