import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

let stompClient = null
let roomEventSubscription = null
let onEventCallback = null

const roomEventService = {

  connect(onRoomEvent) {
    onEventCallback = onRoomEvent

    const token = localStorage.getItem('token')

    stompClient = new Client({
     webSocketFactory: () =>
      new SockJS(`${import.meta.env.VITE_API_URL}/ws`),
      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : '',
      },

      reconnectDelay: 5000,

      onConnect: () => {
        roomEventSubscription = stompClient.subscribe(
          '/topic/room-events',
          (frame) => {
            try {
              const event = JSON.parse(frame.body)
              if (onEventCallback) {
                onEventCallback(event)
              }
            } catch (e) {
              console.error('Failed to parse room event', e)
            }
          }
        )
      },

      onStompError: (frame) => {
        console.error('Room-event STOMP error', frame)
      },
    })

    stompClient.activate()
  },

  disconnect() {
    if (roomEventSubscription) {
      roomEventSubscription.unsubscribe()
      roomEventSubscription = null
    }
    if (stompClient) {
      stompClient.deactivate()
      stompClient = null
    }
    onEventCallback = null
  },
}

export default roomEventService