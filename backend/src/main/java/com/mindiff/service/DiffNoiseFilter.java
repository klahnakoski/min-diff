package com.mindiff.service;

import com.mindiff.model.RefactoringRange;

import java.util.List;

public class DiffNoiseFilter {
    public String removeSelectedRanges(String diff, List<RefactoringRange> refactorings) {
        if (diff == null || diff.isBlank() || refactorings == null || refactorings.isEmpty()) {
            return diff == null ? "" : diff;
        }

        String[] lines = diff.split("\\r?\\n", -1);
        StringBuilder filtered = new StringBuilder();

        for (int lineNumber = 1; lineNumber <= lines.length; lineNumber++) {
            if (shouldKeep(lineNumber, refactorings)) {
                if (!filtered.isEmpty()) {
                    filtered.append('\n');
                }
                filtered.append(lines[lineNumber - 1]);
            }
        }

        return filtered.toString();
    }

    private boolean shouldKeep(int lineNumber, List<RefactoringRange> refactorings) {
        for (RefactoringRange range : refactorings) {
            if (range.selected() && lineNumber >= range.startLine() && lineNumber <= range.endLine()) {
                return false;
            }
        }
        return true;
    }
}
