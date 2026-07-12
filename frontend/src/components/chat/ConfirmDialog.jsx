const ConfirmDialog = ({ title, message, confirmLabel, onConfirm, onCancel, danger }) => {
  return (
    <div className="confirm-dialog-overlay" onClick={onCancel}>
      <div className="confirm-dialog" onClick={(e) => e.stopPropagation()}>
        <div className="confirm-dialog-title">{title}</div>
        <div className="confirm-dialog-message">{message}</div>
        <div className="confirm-dialog-actions">
          <button className="confirm-dialog-cancel-btn" onClick={onCancel}>Cancel</button>
          <button
            className={`confirm-dialog-confirm-btn ${danger ? 'danger' : ''}`}
            onClick={onConfirm}
          >
            {confirmLabel || 'Confirm'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default ConfirmDialog