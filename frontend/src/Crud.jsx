import { useEffect, useState } from 'react'
import { api } from './api.js'

export default function Crud({ config, canEdit }) {
  const { resource, title, fields, columns, headers = {} } = config
  const empty = Object.fromEntries(fields.map(f => [f.name, f.options ? f.options[0] : '']))
  const [rows, setRows] = useState([])
  const [form, setForm] = useState(empty)
  const [editId, setEditId] = useState(null)
  const [error, setError] = useState('')

  const load = () => api.list(resource).then(setRows).catch(e => setError(e.message))
  useEffect(() => { load() }, [resource])

  async function submit(e) {
    e.preventDefault()
    setError('')
    const body = { ...form }
    fields.forEach(f => { if (f.type === 'number') body[f.name] = Number(body[f.name]) })
    try {
      if (editId) await api.update(resource, editId, body)
      else await api.create(resource, body)
      setForm(empty); setEditId(null); load()
    } catch (err) { setError(err.rule ? `${err.rule} — ${err.message}` : err.message) }
  }

  async function remove(id) {
    if (!confirm('Supprimer cet élément ?')) return
    setError('')
    try { await api.remove(resource, id); load() }
    catch (err) { setError(err.rule ? `${err.rule} — ${err.message}` : err.message) }
  }

  function edit(r) {
    setEditId(r.id)
    setForm(Object.fromEntries(fields.map(f => [f.name, r[f.name] ?? ''])))
  }

  return (
    <section>
      <h2>{title}</h2>
      {error && <p className="error">{error}</p>}
      {canEdit && (
        <form className="card grid" onSubmit={submit}>
          {fields.map(f => (
            <label key={f.name}>{f.label}
              {f.options ? (
                <select value={form[f.name]} onChange={e => setForm({ ...form, [f.name]: e.target.value })}>
                  {f.options.map(o => <option key={o}>{o}</option>)}
                </select>
              ) : (
                <input type={f.type || 'text'} required={f.required} value={form[f.name]}
                  onChange={e => setForm({ ...form, [f.name]: e.target.value })} />
              )}
            </label>
          ))}
          <div className="actions">
            <button>{editId ? 'Enregistrer' : 'Ajouter'}</button>
            {editId && <button type="button" className="secondary" onClick={() => { setEditId(null); setForm(empty) }}>Annuler</button>}
          </div>
        </form>
      )}
      <table>
        <thead><tr>{columns.map(c => <th key={c}>{headers[c] || c}</th>)}{canEdit && <th></th>}</tr></thead>
        <tbody>
          {rows.map(r => (
            <tr key={r.id}>
              {columns.map(c => <td key={c}>{r[c]}</td>)}
              {canEdit && <td className="actions">
                <button className="secondary" onClick={() => edit(r)}>Modifier</button>
                <button className="danger" onClick={() => remove(r.id)}>Supprimer</button>
              </td>}
            </tr>
          ))}
          {rows.length === 0 && <tr><td colSpan={columns.length + 1}>Aucune donnée.</td></tr>}
        </tbody>
      </table>
    </section>
  )
}
