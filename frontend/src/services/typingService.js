import websocketService from './websocketService'

let typingSubscription = null
let onTypingCallback = null
let stopTypingTimer = null
let isCurrentlyTyping = false

const typingService = {

  subscribeToRoom(roomId, onTypingUpdate) {
    if (typingSubscription) {
      typingSubscription.unsubscribe()
      typingSubscription = null
    }

    onTypingCallback = onTypingUpdate

    const client = websocketService.getClient()
    if (client && client.connected) {
      typingSubscription = client.subscribe(
        `/topic/typing/${roomId}`,
        (frame) => {
          try {
            const message = JSON.parse(frame.body)
            if (onTypingCallback) {
              onTypingCallback(message)
            }
          } catch (e) {
            console.error('Failed to parse typing message', e)
          }
        }
      )
    }
  },

  unsubscribe() {
    if (typingSubscription) {
      typingSubscription.unsubscribe()
      typingSubscription = null
    }
    if (stopTypingTimer) {
      clearTimeout(stopTypingTimer)
      stopTypingTimer = null
    }
    onTypingCallback = null
    isCurrentlyTyping = false
  },

  sendTyping(roomId) {
    if (!isCurrentlyTyping) {
      isCurrentlyTyping = true
      websocketService.sendTyping(roomId, true)
    }

    if (stopTypingTimer) {
      clearTimeout(stopTypingTimer)
    }

    stopTypingTimer = setTimeout(() => {
      isCurrentlyTyping = false
      websocketService.sendTyping(roomId, false)
      stopTypingTimer = null
    }, 2000)
  },

  stopTyping(roomId) {
    if (stopTypingTimer) {
      clearTimeout(stopTypingTimer)
      stopTypingTimer = null
    }
    if (isCurrentlyTyping) {
      isCurrentlyTyping = false
      websocketService.sendTyping(roomId, false)
    }
  },
}

export default typingService