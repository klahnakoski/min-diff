# min-diff

View GitHub PR diffs without mechanical refactoring noise.

## Initial direction

- **Backend**: Java service with a RefactoringMiner-backed filtering engine.
- **Frontend**: TypeScript/React SPA for interactively toggling/removing refactoring hunks.

## Backend (Java)

```bash
cd backend
mvn test
mvn package
mvn exec:java -Dexec.mainClass=com.mindiff.Main
```

Server endpoints:
- `GET /api/health`
- `POST /api/filter`

Example request payload for `/api/filter`:

```json
{
  "diff": "@@ -1,2 +1,2 @@\n-old\n+new",
  "refactorings": [
    { "label": "Rename", "startLine": 2, "endLine": 2, "selected": true }
  ]
}
```

## Frontend (TypeScript + React)

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server proxies `/api/*` requests to `http://localhost:8080`.

To produce a production build:

```bash
cd frontend
npm run build
```
