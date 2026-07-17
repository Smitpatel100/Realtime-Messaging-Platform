import { useEffect, useRef } from 'react'
import MessageBubble from './MessageBubble'

const MessageList = ({ messages, currentUserEmail, loading, searchActive, searchKeyword, onDeleteForMe, onDeleteForEveryone }) => {
  const bottomRef = useRef(null)

  useEffect(() => {
    if (!searchActive && bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [messages, searchActive])

  if (loading) {
    return <div className="message-list"><div className="loading-spinner">Loading messages...</div></div>
  }

  if (!messages || messages.length === 0) {
    return (
      <div className="message-list">
        <div className="message-list-empty">
          {searchActive ? 'No matching messages found.' : 'No messages yet. Say hello!'}
        </div>
      </div>
    )
  }

  return (
    <div className="message-list">
      {messages.map((msg, index) => (
        <MessageBubble
          key={msg.id || index}
          message={msg}
          currentUserEmail={currentUserEmail}
          highlightKeyword={searchActive ? searchKeyword : ''}
          onDeleteForMe={onDeleteForMe}
          onDeleteForEveryone={onDeleteForEveryone}
        />
      ))}
      {!searchActive && <div ref={bottomRef} />}
    </div>
  )
}

export default MessageList