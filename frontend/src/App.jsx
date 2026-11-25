import { useState } from 'react'
import { api, session } from './api.js'
import Crud from './Crud.jsx'
import Affectations from './Affectations.jsx'

function Login({ onLogin }) {
  const [username, setU] = useState('admin')
  const [password, setP] = useState('')
  const [error, setError] = useState('')

  async function submit(e) {
    e.preventDefault()
    setError('')
    try {
      const s = await api.login(username, password)
      session.set(s)
      onLogin(s)
    } catch (err) { setError(err.message) }
  }

  return (
    <form className="card login" onSubmit={submit}>
      <h1>Gestion des affectations</h1>
      <label>Utilisateur<input value={username} onChange={e => setU(e.target.value)} /></label>
      <label>Mot de passe<input type="password" value={password} onChange={e => setP(e.target.value)} /></label>
      {error && <p className="error">{error}</p>}
      <button>Se connecter</button>
    </form>
  )
}

const GRADES = ['PROFESSEUR', 'MAITRE_CONFERENCES', 'MAITRE_ASSISTANT', 'ASSISTANT', 'VACATAIRE']

const ensConfig = {
  resource: 'enseignants',
  title: 'Enseignants',
  fields: [
    { name: 'nom', label: 'Nom', required: true },
    { name: 'prenom', label: 'Prénom', required: true },
    { name: 'email', label: 'Email', type: 'email', required: true },
    { name: 'grade', label: 'Grade', options: GRADES, required: true },
    { name: 'departement', label: 'Département' },
  ],
  columns: ['nom', 'prenom', 'email', 'grade', 'departement', 'chargeMax'],
  headers: { chargeMax: 'Charge max (h)' },
}

const matConfig = {
  resource: 'matieres',
  title: 'Matières',
  fields: [
    { name: 'code', label: 'Code', required: true },
    { name: 'intitule', label: 'Intitulé', required: true },
    { name: 'volumeHoraire', label: 'Volume horaire (h)', type: 'number', required: true },
    { name: 'semestre', label: 'Semestre', options: [1, 2], type: 'number', required: true },
  ],
  columns: ['code', 'intitule', 'volumeHoraire', 'semestre'],
  headers: { volumeHoraire: 'Volume (h)' },
}

export default function App() {
  const [user, setUser] = useState(session.get())
  const [tab, setTab] = useState('affectations')
  if (!user) return <Login onLogin={setUser} />
  const isAdmin = user.role === 'ADMIN'

  return (
    <div className="app">
      <header>
        <strong>Gestion des affectations</strong>
        <nav>
          {[['affectations', 'Affectations'], ['enseignants', 'Enseignants'], ['matieres', 'Matières']].map(([k, l]) => (
            <button key={k} className={tab === k ? 'active' : ''} onClick={() => setTab(k)}>{l}</button>
          ))}
        </nav>
        <span>{user.username} ({user.role})
          <button className="link" onClick={() => { session.clear(); setUser(null) }}>Déconnexion</button>
        </span>
      </header>
      <main>
        {tab === 'enseignants' && <Crud key="e" config={ensConfig} canEdit={isAdmin} />}
        {tab === 'matieres' && <Crud key="m" config={matConfig} canEdit={isAdmin} />}
        {tab === 'affectations' && <Affectations canEdit={isAdmin} />}
      </main>
    </div>
  )
}
