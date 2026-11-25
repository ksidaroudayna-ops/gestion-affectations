# Gestion des affectations enseignants / matières

Projet de gestion de projet SCRUM (9 semaines) : application web **Spring Boot (API REST + JWT) + React + MySQL**.

```
backend/    API Spring Boot 3 (Java 17+), tests des règles de gestion
frontend/   Interface React (Vite)
database/   schema.sql (MySQL)
uml/        Diagrammes (.mmd source + .png) : cas d'utilisation, classes, séquences, déploiement
```

## Lancer le projet

Prérequis : Java 17+, Maven, Node 18+, MySQL 8 démarré.

```bash
# 1) Backend (crée la base affectations_db automatiquement + données de démo)
cd backend
# adapter DB_USER / DB_PASSWORD si besoin (défaut root/root)
DB_USER=root DB_PASSWORD=root mvn spring-boot:run     # http://localhost:8080

# 2) Frontend
cd frontend
npm install
npm run dev                                           # http://localhost:5173
```

Comptes de démo : `admin / admin123` (lecture + écriture) et `responsable / resp123` (lecture seule).
Tests : `cd backend && mvn test` (base H2 en mémoire, aucun MySQL requis).
Le script `database/schema.sql` est optionnel (Hibernate crée les tables avec `ddl-auto=update`).

## Règles de gestion (RG1 à RG10)

| RG | Règle | Où c'est appliqué |
|----|-------|-------------------|
| RG1 | L'email d'un enseignant est unique | contrainte SQL + `EnseignantService` |
| RG2 | Le code d'une matière est unique | contrainte SQL + `MatiereService` |
| RG3 | Le volume horaire d'une matière est > 0 | validation `@Min(1)` + CHECK SQL |
| RG4 | Le nombre d'heures d'une affectation est > 0 | `AffectationService` + CHECK SQL |
| RG5 | Un enseignant ne dépasse pas sa charge max annuelle selon son grade (Professeur/Maître de conf. 192 h, Maître assistant 224 h, Assistant 240 h, Vacataire 120 h) | `AffectationService` |
| RG6 | Le total d'heures affectées à une matière ne dépasse pas son volume horaire | `AffectationService` |
| RG7 | Pas de doublon (enseignant, matière, type CM/TD/TP, année) | contrainte UNIQUE + service |
| RG8 | Un enseignant ayant des affectations ne peut pas être supprimé | `EnseignantService` + clé étrangère |
| RG9 | Une matière affectée ne peut pas être supprimée | `MatiereService` + clé étrangère |
| RG10 | Lecture pour tout utilisateur connecté, écriture réservée à ADMIN | `SecurityConfig` (JWT) |

Ces règles sont inventées faute de cahier des charges fourni : à remplacer par celles de votre enseignant si elles diffèrent
(les modifier se fait surtout dans `AffectationService` et `Grade`).
Une violation renvoie HTTP 409 `{"rule":"RG5","message":"..."}`, affiché tel quel dans l'interface.

## API

| Méthode | URL | Rôle |
|---------|-----|------|
| POST | `/api/auth/login` | public |
| GET | `/api/enseignants`, `/api/matieres`, `/api/affectations` (+ `/{id}`) | connecté |
| POST/PUT/DELETE | mêmes URL | ADMIN |
| GET | `/api/affectations/charge?enseignantId=1&annee=2025-2026` | connecté |

## Correspondance avec le planning SCRUM

- **Sprint 1** : expression des besoins, RG ci-dessus, `database/schema.sql`, `uml/usecases`, `uml/classes`.
- **Sprint 2** : architecture (`uml/deployment`), dépôt Git, authentification JWT (`uml/sequence_auth`).
- **Sprint 3** : CRUD enseignants / matières / affectations et intégration front-back (`uml/sequence_affectation`).
- **Sprint 4** : tests (`AffectationRulesTest`, matrice de tests = une ligne par RG), corrections, rapport.
- **Sprint 5** : démo (se connecter, créer une affectation valide, puis provoquer RG5 et RG6) et soutenance.
