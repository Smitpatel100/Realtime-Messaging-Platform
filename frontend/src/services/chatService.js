import api from '../config/axiosConfig'

const chatService = {
  getRooms: () => api.get('/api/chat-rooms/my-rooms'),
  getMessages: (roomId) => api.get(`/api/messages/rooms/${roomId}`),
  sendMessage: (roomId, payload) => api.post(`/api/messages/rooms/${roomId}`, payload),
  markRoomMessagesSeen: (roomId) => api.put(`/api/messages/rooms/${roomId}/seen`),
  getAllUsers: () => api.get('/api/users'),
  createPrivateChat: (targetEmail) => api.post('/api/chat-rooms/private', { targetEmail }),
  createGroupChat: (name, memberEmails) => api.post('/api/chat-rooms/group', { name, memberEmails }),
  searchMessages: (roomId, keyword) =>
    api.get(`/api/messages/rooms/${roomId}/search`, { params: { keyword } }),
  getUnreadCounts: () => api.get('/api/chat-rooms/unread-counts'),
}

export default chatService