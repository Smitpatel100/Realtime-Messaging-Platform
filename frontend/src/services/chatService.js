import api from '../config/axiosConfig'

const chatService = {
  getRooms: () => api.get('/chat-rooms/my-rooms'),
  getMessages: (roomId) => api.get(`/messages/rooms/${roomId}`),
  sendMessage: (roomId, payload) => api.post(`/messages/rooms/${roomId}`, payload),
  markRoomMessagesSeen: (roomId) => api.put(`/messages/rooms/${roomId}/seen`),
  getAllUsers: () => api.get('/users'),
  createPrivateChat: (targetEmail) => api.post('/chat-rooms/private', { targetEmail }),
  createGroupChat: (name, memberEmails) => api.post('/chat-rooms/group', { name, memberEmails }),
  searchMessages: (roomId, keyword) =>
    api.get(`/messages/rooms/${roomId}/search`, { params: { keyword } }),
  getUnreadCounts: () => api.get('/chat-rooms/unread-counts'),
  deleteMessage: (messageId) => api.delete(`/messages/${messageId}`),
  deletePrivateChat: (roomId) => api.delete(`/chat-rooms/private/${roomId}`),
  leaveGroup: (roomId) => api.delete(`/chat-rooms/${roomId}/leave`),
  deleteGroup: (roomId) => api.delete(`/chat-rooms/group/${roomId}`),
}

export default chatService