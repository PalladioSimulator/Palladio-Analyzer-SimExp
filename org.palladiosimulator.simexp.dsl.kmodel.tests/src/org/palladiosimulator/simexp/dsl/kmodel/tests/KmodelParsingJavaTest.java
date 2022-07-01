package org.palladiosimulator.simexp.dsl.kmodel.tests;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Element;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelParsingJavaTest {
    @Inject private ParseHelper<KModel> parserHelper;

    @Test
    public void parseSingleIntVariable() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("var int count;\n");
        
        KModel model = parserHelper.parse(sb);
        
        assertModelWithoutErrors(model);
        
        EList<Element> elements = model.getElements();
        Assert.assertEquals(1, elements.size());
        Element element = elements.get(0);
        Statement statement = (Statement) element;
        Variable variable = statement.getVar();
        Assert.assertEquals("count", variable.getName());
        Assert.assertEquals(DataType.INT, variable.getDataType());
    }
    
    private void assertModelWithoutErrors(KModel model) {
        Assert.assertNotNull(model);
        EList<Diagnostic> errors = model.eResource().getErrors();
        StringBuilder joinedErrors = new StringBuilder(); 
        for (Diagnostic diagnostic : errors) {
            joinedErrors.append(String.join(",", diagnostic.getMessage()));
        }
        Assert.assertTrue(String.format("Unexpected errors: %s", joinedErrors), errors.isEmpty());
    }

}
