import api from '../config/axiosConfig'

const fileService = {
  uploadFile: (file, onProgress) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/api/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (event) => {
        if (onProgress && event.total) {
          const percent = Math.round((event.loaded * 100) / event.total)
          onProgress(percent)
        }
      },
    })
  },
}

export default fileService