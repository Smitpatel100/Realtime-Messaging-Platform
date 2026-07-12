import { useState, useRef, useEffect } from 'react'
import ConfirmDialog from './ConfirmDialog'

const initialSearchState = { isOpen: false, value: '' }

const ChatHeader = ({
  room, onlineEmails, currentUserEmail, onOpenProfile, onSearch, searching,
  onDeletePrivateChat, onLeaveGroup, onDeleteGroup,
}) => {
  const [searchState, setSearchState] = useState(initialSearchState)
  const [roomMenuOpen, setRoomMenuOpen] = useState(false)
  const [confirmAction, setConfirmAction] = useState(null)
  const debounceRef = useRef(null)
  const inputRef = useRef(null)

  useEffect(() => {
    if (searchState.isOpen && inputRef.current) {
      inputRef.current.focus()
    }
  }, [searchState.isOpen])

  if (!room) return null

  const isOnline = () => {
    if (room.type !== 'PRIVATE') return false
    if (!room.memberEmails || !onlineEmails) return false
    return room.memberEmails.some((email) => onlineEmails.has(email))
  }

  const getOtherEmail = () => {
    if (room.type !== 'PRIVATE' || !room.memberEmails) return null
    return room.memberEmails.find((email) => email !== currentUserEmail) || null
  }

  const online = isOnline()
  const otherEmail = getOtherEmail()
  const clickable = room.type === 'PRIVATE' && !!otherEmail
  const isAdmin = room.type === 'GROUP' && room.createdByEmail === currentUserEmail

  const handleRoomMenuClick = (e) => {
    e.stopPropagation()
    setRoomMenuOpen((prev) => !prev)
  }

  const requestConfirm = (action) => {
    setRoomMenuOpen(false)
    setConfirmAction(action)
  }

  const runConfirmedAction = () => {
    if (confirmAction === 'delete-private' && onDeletePrivateChat) onDeletePrivateChat(room.id)
    if (confirmAction === 'leave-group' && onLeaveGroup) onLeaveGroup(room.id)
    if (confirmAction === 'delete-group' && onDeleteGroup) onDeleteGroup(room.id)
    setConfirmAction(null)
  }

  const confirmDialogProps = {
    'delete-private': {
      title: 'Delete this chat?',
      message: 'This will permanently delete the chat and all its messages for both participants.',
      confirmLabel: 'Delete',
    },
    'leave-group': {
      title: 'Leave this group?',
      message: 'You will no longer receive messages from this group unless someone adds you back.',
      confirmLabel: 'Leave',
    },
    'delete-group': {
      title: 'Delete this group?',
      message: 'This will permanently delete the group and all its messages for every member.',
      confirmLabel: 'Delete',
    },
  }

  const handleHeaderClick = () => {
    if (clickable && onOpenProfile) {
      onOpenProfile(otherEmail)
    }
  }

  const triggerSearch = (value) => {
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => {
      if (onSearch) onSearch(value)
    }, 300)
  }

  const handleSearchIconClick = (e) => {
    e.stopPropagation()
    setSearchState((prev) => {
      const nextOpen = !prev.isOpen
      if (!nextOpen && onSearch) onSearch('')
      return { isOpen: nextOpen, value: nextOpen ? prev.value : '' }
    })
  }

  const handleSearchChange = (e) => {
    const value = e.target.value
    setSearchState((prev) => ({ ...prev, value }))
    triggerSearch(value)
  }

  const handleCloseSearch = (e) => {
    e.stopPropagation()
    if (debounceRef.current) clearTimeout(debounceRef.current)
    setSearchState(initialSearchState)
    if (onSearch) onSearch('')
  }

  const isSearchOpen = searchState.isOpen
  const searchValue = searchState.value

  return (
    <div
      className={`chat-header ${clickable ? 'chat-header-clickable' : ''}`}
      onClick={handleHeaderClick}
      title={clickable ? 'View profile' : undefined}
    >
      <div className="room-icon-wrapper">
        <div className={`chat-header-icon ${room.type === 'PRIVATE' ? 'private' : ''}`}>
          {room.type === 'PRIVATE' ? '👤' : '👥'}
        </div>
        {room.type === 'PRIVATE' && (
          <span className={`presence-dot ${online ? 'online' : 'offline'} header-presence-dot`} />
        )}
      </div>

      {!isSearchOpen && (
        <div className="chat-header-info">
          <div className="chat-header-name">{room.name}</div>
          <div className="chat-header-meta">
            {room.type === 'PRIVATE'
              ? (online ? '🟢 Online' : '⚪ Offline')
              : `Group · ${room.memberEmails?.length || 0} members`}
          </div>
        </div>
      )}

      {isSearchOpen && (
        <div className="chat-search-box" onClick={(e) => e.stopPropagation()}>
          <svg className="chat-search-input-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="2" />
            <path d="M20 20l-3.5-3.5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
          </svg>
          <input
            ref={inputRef}
            className="chat-search-input"
            type="text"
            placeholder="Search in this chat..."
            value={searchValue}
            onChange={handleSearchChange}
          />
          {searching && <span className="chat-search-spinner" />}
          <button className="chat-search-close-btn" onClick={handleCloseSearch}>✕</button>
        </div>
      )}

      <div className="chat-header-actions">
        <button
          className={`chat-search-toggle-btn ${isSearchOpen ? 'active' : ''}`}
          onClick={handleSearchIconClick}
          title="Search messages"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="2" />
            <path d="M20 20l-3.5-3.5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
          </svg>
        </button>

        <div className="room-menu-wrapper">
          <button
            className={`room-menu-toggle-btn ${roomMenuOpen ? 'active' : ''}`}
            onClick={handleRoomMenuClick}
            title="Chat options"
          >
            ⋮
          </button>
          {roomMenuOpen && (
            <>
              <div className="room-menu-backdrop" onClick={(e) => { e.stopPropagation(); setRoomMenuOpen(false) }} />
              <div className="room-menu-dropdown" onClick={(e) => e.stopPropagation()}>
                {room.type === 'PRIVATE' && (
                  <button
                    className="room-menu-item danger"
                    onClick={() => requestConfirm('delete-private')}
                  >
                    Delete Chat
                  </button>
                )}
                {room.type === 'GROUP' && (
                  <button
                    className="room-menu-item danger"
                    onClick={() => requestConfirm('leave-group')}
                  >
                    Leave Group
                  </button>
                )}
                {room.type === 'GROUP' && isAdmin && (
                  <button
                    className="room-menu-item danger"
                    onClick={() => requestConfirm('delete-group')}
                  >
                    Delete Group
                  </button>
                )}
              </div>
            </>
          )}
        </div>
      </div>

      {confirmAction && (
        <ConfirmDialog
          title={confirmDialogProps[confirmAction].title}
          message={confirmDialogProps[confirmAction].message}
          confirmLabel={confirmDialogProps[confirmAction].confirmLabel}
          danger
          onConfirm={runConfirmedAction}
          onCancel={() => setConfirmAction(null)}
        />
      )}
    </div>
  )
}

export default ChatHeader