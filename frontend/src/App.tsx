import { useMemo, useState } from 'react'
import './App.css'

type RefactoringRange = {
  label: string
  startLine: number
  endLine: number
  selected: boolean
}

type FilterResponse = {
  filteredDiff: string
  engine: string
}

const initialDiff = `@@ -1,7 +1,7 @@
-public class Parser {
+public class DiffParser {
-    public void parse() {}
+    public void parseDiff() {}
+    return filtered;
 }`

const initialRefactorings: RefactoringRange[] = [
  { label: 'Rename class Parser -> DiffParser', startLine: 2, endLine: 3, selected: true },
  { label: 'Rename method parse -> parseDiff', startLine: 4, endLine: 5, selected: true },
]

function removeSelectedRanges(diff: string, refactorings: RefactoringRange[]) {
  const blockedLines = new Set<number>()
  refactorings
    .filter((refactoring) => refactoring.selected)
    .forEach((refactoring) => {
      for (let line = refactoring.startLine; line <= refactoring.endLine; line += 1) {
        blockedLines.add(line)
      }
    })

  return diff
    .split(/\r?\n/)
    .filter((_, index) => !blockedLines.has(index + 1))
    .join('\n')
}

function App() {
  const [diff, setDiff] = useState(initialDiff)
  const [refactorings, setRefactorings] = useState(initialRefactorings)
  const [filteredDiff, setFilteredDiff] = useState(initialDiff)
  const [engineName, setEngineName] = useState('')

  const selectedCount = useMemo(
    () => refactorings.filter((refactoring) => refactoring.selected).length,
    [refactorings],
  )

  const toggleRefactoring = (index: number) => {
    setRefactorings((current) =>
      current.map((refactoring, currentIndex) =>
        currentIndex === index
          ? { ...refactoring, selected: !refactoring.selected }
          : refactoring,
      ),
    )
  }

  const applyFilter = async () => {
    try {
      const response = await fetch('/api/filter', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ diff, refactorings }),
      })

      if (response.ok) {
        const payload = (await response.json()) as FilterResponse
        setFilteredDiff(payload.filteredDiff)
        setEngineName(payload.engine)
        return
      }
    } catch {
      // fall through to local filtering
    }

    setFilteredDiff(removeSelectedRanges(diff, refactorings))
    setEngineName('local fallback')
  }

  return (
    <main>
      <h1>min-diff</h1>
      <p className="subtitle">
        Interactively remove refactoring noise from GitHub diffs.
      </p>

      <section className="panel">
        <h2>1) Paste or edit diff</h2>
        <textarea
          value={diff}
          onChange={(event) => setDiff(event.target.value)}
          rows={9}
          aria-label="Git diff input"
        />
      </section>

      <section className="panel">
        <h2>2) Toggle detected refactorings ({selectedCount} selected)</h2>
        <ul>
          {refactorings.map((refactoring, index) => (
            <li key={refactoring.label}>
              <label>
                <input
                  type="checkbox"
                  checked={refactoring.selected}
                  onChange={() => toggleRefactoring(index)}
                />
                <span>{refactoring.label}</span>
                <small>
                  lines {refactoring.startLine}-{refactoring.endLine}
                </small>
              </label>
            </li>
          ))}
        </ul>
        <button type="button" onClick={applyFilter}>
          Remove selected refactoring noise
        </button>
      </section>

      <section className="panel">
        <h2>3) Cleaned diff</h2>
        <p className="engine">Engine: {engineName || 'pending'}</p>
        <pre>{filteredDiff}</pre>
      </section>
    </main>
  )
}

export default App
