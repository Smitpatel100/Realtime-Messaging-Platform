import { useState, useRef } from 'react'
import fileService from '../../services/fileService'

const ACCEPTED_TYPES = [
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/gif',
  'application/pdf',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/zip',
  'application/x-zip-compressed',
  'text/plain',
].join(',')

const MessageInput = ({ onSend, onTyping, disabled }) => {
  const [value, setValue] = useState('')
  const [pendingAttachment, setPendingAttachment] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [uploadProgress, setUploadProgress] = useState(0)
  const [uploadError, setUploadError] = useState('')

  const fileInputRef = useRef(null)

  const isBusy = disabled || uploading

  const handleAttachClick = () => {
    if (isBusy) return
    fileInputRef.current?.click()
  }

  const handleFileSelect = (e) => {
    const file = e.target.files?.[0]
    if (!file) return

    if (file.size > 10 * 1024 * 1024) {
      setUploadError('File must be 10MB or smaller')
      e.target.value = ''
      return
    }

    setUploading(true)
    setUploadProgress(0)
    setUploadError('')

    fileService.uploadFile(file, (percent) => setUploadProgress(percent))
      .then((res) => {
        setPendingAttachment(res.data)
      })
      .catch((err) => {
        setUploadError(err?.response?.data?.message || 'Failed to upload file')
      })
      .finally(() => {
        setUploading(false)
        e.target.value = ''
      })
  }

  const removeAttachment = () => {
    setPendingAttachment(null)
    setUploadError('')
  }

  const handleSend = () => {
    const trimmed = value.trim()
    if (isBusy) return
    if (!trimmed && !pendingAttachment) return

    onSend({
      content: trimmed,
      fileName: pendingAttachment?.fileName || null,
      fileUrl: pendingAttachment?.fileUrl || null,
      fileType: pendingAttachment?.fileType || null,
    })

    setValue('')
    setPendingAttachment(null)
    setUploadError('')
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  const handleChange = (e) => {
    setValue(e.target.value)
    if (onTyping) {
      onTyping()
    }
  }

  const isImageAttachment = pendingAttachment?.fileType?.startsWith('image/')

  return (
    <div className="message-input-wrapper">
      {uploading && (
        <div className="attachment-upload-progress">
          <div className="attachment-upload-bar">
            <div
              className="attachment-upload-bar-fill"
              style={{ width: `${uploadProgress}%` }}
            />
          </div>
          <span className="attachment-upload-label">Uploading... {uploadProgress}%</span>
        </div>
      )}

      {uploadError && <div className="attachment-upload-error">{uploadError}</div>}

      {pendingAttachment && !uploading && (
        <div className="attachment-preview-chip">
          {isImageAttachment ? (
            <img
              src={pendingAttachment.fileUrl}
              alt={pendingAttachment.fileName}
              className="attachment-preview-thumb"
            />
          ) : (
            <span className="attachment-preview-icon">📄</span>
          )}
          <span className="attachment-preview-name">{pendingAttachment.fileName}</span>
          <button
            className="attachment-preview-remove"
            onClick={removeAttachment}
            disabled={disabled}
          >
            ✕
          </button>
        </div>
      )}

      <div className="message-input-container">
        <button
          className="attach-btn"
          onClick={handleAttachClick}
          disabled={isBusy}
          title="Attach file"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M12 5v14M5 12h14"
              stroke="currentColor"
              strokeWidth="2.4"
              strokeLinecap="round"
            />
          </svg>
        </button>
        <input
          ref={fileInputRef}
          type="file"
          accept={ACCEPTED_TYPES}
          style={{ display: 'none' }}
          onChange={handleFileSelect}
        />

        <input
          className="message-input"
          type="text"
          placeholder="Type a message..."
          value={value}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
          disabled={isBusy}
        />
        <button
          className="send-btn"
          onClick={handleSend}
          disabled={isBusy || (!value.trim() && !pendingAttachment)}
        >
          ➤
        </button>
      </div>
    </div>
  )
}

export default MessageInput