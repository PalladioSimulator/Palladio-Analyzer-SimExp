package org.palladiosimulator.simexp.dsl.kmodel.tests;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
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
    		Assert.assertEquals(messages[i], errors.get(i).getMessage());
    	}
    }
}
