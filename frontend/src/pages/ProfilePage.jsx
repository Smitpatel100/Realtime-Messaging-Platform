import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import profileService from '../services/profileService'
import './ProfilePage.css'

const ProfilePage = () => {
  const navigate = useNavigate()
  const fileInputRef = useRef(null)

  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [uploading, setUploading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [username, setUsername] = useState('')
  const [bio, setBio] = useState('')

  useEffect(() => {
    profileService.getProfile()
      .then((res) => {
        setProfile(res.data)
        setUsername(res.data.username || '')
        setBio(res.data.bio || '')
      })
      .catch(() => setError('Failed to load profile'))
      .finally(() => setLoading(false))
  }, [])

  const handleFileSelect = (e) => {
    const file = e.target.files?.[0]
    if (!file) return

    if (file.size > 5 * 1024 * 1024) {
      setError('Image must be 5MB or smaller')
      e.target.value = ''
      return
    }

    setUploading(true)
    setError('')
    setSuccess('')

    profileService.uploadProfileImage(file)
      .then((res) => {
        setProfile(res.data)
        setSuccess('Profile picture updated')
      })
      .catch((err) => {
        setError(err?.response?.data?.message || 'Failed to upload image')
      })
      .finally(() => {
        setUploading(false)
        e.target.value = ''
      })
  }

  const handleSave = () => {
    if (saving) return

    if (!username.trim()) {
      setError('Username is required')
      return
    }

    setSaving(true)
    setError('')
    setSuccess('')

    profileService.updateProfile({
      username: username.trim(),
      bio: bio.trim(),
      profileImage: profile?.profileImage || '',
    })
      .then((res) => {
        setProfile(res.data)
        setUsername(res.data.username || '')
        setBio(res.data.bio || '')
        setSuccess('Profile updated successfully')
      })
      .catch((err) => {
        setError(err?.response?.data?.message || 'Failed to update profile')
      })
      .finally(() => setSaving(false))
  }

  if (loading) {
    return (
      <div className="profile-page">
        <div className="profile-loading">Loading profile...</div>
      </div>
    )
  }

  const busy = saving || uploading

  const openFilePicker = () => {
    if (busy) return
    fileInputRef.current?.click()
  }

  return (
    <div className="profile-page">
      <div className="profile-card">
        <button className="profile-back-btn" onClick={() => navigate('/chat')}>← Back to Chat</button>

        <div className="profile-header">
          <div
            className="profile-avatar-preview-wrapper profile-avatar-clickable"
            onClick={openFilePicker}
            title="Click to change photo"
          >
            <img
              src={profile?.profileImage}
              alt="Profile avatar"
              className="profile-avatar-preview"
              onError={(e) => {
                e.currentTarget.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(username || 'User')}`
              }}
            />
            <div className="profile-avatar-hover-hint">
              {uploading ? 'Uploading...' : '📷 Change'}
            </div>
          </div>

          <button
            className="profile-upload-btn"
            onClick={openFilePicker}
            disabled={busy}
          >
            {uploading ? 'Uploading...' : '📷 Choose Photo'}
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/jpeg,image/png,image/webp,image/gif"
            style={{ display: 'none' }}
            onChange={handleFileSelect}
          />

          <div className="profile-title">Your Profile</div>
        </div>

        {error && <div className="profile-error">{error}</div>}
        {success && <div className="profile-success">{success}</div>}

        <div className="profile-field">
          <label className="profile-label">Email</label>
          <input
            className="profile-input profile-input-readonly"
            type="text"
            value={profile?.email || ''}
            readOnly
            disabled
          />
        </div>

        <div className="profile-field">
          <label className="profile-label">Username</label>
          <input
            className="profile-input"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            disabled={busy}
            placeholder="Username"
          />
        </div>

        <div className="profile-field">
          <label className="profile-label">Bio</label>
          <textarea
            className="profile-textarea"
            value={bio}
            onChange={(e) => setBio(e.target.value)}
            disabled={busy}
            placeholder="Tell others a little about yourself"
            maxLength={255}
            rows={3}
          />
          <div className="profile-char-count">{bio.length}/255</div>
        </div>

        <button className="profile-save-btn" onClick={handleSave} disabled={busy}>
          {saving ? 'Saving...' : 'Save Changes'}
        </button>
      </div>
    </div>
  )
}

export default ProfilePage