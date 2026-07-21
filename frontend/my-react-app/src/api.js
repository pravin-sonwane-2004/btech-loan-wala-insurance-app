// ============================================
// API CONFIG AND HELPER
// ============================================

export const API_BASE = 'http://localhost:8080'

export const TOKENS = {
  ADMIN: 'SAWAI_ADMIN_TOKEN_2026',
  AGENT: 'SAWAI_AGENT_TOKEN_2026'
}

export async function api(path, options = {}) {
  const token = TOKENS[localStorage.getItem('role') || 'ADMIN']
  const res = await fetch(API_BASE + path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'X-Auth-Token': token,
      ...(options.headers || {})
    }
  })
  if (res.status === 204) return null
  const data = await res.json()
  if (!res.ok) throw new Error(data?.message || res.statusText)
  return data
}