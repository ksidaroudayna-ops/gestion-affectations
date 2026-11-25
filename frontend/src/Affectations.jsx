import { useEffect, useState } from 'react'
import { api } from './api.js'

const TYPES = ['CM', 'TD', 'TP']

export default function Affectations({ canEdit }) {
  const [rows, setRows] = useState([])
  const [ens, setEns] = useState([])
  const [mats, setMats] = useState([])
  const [annee, setAnnee] = useState('2025-2026')
  const [form, setForm] = useState({ enseignantId: '', matiereId: '', typeSeance: 'CM', heures: 10 })
  const [editId, setEditId] = useState(null)
  const [charge, setCharge] = useState(null)
  const [error, setError] = useState('')

  const load = () => Promise.all([api.list('affectations'), api.list('enseignants'), api.list('matieres')])
    .then(([a, e, m]) => {
      setRows(a); setEns(e); setMats(m)
      setForm(f => ({ ...f, enseignantId: f.enseignantId || e[0]?.id || '', matiereId: f.matiereId || m[0]?.id || '' }))
    }).catch(e => setError(e.message))
  useEffect(() => { load() }, [])

  useEffect(() => {
    if (form.enseignantId && /^\d{4}-\d{4}$/.test(annee))
      api.charge(form.enseignantId, annee).then(setCharge).catch(() => setCharge(null))
  }, [form.enseignantId, annee, rows])

  async function submit(e) {
    e.preventDefault()
    setError('')
    const body = { ...form, enseignantId: Number(form.enseignantId), matiereId: Number(form.matiereId),
      heures: Number(form.heures), anneeUniversitaire: annee }
    try {
      if (editId) await api.update('affectations', editId, body)
      else await api.create('affectations', body)
      setEditId(null); load()
    } catch (err) { setError(err.rule ? `${err.rule} — ${err.message}` : err.message) }
  }

  async function remove(id) {
    if (!confirm('Supprimer cette affectation ?')) return
    try { await api.remove('affectations', id); load() } catch (err) { setError(err.message) }
  }

  function edit(a) {
    setEditId(a.id); setAnnee(a.anneeUniversitaire)
    setForm({ enseignantId: a.enseignant.id, matiereId: a.matiere.id, typeSeance: a.typeSeance, heures: a.heures })
  }

  const pct = charge ? Math.min(100, Math.round(100 * charge.heuresAffectees / charge.chargeMax)) : 0

  return (
    <section>
      <h2>Affectations</h2>
      {error && <p className="error">{error}</p>}
      {canEdit && (
        <form className="card grid" onSubmit={submit}>
          <label>Enseignant
            <select value={form.enseignantId} onChange={e => setForm({ ...form, enseignantId: e.target.value })}>
              {ens.map(x => <option key={x.id} value={x.id}>{x.nom} {x.prenom}</option>)}
            </select></label>
          <label>Matière
            <select value={form.matiereId} onChange={e => setForm({ ...form, matiereId: e.target.value })}>
              {mats.map(x => <option key={x.id} value={x.id}>{x.code} — {x.intitule}</option>)}
            </select></label>
          <label>Type
            <select value={form.typeSeance} onChange={e => setForm({ ...form, typeSeance: e.target.value })}>
              {TYPES.map(t => <option key={t}>{t}</option>)}
            </select></label>
          <label>Heures
            <input type="number" min="1" value={form.heures} onChange={e => setForm({ ...form, heures: e.target.value })} /></label>
          <label>Année universitaire
            <input value={annee} onChange={e => setAnnee(e.target.value)} placeholder="2025-2026" /></label>
          <div className="actions">
            <button>{editId ? 'Enregistrer' : 'Affecter'}</button>
            {editId && <button type="button" className="secondary" onClick={() => setEditId(null)}>Annuler</button>}
          </div>
          {charge && (
            <div className="charge">
              Charge de l'enseignant : {charge.heuresAffectees} / {charge.chargeMax} h
              ({charge.heuresRestantes} h restantes)
              <div className="bar"><div style={{ width: pct + '%' }} className={pct >= 100 ? 'full' : ''} /></div>
            </div>
          )}
        </form>
      )}
      <table>
        <thead><tr><th>Année</th><th>Enseignant</th><th>Matière</th><th>Type</th><th>Heures</th>{canEdit && <th></th>}</tr></thead>
        <tbody>
          {rows.map(a => (
            <tr key={a.id}>
              <td>{a.anneeUniversitaire}</td>
              <td>{a.enseignant.nom} {a.enseignant.prenom}</td>
              <td>{a.matiere.code} — {a.matiere.intitule}</td>
              <td>{a.typeSeance}</td><td>{a.heures}</td>
              {canEdit && <td className="actions">
                <button className="secondary" onClick={() => edit(a)}>Modifier</button>
                <button className="danger" onClick={() => remove(a.id)}>Supprimer</button>
              </td>}
            </tr>
          ))}
          {rows.length === 0 && <tr><td colSpan="6">Aucune affectation.</td></tr>}
        </tbody>
      </table>
    </section>
  )
}
