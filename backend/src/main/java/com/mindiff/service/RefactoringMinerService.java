package com.mindiff.service;

import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

public class RefactoringMinerService {
    public String engineName() {
        return GitHistoryRefactoringMinerImpl.class.getSimpleName();
    }
}
