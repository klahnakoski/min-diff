package com.mindiff.model;

import java.util.List;

public record FilterRequest(String diff, List<RefactoringRange> refactorings) {
}
