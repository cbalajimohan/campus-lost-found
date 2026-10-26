# Campus Lost & Found Management System

A responsive full-stack web app where students report, search, track and recover lost belongings on campus.

**Stack:** React + Vite · Flask · SQLite

## Features
- Student registration and login; admin role with separate dashboard
- Lost and found reports with category, description, date, location and optional photo
- Search and filter by keyword, type, category and status
- **Automatic matching**: each new report is scored against open reports of the opposite type (category, name, description and location overlap, date proximity). Scores of 45 and above are saved as matches
- Claim workflow: a student claims a found item, an admin approves or rejects, and approval marks the item (and the linked lost report) as returned
- Admin dashboard: stats, claim verification, status changes, deletion

## Security
- Passwords hashed with Werkzeug (PBKDF2); password strength and input validation on every endpoint
- Server-side sessions in a signed, HttpOnly, SameSite=Lax cookie with an 8-hour lifetime
- Role-based access control (`login_required`, `admin_required`); protected routes on the frontend
- Parameterized SQL only; React escapes output; uploads checked by file signature, size-capped (3 MB) and stored under random names
- Basic login throttling per email

## Setup

### 1. Backend
```bash
cd backend
python -m venv venv && source venv/bin/activate     # Windows: venv\Scripts\activate
pip install -r requirements.txt
python seed.py          # creates lostfound.db with demo users and reports
python app.py           # http://127.0.0.1:5000
```
Demo logins: `admin@campus.edu` / `Admin@123` and `aarav@campus.edu` / `Student@123`.

For any real deployment set `SECRET_KEY` (and `COOKIE_SECURE=1` behind HTTPS) in the environment.

### 2. Frontend
```bash
cd frontend
npm install
npm run dev             # http://localhost:5173 (proxies /api and /uploads to Flask)
```
Production build: `npm run build`, then serve `dist/` behind a reverse proxy that forwards `/api` and `/uploads` to Flask.

### 3. Tests
```bash
cd backend && python -m pytest
```

## API summary
| Method | Path | Access |
|---|---|---|
| POST | `/api/auth/register`, `/login`, `/logout` · GET `/api/auth/me` | public |
| GET | `/api/items` (`q,type,category,status`), `/api/items/<id>` | public |
| POST | `/api/items` (multipart) | student |
| GET | `/api/items/<id>/matches` | reporter or admin |
| POST | `/api/items/<id>/claims` | student (not the reporter) |
| GET | `/api/my/items`, `/api/my/claims` | student |
| GET | `/api/admin/stats`, `/api/admin/claims` · PATCH `/api/admin/claims/<id>` · PATCH/DELETE `/api/admin/items/<id>` | admin |

## Project layout
```
backend/   app.py (API) · db.py (schema) · matching.py · seed.py · tests/
frontend/  src/App.jsx · src/api.js · src/pages/* · src/styles.css
```

## Ideas for next steps
Email notifications on new matches, image similarity matching, pagination, CSRF tokens if hosted cross-origin, Docker setup.

## Demo on your phone (same Wi-Fi)
1. Start Flask (`python app.py`) and Vite (`npm run dev`) on the laptop. Vite prints a `Network:` URL such as `http://192.168.x.x:5173`.
2. Open that URL on the Android phone. Flask stays private on the laptop; Vite forwards `/api` and `/uploads` to it.
3. If it does not load, allow Node.js through Windows Firewall (Private networks).
