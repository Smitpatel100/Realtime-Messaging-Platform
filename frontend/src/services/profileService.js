import api from '../config/axiosConfig'

const profileService = {
  getProfile: () => api.get('/users/profile'),
  updateProfile: (data) => api.put('/users/profile', data),
  getPublicProfile: (email) => api.get(`/users/profile/${encodeURIComponent(email)}`),
  uploadProfileImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/users/profile/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}

export default profileService