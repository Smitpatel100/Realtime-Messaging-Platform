import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import useAuth from '../context/useAuth'
import authService from '../services/authService'
import './Auth.css'

const LoginPage = () => {
  const navigate = useNavigate()
  const { login } = useAuth()

  const [form, setForm] = useState({ email: '', password: '' })
  const [errors, setErrors] = useState({})
  const [serverError, setServerError] = useState('')
  const [loading, setLoading] = useState(false)

  const validate = () => {
    const newErrors = {}
    if (!form.email.trim()) newErrors.email = 'Email is required'
    else if (!/\S+@\S+\.\S+/.test(form.email)) newErrors.email = 'Enter a valid email'
    if (!form.password.trim()) newErrors.password = 'Password is required'
    return newErrors
  }

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setErrors({ ...errors, [e.target.name]: '' })
    setServerError('')
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
      const response = await authService.login({ email: form.email, password: form.password })
      const token = response.data.token
      login(token, null)
      navigate('/chat')
    } catch (err) {
      setServerError(
        err.response?.data?.message || 'Invalid email or password. Please try again.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1 className="auth-title">Welcome Back</h1>
        <p className="auth-subtitle">Sign in to your account</p>

        {serverError && <div className="error-box">{serverError}</div>}

        <form onSubmit={handleSubmit} noValidate>
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
              placeholder="Enter your password"
              value={form.password}
              onChange={handleChange}
            />
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>

          <button className="auth-button" type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <p className="auth-link-text">
          Don't have an account?
          <Link className="auth-link" to="/register">Register</Link>
        </p>
      </div>
    </div>
  )
}

export default LoginPage