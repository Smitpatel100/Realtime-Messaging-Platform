import { useState, useEffect } from 'react'
import chatService from '../../services/chatService'
import './createChatModal.css'

const initialFormState = {
  activeTab: 'private',
  selectedMembers: new Set(),
  groupName: '',
  error: '',
}

const CreateChatModal = ({ onClose, onRoomCreated }) => {
  const [formState, setFormState] = useState(initialFormState)
  const [users, setUsers] = useState([])
  const [loadingUsers, setLoadingUsers] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  const { activeTab, selectedMembers, groupName, error } = formState

  useEffect(() => {
    let cancelled = false

    chatService.getAllUsers()
      .then((res) => {
        if (!cancelled) setUsers(res.data)
      })
      .catch(() => {
        if (!cancelled) {
          setFormState((prev) => ({ ...prev, error: 'Failed to load users' }))
        }
      })
      .finally(() => {
        if (!cancelled) setLoadingUsers(false)
      })

    return () => {
      cancelled = true
    }
  }, [])

  const setActiveTab = (tab) => {
    setFormState((prev) => ({ ...prev, activeTab: tab, error: '' }))
  }

  const handlePrivateSelect = (user) => {
    if (submitting) return
    setSubmitting(true)
    setFormState((prev) => ({ ...prev, error: '' }))

    chatService.createPrivateChat(user.email)
      .then((res) => {
        onRoomCreated(res.data)
      })
      .catch((err) => {
        setFormState((prev) => ({
          ...prev,
          error: err?.response?.data?.message || 'Failed to create private chat',
        }))
      })
      .finally(() => setSubmitting(false))
  }

  const toggleMember = (email) => {
    setFormState((prev) => {
      const next = new Set(prev.selectedMembers)
      if (next.has(email)) {
        next.delete(email)
      } else {
        next.add(email)
      }
      return { ...prev, selectedMembers: next }
    })
  }

  const handleGroupNameChange = (value) => {
    setFormState((prev) => ({ ...prev, groupName: value }))
  }

  const handleCreateGroup = () => {
    if (submitting) return

    if (!groupName.trim()) {
      setFormState((prev) => ({ ...prev, error: 'Group name is required' }))
      return
    }
    if (selectedMembers.size === 0) {
      setFormState((prev) => ({ ...prev, error: 'Select at least one member' }))
      return
    }

    setSubmitting(true)
    setFormState((prev) => ({ ...prev, error: '' }))

    chatService.createGroupChat(groupName.trim(), Array.from(selectedMembers))
      .then((res) => {
        onRoomCreated(res.data)
      })
      .catch((err) => {
        setFormState((prev) => ({
          ...prev,
          error: err?.response?.data?.message || 'Failed to create group chat',
        }))
      })
      .finally(() => setSubmitting(false))
  }

  return (
    <div className="cc-overlay" onClick={onClose}>
      <div className="cc-modal" onClick={(e) => e.stopPropagation()}>
        <div className="cc-header">
          <div className="cc-title">New Chat</div>
          <button className="cc-close-btn" onClick={onClose} disabled={submitting}>✕</button>
        </div>

        <div className="cc-tabs">
          <button
            className={`cc-tab ${activeTab === 'private' ? 'active' : ''}`}
            onClick={() => setActiveTab('private')}
            disabled={submitting}
          >
            Private Chat
          </button>
          <button
            className={`cc-tab ${activeTab === 'group' ? 'active' : ''}`}
            onClick={() => setActiveTab('group')}
            disabled={submitting}
          >
            Group Chat
          </button>
        </div>

        {error && <div className="cc-error">{error}</div>}

        {activeTab === 'private' && (
          <div className="cc-body">
            {loadingUsers ? (
              <div className="cc-loading">Loading users...</div>
            ) : users.length === 0 ? (
              <div className="cc-empty">No other users found</div>
            ) : (
              <div className="cc-user-list">
                {users.map((user) => (
                  <div
                    key={user.id}
                    className={`cc-user-item ${submitting ? 'disabled' : ''}`}
                    onClick={() => handlePrivateSelect(user)}
                  >
                    <div className="cc-user-avatar">
                      {user.username?.slice(0, 2).toUpperCase() || '??'}
                    </div>
                    <div className="cc-user-info">
                      <div className="cc-user-name">{user.username}</div>
                      <div className="cc-user-email">{user.email}</div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'group' && (
          <div className="cc-body">
            <input
              className="cc-group-name-input"
              type="text"
              placeholder="Group name"
              value={groupName}
              onChange={(e) => handleGroupNameChange(e.target.value)}
              disabled={submitting}
            />

            {loadingUsers ? (
              <div className="cc-loading">Loading users...</div>
            ) : users.length === 0 ? (
              <div className="cc-empty">No other users found</div>
            ) : (
              <div className="cc-user-list">
                {users.map((user) => (
                  <label key={user.id} className="cc-user-item cc-checkbox-item">
                    <input
                      type="checkbox"
                      checked={selectedMembers.has(user.email)}
                      onChange={() => toggleMember(user.email)}
                      disabled={submitting}
                    />
                    <div className="cc-user-avatar">
                      {user.username?.slice(0, 2).toUpperCase() || '??'}
                    </div>
                    <div className="cc-user-info">
                      <div className="cc-user-name">{user.username}</div>
                      <div className="cc-user-email">{user.email}</div>
                    </div>
                  </label>
                ))}
              </div>
            )}

            <button
              className="cc-create-btn"
              onClick={handleCreateGroup}
              disabled={submitting}
            >
              {submitting ? 'Creating...' : 'Create Group'}
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

export default CreateChatModal