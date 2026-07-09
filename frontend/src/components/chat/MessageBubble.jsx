const escapeRegExp = (str) => str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')

const renderHighlightedText = (text, keyword) => {
  if (!keyword || !keyword.trim() || !text) return text

  const escaped = escapeRegExp(keyword.trim())
  const regex = new RegExp(`(${escaped})`, 'gi')
  const parts = text.split(regex)

  return parts.map((part, i) =>
    regex.test(part) && part.toLowerCase() === keyword.trim().toLowerCase() ? (
      <mark key={i} className="search-highlight">{part}</mark>
    ) : (
      <span key={i}>{part}</span>
    )
  )
}

const MessageBubble = ({ message, currentUserEmail, highlightKeyword }) => {
  const isOwn = message.senderEmail === currentUserEmail

  const formatTime = (timestamp) => {
    if (!timestamp) return ''
    return new Date(timestamp).toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const renderTicks = () => {
    if (!isOwn) return null

    if (message.seen) {
      return (
        <span
          className="message-status-tick seen"
          title={message.seenAt ? `Seen at ${formatTime(message.seenAt)}` : 'Seen'}
        >
          {' '}✓✓
        </span>
      )
    }

    if (message.delivered) {
      return (
        <span
          className="message-status-tick delivered"
          title={message.deliveredAt ? `Delivered at ${formatTime(message.deliveredAt)}` : 'Delivered'}
        >
          {' '}✓✓
        </span>
      )
    }

    return (
      <span className="message-status-tick sent" title="Sent">
        {' '}✓
      </span>
    )
  }

  const isImageAttachment = message.fileType?.startsWith('image/')
  const hasAttachment = !!message.fileUrl

  return (
    <div className={`message-bubble-wrapper ${isOwn ? 'own' : 'other'}`}>
      {!isOwn && (
        <div className="message-sender">{message.senderUsername || message.senderEmail}</div>
      )}

      <div className="message-bubble">
        {hasAttachment && isImageAttachment && (
          <a href={message.fileUrl} target="_blank" rel="noopener noreferrer">
            <img
              src={message.fileUrl}
              alt={message.fileName || 'attachment'}
              className="message-attachment-image"
            />
          </a>
        )}

        {hasAttachment && !isImageAttachment && (
          <a
            href={message.fileUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="message-attachment-file"
          >
            📄 <span className="message-attachment-filename">{message.fileName}</span>
          </a>
        )}

        {message.content && (
          <div className={hasAttachment ? 'message-attachment-caption' : ''}>
            {highlightKeyword ? renderHighlightedText(message.content, highlightKeyword) : message.content}
          </div>
        )}
      </div>

      <div className="message-time">
        {formatTime(message.createdAt)}
        {renderTicks()}
      </div>
    </div>
  )
}

export default MessageBubble