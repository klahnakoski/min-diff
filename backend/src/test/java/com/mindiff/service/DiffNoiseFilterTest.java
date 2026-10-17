package com.mindiff.service;

import com.mindiff.model.RefactoringRange;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiffNoiseFilterTest {

    @Test
    void removesOnlySelectedRefactoringRanges() {
        DiffNoiseFilter filter = new DiffNoiseFilter();

        String diff = String.join("\n",
            "@@ -1,4 +1,4 @@",
            "-class A {}",
            "+class Alpha {}",
            "+newFeature();");

        String filtered = filter.removeSelectedRanges(diff, List.of(
            new RefactoringRange("Rename Class", 2, 3, true),
            new RefactoringRange("Extract Method", 4, 4, false)
        ));

        assertEquals(String.join("\n",
            "@@ -1,4 +1,4 @@",
            "+newFeature();"), filtered);
    }
}
