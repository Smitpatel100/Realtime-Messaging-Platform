import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import authService from '../services/authService'
import './Auth.css'

const RegisterPage = () => {
  const navigate = useNavigate()

  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [errors, setErrors] = useState({})
  const [serverError, setServerError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  const validate = () => {
    const newErrors = {}
    if (!form.username.trim()) newErrors.username = 'Username is required'
    else if (form.username.trim().length < 3) newErrors.username = 'Username must be at least 3 characters'
    if (!form.email.trim()) newErrors.email = 'Email is required'
    else if (!/\S+@\S+\.\S+/.test(form.email)) newErrors.email = 'Enter a valid email'
    if (!form.password.trim()) newErrors.password = 'Password is required'
    else if (form.password.length < 8) newErrors.password = 'Password must be at least 8 characters'
    return newErrors
  }

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setErrors({ ...errors, [e.target.name]: '' })
    setServerError('')
    setSuccess('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const validationErrors = validate()
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }
    setLoading(true)
    try {
      await authService.register({
        username: form.username,
        email: form.email,
        password: form.password,
      })
      setSuccess('Account created! Redirecting to login...')
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      setServerError(
        err.response?.data?.message || 'Registration failed. Please try again.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1 className="auth-title">Create Account</h1>
        <p className="auth-subtitle">Join the platform today</p>

        {serverError && <div className="error-box">{serverError}</div>}
        {success && <div className="success-box">{success}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              className={`form-input ${errors.username ? 'error-input' : ''}`}
              type="text"
              name="username"
              placeholder="Enter your username"
              value={form.username}
              onChange={handleChange}
            />
            {errors.username && <span className="field-error">{errors.username}</span>}
          </div>

          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              className={`form-input ${errors.email ? 'error-input' : ''}`}
              type="email"
              name="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={handleChange}
            />
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              className={`form-input ${errors.password ? 'error-input' : ''}`}
              type="password"
              name="password"
              placeholder="Minimum 8 characters"
              value={form.password}
              onChange={handleChange}
            />
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>

          <button className="auth-button" type="submit" disabled={loading}>
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <p className="auth-link-text">
          Already have an account?
          <Link className="auth-link" to="/login">Sign In</Link>
        </p>
      </div>
    </div>
  )
}

export default RegisterPage