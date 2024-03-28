package org.palladiosimulator.simexp.dsl.smodel.tests.util;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.validation.Issue;
import org.junit.Assert;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SmodelTestUtil {
    public static final String MODEL_NAME_LINE = "modelName = \"name\";";

    public static void assertModelWithoutErrors(Smodel model) {
        Assert.assertNotNull(model);
        EList<Diagnostic> errors = model.eResource()
            .getErrors();
        StringBuilder joinedErrors = new StringBuilder();
        for (Diagnostic diagnostic : errors) {
            joinedErrors.append(String.join(",", diagnostic.getMessage()));
        }
        Assert.assertTrue(String.format("Unexpected errors: %s", joinedErrors), errors.isEmpty());
    }

    public static void assertErrorMessages(Smodel model, int numErrors, String... messages) {
        Assert.assertNotNull(model);
        EList<Diagnostic> errors = model.eResource()
            .getErrors();

        Assert.assertEquals(numErrors, errors.size());

        for (int i = 0; i < numErrors; i++) {
            String errorMessage = "Expected error: \"" + messages[i] + "\", got \"" + errors.get(i)
                .getMessage() + "\" instead.";
            Assert.assertEquals(errorMessage, messages[i], errors.get(i)
                .getMessage());
        }
    }

    public static void assertNoValidationIssues(ValidationTestHelper helper, Smodel model) {
        Assert.assertNotNull(model);
        List<Issue> issues = helper.validate(model);
        List<Issue> errors = issues.stream()
            .filter(issue -> issue.getSeverity() == Severity.ERROR)
            .collect(Collectors.toList());

        StringBuilder joinedIssues = new StringBuilder();
        for (Issue issue : errors) {
            joinedIssues.append(String.join(",", issue.getMessage()));
        }
        Assert.assertTrue(String.format("Unexpected issues: %s", joinedIssues), errors.isEmpty());
    }

    public static void assertValidationIssues(ValidationTestHelper helper, Smodel model, int numIssues,
            String... messages) {
        Assert.assertNotNull(model);
        List<Issue> issues = helper.validate(model);
        List<Issue> errors = issues.stream()
            .filter(issue -> issue.getSeverity() == Severity.ERROR)
            .collect(Collectors.toList());

        Assert.assertEquals(numIssues, errors.size());

        for (int i = 0; i < numIssues; i++) {
            Issue issue = errors.get(i);
            String errorMessage = "Expected issue: \"" + messages[i] + "\", got \"" + issue.getMessage()
                    + "\" instead.";
            Assert.assertEquals(errorMessage, messages[i], issue.getMessage());
        }
    }
}
