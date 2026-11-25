const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8081/api'

export const session = {
  get() { try { return JSON.parse(localStorage.getItem('session')) } catch { return null } },
  set(s) { localStorage.setItem('session', JSON.stringify(s)) },
  clear() { localStorage.removeItem('session') },
}

async function request(path, options = {}) {
  const s = session.get()
  const res = await fetch(BASE + path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(s ? { Authorization: `Bearer ${s.token}` } : {}),
    },
  })
  if (res.status === 401 && path !== '/auth/login') { session.clear(); location.reload() }
  if (res.status === 204) return null
  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    const err = new Error(data.message || `Erreur ${res.status}`)
    err.rule = data.rule
    throw err
  }
  return data
}

export const api = {
  login: (username, password) =>
    request('/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) }),
  list: (res) => request(`/${res}`),
  create: (res, body) => request(`/${res}`, { method: 'POST', body: JSON.stringify(body) }),
  update: (res, id, body) => request(`/${res}/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  remove: (res, id) => request(`/${res}/${id}`, { method: 'DELETE' }),
  charge: (id, annee) => request(`/affectations/charge?enseignantId=${id}&annee=${annee}`),
}
