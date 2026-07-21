import React, { useState, useEffect } from 'react'
import { api, API_BASE, TOKENS } from './api'
import { FormFields } from './forms'
import { TableHeaders, TableRow } from './tables'

// ============================================
// APP COMPONENT
// ============================================

export default function App() {
  const [module, setModule] = useState('customers')
  const [role, setRole] = useState(localStorage.getItem('role') || 'ADMIN')
  const [records, setRecords] = useState({ customers: [], policies: [], leads: [] })
  const [form, setForm] = useState({})
  const [editingId, setEditingId] = useState(null)
  const [message, setMessage] = useState('')

  // Load all records on first render
  useEffect(() => { loadAll() }, [])

  function loadAll() {
    setMessage('Loading...')
    Promise.all([
      api('/api/customers'),
      api('/api/policies'),
      api('/api/leads')
    ])
      .then(([c, p, l]) => {
        setRecords({ customers: c, policies: p, leads: l })
        setMessage('')
      })
      .catch(e => setMessage('Error: ' + e.message))
  }

  function startEdit(record) {
    setEditingId(record.id)
    // For policies, customerId must be a number for the select to match
    if (module === 'policies') {
      setForm({ ...record, customerId: String(record.customerId) })
    } else {
      setForm(record)
    }
  }

  function cancelEdit() {
    setEditingId(null)
    setForm({})
  }

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  function handleSave(e) {
    e.preventDefault()
    const method = editingId ? 'PUT' : 'POST'
    const url = `/api/${module}${editingId ? '/' + editingId : ''}`
    // Build payload matching backend DTO exactly
    const payload = buildPayload(module, form)
    api(url, { method, body: JSON.stringify(payload) })
      .then(() => {
        cancelEdit()
        loadAll()
        setMessage('Saved!')
      })
      .catch(e => setMessage('Error: ' + e.message))
  }

  function handleDelete(id) {
    if (!confirm('Delete this record?')) return
    api(`/api/${module}/${id}`, { method: 'DELETE' })
      .then(() => {
        loadAll()
        setMessage('Deleted!')
      })
      .catch(e => setMessage('Error: ' + e.message))
  }

  function handleRoleChange(e) {
    const newRole = e.target.value
    setRole(newRole)
    localStorage.setItem('role', newRole)
  }

  function handleDownload() {
    const token = TOKENS[localStorage.getItem('role') || 'ADMIN']
    fetch(API_BASE + '/api/export', {
      headers: { 'X-Auth-Token': token }
    })
      .then(res => {
        if (!res.ok) throw new Error('Download failed')
        return res.blob()
      })
      .then(blob => {
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = 'insurance_data.csv'
        document.body.appendChild(a)
        a.click()
        a.remove()
        window.URL.revokeObjectURL(url)
        setMessage('Downloaded!')
      })
      .catch(e => setMessage('Error: ' + e.message))
  }

  const currentRecords = records[module] || []

  return (
    <div className="app">
      {/* HEADER */}
      <header className="topbar">
        <div>
          <h1>Sawai Insurance Management</h1>
          <p>Manage customers, policies, and leads</p>
        </div>
        <div className="topbar-right">
          <select value={role} onChange={handleRoleChange}>
            <option value="ADMIN">ADMIN</option>
            <option value="AGENT">AGENT</option>
          </select>
          <button onClick={loadAll}>Refresh</button>
          <button className="download-btn" onClick={handleDownload}>📥 Download CSV</button>
        </div>
      </header>

      <div className="shell">
        {/* TABS */}
        <nav className="tabs">
          {['customers', 'policies', 'leads'].map(m => (
            <button
              key={m}
              className={module === m ? 'tab active' : 'tab'}
              onClick={() => { setModule(m); cancelEdit() }}
            >
              {m.charAt(0).toUpperCase() + m.slice(1)}
            </button>
          ))}
        </nav>

        {/* STATUS MESSAGE */}
        {message && <div className="status">{message}</div>}

        {/* SUMMARY CARDS */}
        <div className="summary">
          {['customers', 'policies', 'leads'].map(m => (
            <div key={m} className="card">
              <strong>{records[m].length}</strong>
              <span>{m.charAt(0).toUpperCase() + m.slice(1)}</span>
            </div>
          ))}
        </div>

        {/* FORM + TABLE */}
        <div className="workspace">
          <div className="panel form-panel">
            <h2>{editingId ? 'Edit' : 'Add'} {module.slice(0, -1)}</h2>
            <form onSubmit={handleSave}>
              <FormFields module={module} form={form} onChange={handleChange} customers={records.customers} />
              <div className="actions">
                <button type="submit">{editingId ? 'Update' : 'Save'}</button>
                {editingId && <button type="button" onClick={cancelEdit}>Cancel</button>}
              </div>
            </form>
          </div>

          <div className="panel table-panel">
            <h3>{module.charAt(0).toUpperCase() + module.slice(1)}</h3>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr><TableHeaders module={module} /></tr>
                </thead>
                <tbody>
                  {currentRecords.length === 0 ? (
                    <tr><td colSpan={99}>No records found.</td></tr>
                  ) : (
                    currentRecords.map(r => (
                      <TableRow key={r.id} record={r} module={module} onEdit={() => startEdit(r)} onDelete={() => handleDelete(r.id)} />
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

// ============================================
// Build JSON payload matching backend DTOs
// ============================================

function buildPayload(module, form) {
  if (module === 'customers') {
    return {
      firstName: form.firstName?.trim() || '',
      lastName: form.lastName?.trim() || '',
      email: form.email?.trim() || '',
      phoneNumber: form.phoneNumber?.trim() || '',
      dateOfBirth: form.dateOfBirth || '',
      accountStatus: form.accountStatus || 'ACTIVE'
    }
  }
  if (module === 'policies') {
    return {
      policyNumber: form.policyNumber?.trim() || '',
      policyName: form.policyName?.trim() || '',
      policyType: form.policyType || 'HEALTH',
      premiumAmount: Number(form.premiumAmount) || 0,
      coverageTermMonths: Number(form.coverageTermMonths) || 0,
      effectiveStartDate: form.effectiveStartDate || '',
      customerId: Number(form.customerId) || null
    }
  }
  return {
    prospectName: form.prospectName?.trim() || '',
    contactInfo: form.contactInfo?.trim() || '',
    referralSource: form.referralSource?.trim() || '',
    leadStatus: form.leadStatus || 'NEW',
    assignedAgentName: form.assignedAgentName?.trim() || ''
  }
}