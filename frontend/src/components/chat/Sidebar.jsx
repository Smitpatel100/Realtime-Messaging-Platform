import { useNavigate } from 'react-router-dom'
import RoomList from './RoomList'
import '../../components/chat/Chat.css'

const Sidebar = ({ user, rooms, selectedRoom, onSelectRoom, onLogout, onlineEmails, onOpenCreateChat, unreadCounts }) => {
  const navigate = useNavigate()

  const initials = user?.username
    ? user.username.slice(0, 2).toUpperCase()
    : user?.email?.slice(0, 2).toUpperCase() || '??'

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <div className="sidebar-logo">💬 RealTimeChat</div>
        <div
          className="sidebar-user sidebar-user-clickable"
          onClick={() => navigate('/profile')}
          title="View profile"
        >
          <div className="sidebar-avatar-wrapper">
            {user?.profileImage ? (
              <img
                src={user.profileImage}
                alt="avatar"
                className="sidebar-avatar-img"
                onError={(e) => {
                  e.currentTarget.style.display = 'none'
                  e.currentTarget.nextSibling.style.display = 'flex'
                }}
              />
            ) : null}
            <div className="sidebar-avatar" style={{ display: user?.profileImage ? 'none' : 'flex' }}>
              {initials}
            </div>
            <span className="presence-dot online sidebar-presence-dot" />
          </div>
          <div className="sidebar-user-info">
            <div className="sidebar-username">{user?.username || 'User'}</div>
            <div className="sidebar-email">{user?.email || ''}</div>
          </div>
        </div>
        <button className="logout-btn" onClick={onLogout}>Sign Out</button>
      </div>
      <div className="new-chat-btn-wrapper">
        <button className="new-chat-btn" onClick={onOpenCreateChat}>+ New Chat</button>
      </div>
      <RoomList
        rooms={rooms}
        selectedRoom={selectedRoom}
        onSelectRoom={onSelectRoom}
        onlineEmails={onlineEmails}
        currentUserEmail={user?.email}
        unreadCounts={unreadCounts}
      />
    </div>
  )
}

export default Sidebar