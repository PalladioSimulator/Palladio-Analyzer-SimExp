package org.palladiosimulator.simexp.dsl.kmodel.acceptance.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.tests.KmodelTestUtil;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelAcceptanceConditionsTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseOneLiteral() throws Exception {
        String sb = String.join("\n", 
                "if(true) {}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> conditions = model.getStatements();
        assertEquals(1, conditions.size());
        // TODO check actions
        //assertEquals(0, conditions.get);
    }

    @Test
    public void parseOneConst() throws Exception {
        String sb = String.join("\n",
                "const bool cb = true;",
                "if(cb) {}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> conditions = model.getStatements();
        assertEquals(1, conditions.size());
        // TODO check actions
        //assertEquals(0, conditions.get);
    }

    @Test
    public void parseOneConstExpressionOr() throws Exception {
        String sb = String.join("\n",
                "const bool cb = true;",
                "if(cb || false) {}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> conditions = model.getStatements();
        assertEquals(1, conditions.size());
        // TODO check actions
        //assertEquals(0, conditions.get);
    }

    @Test
    public void parseOneConstExpressionAnd() throws Exception {
        String sb = String.join("\n",
                "const bool cb = true;",
                "if(cb && false) {}"
                );
        
        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> conditions = model.getStatements();
        assertEquals(1, conditions.size());
        // TODO check actions
        //assertEquals(0, conditions.get);
    }

    @Test
    public void parseOneConstExpressionNotEqual() throws Exception {
        String sb = String.join("\n",
                "const int ci = 1;",
                "if(ci != 0) {}"
                );
        
        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> conditions = model.getStatements();
        assertEquals(1, conditions.size());
        // TODO check actions
        //assertEquals(0, conditions.get);
    }
    
    @Test
    public void parseOneAction() throws Exception {
        String sb = String.join("\n",
                "action aName();",
                "if(true) {",
                    "aName();",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> conditions = model.getStatements();
        assertEquals(1, conditions.size());
        Statement condition = conditions.get(0);
        // TODO check action
    }

    @Test
    public void parseMultipleActions() throws Exception {
        String sb = String.join("\n",
                "action aName1();",
                "action aName2();",
                "if(true) {",
                    "aName1();",
                    "aName2();",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> conditions = model.getStatements();
        assertEquals(1, conditions.size());
        Statement condition = conditions.get(0);
        // TODO check action
    }

    @Test
    public void parseInvalidAction() throws Exception {
        String sb = String.join("\n",
                "if(true) {",
                    "aName();",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
     
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Couldn't resolve reference to Action 'aName'.");
    }
}