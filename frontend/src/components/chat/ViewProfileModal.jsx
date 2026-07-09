import { useState, useEffect } from 'react'
import profileService from '../../services/profileService'
import './viewProfileModal.css'

const ViewProfileModal = ({ email, onClose }) => {
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!email) return

    let cancelled = false

    profileService.getPublicProfile(email)
      .then((res) => {
        if (!cancelled) setProfile(res.data)
      })
      .catch(() => {
        if (!cancelled) setError('Failed to load profile')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [email])

  if (!email) return null

  return (
    <div className="vp-overlay" onClick={onClose}>
      <div className="vp-modal" onClick={(e) => e.stopPropagation()}>
        <button className="vp-close-btn" onClick={onClose}>✕</button>

        {loading ? (
          <div className="vp-loading">Loading profile...</div>
        ) : error ? (
          <div className="vp-error">{error}</div>
        ) : (
          <>
            <div className="vp-avatar-wrapper">
              <img
                src={profile?.profileImage}
                alt="avatar"
                className="vp-avatar"
                onError={(e) => {
                  e.currentTarget.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(profile?.username || 'User')}`
                }}
              />
            </div>
            <div className="vp-username">{profile?.username}</div>
            <div className="vp-email">{profile?.email}</div>
            <div className="vp-bio-label">Bio</div>
            <div className="vp-bio">
              {profile?.bio ? profile.bio : <span className="vp-bio-empty">No bio yet</span>}
            </div>
          </>
        )}
      </div>
    </div>
  )
}

export default ViewProfileModal