import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

let stompClient = null
let unreadSubscription = null
let onUpdateCallback = null

const unreadService = {

  connect(onUnreadUpdate) {
    onUpdateCallback = onUnreadUpdate

    const token = localStorage.getItem('token')

    stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),

      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : '',
      },

      reconnectDelay: 5000,

      onConnect: () => {
        unreadSubscription = stompClient.subscribe(
          '/user/queue/unread-count',
          (frame) => {
            try {
              const update = JSON.parse(frame.body)
              if (onUpdateCallback) {
                onUpdateCallback(update)
              }
            } catch (e) {
              console.error('Failed to parse unread-count update', e)
            }
          }
        )
      },

      onStompError: (frame) => {
        console.error('Unread-count STOMP error', frame)
      },
    })

    stompClient.activate()
  },

  disconnect() {
    if (unreadSubscription) {
      unreadSubscription.unsubscribe()
      unreadSubscription = null
    }
    if (stompClient) {
      stompClient.deactivate()
      stompClient = null
    }
    onUpdateCallback = null
  },
}

export default unreadService