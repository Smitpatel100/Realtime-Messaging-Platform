import { useState, useEffect, useRef } from 'react'
import websocketService from '../services/websocketService'

const ROOM_ID = 1

function Chat() {
  const [messages, setMessages] = useState([])
  const [inputValue, setInputValue] = useState('')
  const bottomRef = useRef(null)

useEffect(() => {
    websocketService.subscribe((message) => {
      setMessages((prev) => [...prev, message])
    })

    websocketService.connect(ROOM_ID)

    return () => {
      websocketService.disconnect()
    }
  }, [])

  useEffect(() => {
    if (bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [messages])

  const handleSend = () => {
    const trimmed = inputValue.trim()
    if (!trimmed) return
    websocketService.sendMessage(ROOM_ID, trimmed)
    setInputValue('')
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSend()
    }
  }

  return (
    <div style={styles.container}>

      <div style={styles.header}>
        <span>Room {ROOM_ID}</span>
        <div style={styles.header}>
         <span>Room {ROOM_ID}</span>
         <span style={{ color: '#3FB950' }}>● Live</span>
        </div>
      </div>

      <div style={styles.messageList}>
        {messages.length === 0 && (
          <p style={styles.empty}>No messages yet. Say hello!</p>
        )}
        {messages.map((msg, index) => (
          <div key={index} style={styles.messageItem}>
            <span style={styles.sender}>{msg.senderUsername}</span>
            <span style={styles.content}>{msg.content}</span>
            <span style={styles.timestamp}>
              {msg.createdAt
                ? new Date(msg.createdAt).toLocaleTimeString([], {
                    hour: '2-digit',
                    minute: '2-digit',
                  })
                : ''}
            </span>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      <div style={styles.inputRow}>
        <input
          style={styles.input}
          type="text"
          placeholder="Type a message..."
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyDown={handleKeyDown}
        />
        <button style={styles.button} onClick={handleSend}>
          Send
        </button>
      </div>

    </div>
  )
}

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    height: '100vh',
    maxWidth: '700px',
    margin: '0 auto',
    fontFamily: 'sans-serif',
    backgroundColor: '#0D1117',
    color: '#fff',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '12px 16px',
    backgroundColor: '#161B22',
    borderBottom: '1px solid #30363D',
    fontSize: '14px',
  },
  messageList: {
    flex: 1,
    overflowY: 'auto',
    padding: '16px',
    display: 'flex',
    flexDirection: 'column',
    gap: '10px',
  },
  empty: {
    color: '#8B949E',
    textAlign: 'center',
    marginTop: '40px',
    fontSize: '14px',
  },
  messageItem: {
    display: 'flex',
    flexDirection: 'column',
    backgroundColor: '#161B22',
    padding: '10px 14px',
    borderRadius: '8px',
    gap: '4px',
  },
  sender: {
    color: '#58A6FF',
    fontSize: '12px',
    fontWeight: 'bold',
  },
  content: {
    fontSize: '14px',
    color: '#ffffff',
  },
  timestamp: {
    fontSize: '11px',
    color: '#8B949E',
    alignSelf: 'flex-end',
  },
  inputRow: {
    display: 'flex',
    padding: '12px 16px',
    gap: '8px',
    borderTop: '1px solid #30363D',
    backgroundColor: '#161B22',
  },
  input: {
    flex: 1,
    padding: '10px 14px',
    borderRadius: '6px',
    border: '1px solid #30363D',
    backgroundColor: '#0D1117',
    color: '#fff',
    fontSize: '14px',
    outline: 'none',
  },
  button: {
    padding: '10px 20px',
    borderRadius: '6px',
    border: 'none',
    backgroundColor: '#238636',
    color: '#fff',
    fontSize: '14px',
    cursor: 'pointer',
  },
}

export default Chat
