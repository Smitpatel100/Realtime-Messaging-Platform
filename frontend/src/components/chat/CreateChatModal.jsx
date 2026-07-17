import { useState } from 'react'
import chatService from '../../services/chatService'
import './createChatModal.css'

const initialFormState = {
  activeTab: 'private',
  error: '',
}

const CreateChatModal = ({ onClose, onRoomCreated }) => {
  const [formState, setFormState] = useState(initialFormState)
  const [submitting, setSubmitting] = useState(false)

  // Private tab state
  const [privateEmail, setPrivateEmail] = useState('')
  const [privateLookupResult, setPrivateLookupResult] = useState(null)
  const [privateLookingUp, setPrivateLookingUp] = useState(false)
  const [privateNotFound, setPrivateNotFound] = useState(false)

  // Group tab state
  const [groupName, setGroupName] = useState('')
  const [groupEmailInput, setGroupEmailInput] = useState('')
  const [groupMembers, setGroupMembers] = useState([])
  const [groupLookingUp, setGroupLookingUp] = useState(false)

  const { activeTab, error } = formState

  const setActiveTab = (tab) => {
    setFormState((prev) => ({ ...prev, activeTab: tab, error: '' }))
  }

  const setError = (message) => {
    setFormState((prev) => ({ ...prev, error: message }))
  }

  const handlePrivateEmailSearch = () => {
    const trimmed = privateEmail.trim()
    if (!trimmed) return

    setPrivateLookingUp(true)
    setPrivateLookupResult(null)
    setPrivateNotFound(false)
    setError('')

    chatService.lookupUserByEmail(trimmed)
      .then((res) => {
        setPrivateLookupResult(res.data)
      })
      .catch(() => {
        setPrivateNotFound(true)
      })
      .finally(() => setPrivateLookingUp(false))
  }

  const handlePrivateEmailKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handlePrivateEmailSearch()
    }
  }

  const handleStartPrivateChat = () => {
    if (submitting || !privateLookupResult) return
    setSubmitting(true)
    setError('')

    chatService.createPrivateChat(privateLookupResult.email)
      .then((res) => {
        onRoomCreated(res.data)
      })
      .catch((err) => {
        setError(err?.response?.data?.message || 'Failed to create private chat')
      })
      .finally(() => setSubmitting(false))
  }

  const handleAddGroupMember = () => {
    const trimmed = groupEmailInput.trim()
    if (!trimmed) return

    if (groupMembers.some((m) => m.email.toLowerCase() === trimmed.toLowerCase())) {
      setError('That member has already been added')
      return
    }

    setGroupLookingUp(true)
    setError('')

    chatService.lookupUserByEmail(trimmed)
      .then((res) => {
        setGroupMembers((prev) => [...prev, res.data])
        setGroupEmailInput('')
      })
      .catch(() => {
        setError('User not found.')
      })
      .finally(() => setGroupLookingUp(false))
  }

  const handleGroupEmailKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleAddGroupMember()
    }
  }

  const removeGroupMember = (email) => {
    setGroupMembers((prev) => prev.filter((m) => m.email !== email))
  }

  const handleCreateGroup = () => {
    if (submitting) return

    if (!groupName.trim()) {
      setError('Group name is required')
      return
    }
    if (groupMembers.length === 0) {
      setError('Add at least one member')
      return
    }

    setSubmitting(true)
    setError('')

    chatService.createGroupChat(groupName.trim(), groupMembers.map((m) => m.email))
      .then((res) => {
        onRoomCreated(res.data)
      })
      .catch((err) => {
        setError(err?.response?.data?.message || 'Failed to create group chat')
      })
      .finally(() => setSubmitting(false))
  }

  const renderUserPreview = (user) => (
    <div className="cc-user-item">
      <div className="cc-user-avatar-img-wrapper">
        {user.profileImage ? (
          <img src={user.profileImage} alt={user.username} className="cc-user-avatar-img" />
        ) : (
          <div className="cc-user-avatar">{user.username?.slice(0, 2).toUpperCase() || '??'}</div>
        )}
      </div>
      <div className="cc-user-info">
        <div className="cc-user-name">{user.username}</div>
        <div className="cc-user-email">{user.email}</div>
      </div>
    </div>
  )

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
            <div className="cc-email-search-row">
              <input
                className="cc-group-name-input"
                type="email"
                placeholder="Enter user email"
                value={privateEmail}
                onChange={(e) => {
                  setPrivateEmail(e.target.value)
                  setPrivateLookupResult(null)
                  setPrivateNotFound(false)
                }}
                onKeyDown={handlePrivateEmailKeyDown}
                disabled={submitting}
              />
              <button
                className="cc-search-btn"
                onClick={handlePrivateEmailSearch}
                disabled={submitting || privateLookingUp || !privateEmail.trim()}
              >
                {privateLookingUp ? '...' : 'Search'}
              </button>
            </div>

            {privateNotFound && <div className="cc-not-found">User not found.</div>}

            {privateLookupResult && (
              <>
                {renderUserPreview(privateLookupResult)}
                <button
                  className="cc-create-btn"
                  onClick={handleStartPrivateChat}
                  disabled={submitting}
                >
                  {submitting ? 'Starting...' : 'Start Chat'}
                </button>
              </>
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
              onChange={(e) => setGroupName(e.target.value)}
              disabled={submitting}
            />

            <div className="cc-email-search-row">
              <input
                className="cc-group-name-input"
                type="email"
                placeholder="Add member by email"
                value={groupEmailInput}
                onChange={(e) => setGroupEmailInput(e.target.value)}
                onKeyDown={handleGroupEmailKeyDown}
                disabled={submitting}
              />
              <button
                className="cc-search-btn"
                onClick={handleAddGroupMember}
                disabled={submitting || groupLookingUp || !groupEmailInput.trim()}
              >
                {groupLookingUp ? '...' : 'Add'}
              </button>
            </div>

            {groupMembers.length > 0 && (
              <div className="cc-user-list">
                {groupMembers.map((member) => (
                  <div key={member.email} className="cc-user-item">
                    <div className="cc-user-avatar-img-wrapper">
                      {member.profileImage ? (
                        <img src={member.profileImage} alt={member.username} className="cc-user-avatar-img" />
                      ) : (
                        <div className="cc-user-avatar">{member.username?.slice(0, 2).toUpperCase() || '??'}</div>
                      )}
                    </div>
                    <div className="cc-user-info">
                      <div className="cc-user-name">{member.username}</div>
                      <div className="cc-user-email">{member.email}</div>
                    </div>
                    <button
                      className="cc-remove-member-btn"
                      onClick={() => removeGroupMember(member.email)}
                      disabled={submitting}
                    >
                      ✕
                    </button>
                  </div>
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