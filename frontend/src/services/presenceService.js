import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import api from '../config/axiosConfig'

let stompClient = null
let presenceSubscription = null
let onPresenceCallback = null

const presenceService = {

  connect(onPresenceUpdate) {
    onPresenceCallback = onPresenceUpdate

    const token = localStorage.getItem('token')

    stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),

      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : '',
      },

      reconnectDelay: 5000,

      onConnect: () => {
        presenceSubscription = stompClient.subscribe(
          '/topic/presence',
          (frame) => {
            try {
              const presence = JSON.parse(frame.body)
              if (onPresenceCallback) {
                onPresenceCallback(presence)
              }
            } catch (e) {
              console.error('Failed to parse presence message', e)
            }
          }
        )
      },

      onStompError: (frame) => {
        console.error('Presence STOMP error', frame)
      },
    })

    stompClient.activate()
  },

  disconnect() {
    if (presenceSubscription) {
      presenceSubscription.unsubscribe()
      presenceSubscription = null
    }
    if (stompClient) {
      stompClient.deactivate()
      stompClient = null
    }
    onPresenceCallback = null
  },

  getInitialOnlineUsers: () => api.get('/api/presence/online'),
}

export default presenceService