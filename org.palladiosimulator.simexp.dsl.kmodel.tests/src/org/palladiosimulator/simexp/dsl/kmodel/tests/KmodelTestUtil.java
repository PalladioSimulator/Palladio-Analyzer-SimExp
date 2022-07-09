package org.palladiosimulator.simexp.dsl.kmodel.tests;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.validation.Issue;
import org.junit.Assert;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;

public class KmodelTestUtil {
	
	public static void assertModelWithoutErrors(KModel model) {
        Assert.assertNotNull(model);
        EList<Diagnostic> errors = model.eResource().getErrors();
        StringBuilder joinedErrors = new StringBuilder(); 
        for (Diagnostic diagnostic : errors) {
            joinedErrors.append(String.join(",", diagnostic.getMessage()));
        }
        Assert.assertTrue(String.format("Unexpected errors: %s", joinedErrors), errors.isEmpty());
    }
	
	public static void assertErrorMessages(KModel model, int numErrors, String... messages) {
    	Assert.assertNotNull(model);
    	EList<Diagnostic> errors = model.eResource().getErrors();
    	
    	Assert.assertEquals(numErrors, errors.size());
    	
    	for (int i = 0; i < numErrors; i++) {
    		String errorMessage = "Expected error: \"" + messages[i] + "\", got \"" + errors.get(i).getMessage() + "\" instead.";
    		Assert.assertEquals(errorMessage, messages[i], errors.get(i).getMessage());
    	}
    }
	
	public static void assertNoValidationIssues(ValidationTestHelper helper, KModel model) {
		Assert.assertNotNull(model);
		List<Issue> issues = helper.validate(model);
		StringBuilder joinedIssues = new StringBuilder(); 
        for (Issue issue : issues) {
            joinedIssues.append(String.join(",", issue.getMessage()));
        }
        Assert.assertTrue(String.format("Unexpected issues: %s", joinedIssues), issues.isEmpty());
	}
	
	public static void assertValidationIssues(ValidationTestHelper helper, KModel model, int numIssues, String... messages) {
    	Assert.assertNotNull(model);
    	List<Issue> issues = helper.validate(model);
    	
    	Assert.assertEquals(numIssues, issues.size());
    	
    	for (int i = 0; i < numIssues; i++) {
    		String errorMessage = "Expected issue: \"" + messages[i] + "\", got \"" + issues.get(i).getMessage() + "\" instead.";
    		Assert.assertEquals(errorMessage, messages[i], issues.get(i).getMessage());
    	}
    }
	
	
}
