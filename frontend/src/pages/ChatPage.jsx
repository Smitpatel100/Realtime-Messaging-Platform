import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import useAuth from '../context/useAuth'
import authService from '../services/authService'
import profileService from '../services/profileService'
import chatService from '../services/chatService'
import websocketService from '../services/websocketService'
import presenceService from '../services/presenceService'
import unreadService from '../services/unreadService'
import roomEventService from '../services/roomEventService'
import typingService from '../services/typingService'
import Sidebar from '../components/chat/Sidebar'
import ChatHeader from '../components/chat/ChatHeader'
import MessageList from '../components/chat/MessageList'
import MessageInput from '../components/chat/MessageInput'
import CreateChatModal from '../components/chat/CreateChatModal'
import ViewProfileModal from '../components/chat/ViewProfileModal'
import '../components/chat/Chat.css'

const ChatPage = () => {
  const navigate = useNavigate()
  const { logout } = useAuth()

  const [currentUser, setCurrentUser] = useState(null)
  const [rooms, setRooms] = useState([])
  const [selectedRoom, setSelectedRoom] = useState(null)
  const [messages, setMessages] = useState([])
  const [messagesLoading, setMessagesLoading] = useState(false)
  const [onlineEmails, setOnlineEmails] = useState(new Set())
  const [typingUsers, setTypingUsers] = useState({})
  const [isCreateChatOpen, setIsCreateChatOpen] = useState(false)
  const [viewProfileEmail, setViewProfileEmail] = useState(null)
  const [searchKeyword, setSearchKeyword] = useState('')
  const [searchResults, setSearchResults] = useState([])
  const [isSearching, setIsSearching] = useState(false)
  const [unreadCounts, setUnreadCounts] = useState({})
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const selectedRoomRef = useRef(null)
  const currentUserRef = useRef(null)

  useEffect(() => {
    authService.getMe()
      .then((res) => {
        currentUserRef.current = res.data
        // Fetch full profile (adds profileImage/bio) without breaking existing /me usage
        profileService.getProfile()
          .then((profileRes) => {
            const merged = { ...res.data, ...profileRes.data }
            setCurrentUser(merged)
            currentUserRef.current = merged
          })
          .catch(() => {
            setCurrentUser(res.data)
          })
      })
      .catch(() => {})

    chatService.getRooms()
      .then((res) => setRooms(res.data))
      .catch(() => {})

    presenceService.getInitialOnlineUsers()
      .then((res) => setOnlineEmails(new Set(res.data)))
      .catch(() => {})

    presenceService.connect((presence) => {
      setOnlineEmails((prev) => {
        const next = new Set(prev)
        if (presence.online) {
          next.add(presence.email)
        } else {
          next.delete(presence.email)
        }
        return next
      })
    })

    chatService.getUnreadCounts()
      .then((res) => {
        const counts = {}
        res.data.forEach((entry) => {
          counts[entry.roomId] = entry.count
        })
        setUnreadCounts(counts)
      })
      .catch(() => {})

    unreadService.connect((update) => {
      const myEmail = currentUserRef.current?.email
      if (update.senderEmail === myEmail) return

      if (selectedRoomRef.current?.id !== update.roomId) {
        setUnreadCounts((prev) => ({
          ...prev,
          [update.roomId]: (prev[update.roomId] || 0) + 1,
        }))
      }

      if (document.hidden && typeof Notification !== 'undefined' && Notification.permission === 'granted') {
        try {
          new Notification(`${update.senderUsername} • ${update.roomName}`, {
            body: update.preview,
          })
        } catch (e) {
          console.error('Failed to show notification', e)
        }
      }
    })

    if (typeof Notification !== 'undefined' && Notification.permission === 'default') {
      Notification.requestPermission()
    }

    roomEventService.connect((event) => {

  if (
    event.type === 'PRIVATE_CREATED' ||
    event.type === 'GROUP_CREATED'
  ) {
    chatService.getRooms()
      .then((res) => setRooms(res.data))
      .catch(() => {})
  }

  else if (
    event.type === 'PRIVATE_DELETED' ||
    event.type === 'GROUP_DELETED'
  ) {
    setRooms((prev) => prev.filter((r) => r.id !== event.roomId))

    setUnreadCounts((prev) => {
      const next = { ...prev }
      delete next[event.roomId]
      return next
    })

    if (selectedRoomRef.current?.id === event.roomId) {
      websocketService.disconnect()
      typingService.unsubscribe()
      setSelectedRoom(null)
      selectedRoomRef.current = null
      setMessages([])
    }
  }

  else if (event.type === 'MEMBER_LEFT') {
    chatService.getRooms()
      .then((res) => setRooms(res.data))
      .catch(() => {})
  }

})

    return () => {
      websocketService.disconnect()
      presenceService.disconnect()
      unreadService.disconnect()
      roomEventService.disconnect()
      typingService.unsubscribe()
    }
  }, [])

  const handleIncomingMessage = (message) => {
    if (!selectedRoomRef.current) return
    if (message.roomId !== selectedRoomRef.current.id) return

    if (message.deleted) {
      setMessages((prev) => prev.filter((m) => m.id !== message.id))
      setSearchResults((prev) => prev.filter((m) => m.id !== message.id))
      return
    }

    setMessages((prev) => {
      const exists = prev.some((m) => m.id === message.id)
      if (exists) {
        return prev.map((m) => (m.id === message.id ? message : m))
      }
      return [...prev, message]
    })

    const currentEmail = currentUserRef.current?.email
    if (message.senderEmail !== currentEmail && !message.seen) {
      chatService.markRoomMessagesSeen(selectedRoomRef.current.id).catch(() => {})
    }
  }

  const handleTypingUpdate = (typingMessage) => {
    const currentEmail = currentUserRef.current?.email
    if (typingMessage.senderEmail === currentEmail) return

    setTypingUsers((prev) => {
      const next = { ...prev }
      if (typingMessage.typing) {
        next[typingMessage.senderEmail] = typingMessage.senderUsername
      } else {
        delete next[typingMessage.senderEmail]
      }
      return next
    })
  }

  const handleSelectRoom = (room) => {
    typingService.unsubscribe()
    websocketService.disconnect()

    setSelectedRoom(room)
    selectedRoomRef.current = room
    setMessages([])
    setTypingUsers({})
    setSearchKeyword('')
    setSearchResults([])
    setIsSearching(false)
    setUnreadCounts((prev) => ({ ...prev, [room.id]: 0 }))

    setMessagesLoading(true)
    chatService.getMessages(room.id)
      .then((res) => {
        setMessages(res.data)
        return chatService.markRoomMessagesSeen(room.id)
      })
      .then((res) => {
        if (res?.data?.length) {
          setMessages((prev) => {
            const updatesById = new Map(res.data.map((m) => [m.id, m]))
            return prev.map((m) => updatesById.get(m.id) || m)
          })
        }
      })
      .catch(() => setMessages([]))
      .finally(() => setMessagesLoading(false))

    websocketService.connect(room.id, handleIncomingMessage)

    setTimeout(() => {
      typingService.subscribeToRoom(room.id, handleTypingUpdate)
    }, 500)
  }

  const handleSend = (payload) => {
    if (!selectedRoom) return
    const hasContent = typeof payload === 'string' ? payload.trim() : payload?.content?.trim()
    const hasFile = typeof payload === 'object' && payload?.fileUrl
    if (!hasContent && !hasFile) return
    typingService.stopTyping(selectedRoom.id)
    websocketService.sendMessage(selectedRoom.id, payload)
  }

  const handleTyping = () => {
    if (!selectedRoom) return
    typingService.sendTyping(selectedRoom.id)
  }

  const handleLogout = () => {
    typingService.unsubscribe()
    websocketService.disconnect()
    presenceService.disconnect()
    unreadService.disconnect()
    roomEventService.disconnect()
    logout()
    navigate('/login')
  }

  const handleSearch = (keyword) => {
    if (!selectedRoom) return
    const trimmed = keyword.trim()
    setSearchKeyword(trimmed)

    if (!trimmed) {
      setSearchResults([])
      setIsSearching(false)
      return
    }

    setIsSearching(true)
    chatService.searchMessages(selectedRoom.id, trimmed)
      .then((res) => setSearchResults(res.data))
      .catch(() => setSearchResults([]))
      .finally(() => setIsSearching(false))
  }

  const handleRoomCreated = (newRoom) => {
    setIsCreateChatOpen(false)
    chatService.getRooms()
      .then((res) => {
        setRooms(res.data)
        const created = res.data.find((r) => r.id === newRoom.id) || newRoom
        handleSelectRoom(created)
      })
      .catch(() => {
        setRooms((prev) => [...prev, newRoom])
        handleSelectRoom(newRoom)
      })
  }

  const handleDeleteMessageForEveryone = (messageId) => {
    chatService.deleteMessage(messageId)
      .then(() => {
        setMessages((prev) => prev.filter((m) => m.id !== messageId))
        setSearchResults((prev) => prev.filter((m) => m.id !== messageId))
      })
      .catch((err) => {
        console.error('Failed to delete message', err)
      })
  }

  const handleDeleteMessageForMe = (messageId) => {
    chatService.deleteMessageForMe(messageId)
      .then(() => {
        setMessages((prev) => prev.filter((m) => m.id !== messageId))
        setSearchResults((prev) => prev.filter((m) => m.id !== messageId))
      })
      .catch((err) => {
        console.error('Failed to delete message for me', err)
      })
  }

  const handleClearChat = (roomId) => {
    chatService.clearChat(roomId)
      .then(() => {
        if (selectedRoomRef.current?.id === roomId) {
          setMessages([])
          setSearchResults([])
        }
      })
      .catch((err) => {
        console.error('Failed to clear chat', err)
      })
  }

  const clearSelectedRoomIfMatches = (roomId) => {
    if (selectedRoomRef.current?.id === roomId) {
      websocketService.disconnect()
      typingService.unsubscribe()
      setSelectedRoom(null)
      selectedRoomRef.current = null
      setMessages([])
    }
  }

  const handleDeletePrivateChat = (roomId) => {
    chatService.deletePrivateChat(roomId)
      .then(() => {
        setRooms((prev) => prev.filter((r) => r.id !== roomId))
        clearSelectedRoomIfMatches(roomId)
      })
      .catch((err) => {
        console.error('Failed to delete chat', err)
      })
  }

  const handleLeaveGroup = (roomId) => {
    chatService.leaveGroup(roomId)
      .then(() => {
        setRooms((prev) => prev.filter((r) => r.id !== roomId))
        clearSelectedRoomIfMatches(roomId)
      })
      .catch((err) => {
        console.error('Failed to leave group', err)
      })
  }

  const handleDeleteGroup = (roomId) => {
    chatService.deleteGroup(roomId)
      .then(() => {
        setRooms((prev) => prev.filter((r) => r.id !== roomId))
        clearSelectedRoomIfMatches(roomId)
      })
      .catch((err) => {
        console.error('Failed to delete group', err)
      })
  }

  const typingText = () => {
    const names = Object.values(typingUsers)
    if (names.length === 0) return null
    if (names.length === 1) return `${names[0]} is typing...`
    if (names.length === 2) return `${names[0]} and ${names[1]} are typing...`
    return 'Several people are typing...'
  }

  return (
    <div className="chat-layout">
     <Sidebar
  user={currentUser}
  rooms={rooms}
  selectedRoom={selectedRoom}
  onSelectRoom={handleSelectRoom}
  onLogout={handleLogout}
  onlineEmails={onlineEmails}
  onOpenCreateChat={() => setIsCreateChatOpen(true)}
  unreadCounts={unreadCounts}
  sidebarOpen={sidebarOpen}
  setSidebarOpen={setSidebarOpen}
/>

      <div className="chat-main">
        {!selectedRoom ? (
          <div className="chat-empty-state">
            <div className="chat-empty-icon">💬</div>
            <div className="chat-empty-text">Select a chat to start messaging</div>
          </div>
        ) : (
          <>
           <ChatHeader
              setSidebarOpen={setSidebarOpen}
              key={selectedRoom.id}
              room={selectedRoom}
              onlineEmails={onlineEmails}
              currentUserEmail={currentUser?.email}
              onOpenProfile={(email) => setViewProfileEmail(email)}
              onSearch={handleSearch}
              searching={isSearching}
              onDeletePrivateChat={handleDeletePrivateChat}
              onLeaveGroup={handleLeaveGroup}
              onDeleteGroup={handleDeleteGroup}
              onClearChat={handleClearChat}
            />
            <MessageList
              messages={searchKeyword ? searchResults : messages}
              currentUserEmail={currentUser?.email}
              loading={searchKeyword ? isSearching : messagesLoading}
              searchActive={!!searchKeyword}
              searchKeyword={searchKeyword}
              onDeleteForMe={handleDeleteMessageForMe}
              onDeleteForEveryone={handleDeleteMessageForEveryone}
            />
            {typingText() && (
              <div className="typing-indicator">
                <span className="typing-dots">
                  <span /><span /><span />
                </span>
                {typingText()}
              </div>
            )}
            <MessageInput
              onSend={handleSend}
              onTyping={handleTyping}
              disabled={false}
            />
          </>
        )}
      </div>

      {isCreateChatOpen && (
        <CreateChatModal
          onClose={() => setIsCreateChatOpen(false)}
          onRoomCreated={handleRoomCreated}
        />
      )}

      {viewProfileEmail && (
        <ViewProfileModal
          email={viewProfileEmail}
          onClose={() => setViewProfileEmail(null)}
        />
      )}
    </div>
  )
}

export default ChatPage