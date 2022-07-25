package org.palladiosimulator.simexp.dsl.kmodel.acceptance.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Parameter;
import org.palladiosimulator.simexp.dsl.kmodel.tests.KmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.kmodel.tests.KmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelAcceptanceActionCallsTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseOneActionNoParam() throws Exception {
        String sb = String.join("\n", 
                "action aName();",
                "if (true) {",
                    "aName();",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneParam() throws Exception {
        String sb = String.join("\n", 
                "action aName(param int pi);",
                "if (true) {",
                    "aName(pi=2);",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneParamConst() throws Exception {
        String sb = String.join("\n", 
                "const int ci = 1;",
                "action aName(param int pi);",
                "if (true) {",
                    "aName(pi=ci);",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
    
    @Test
    public void parseOneActionMultipleParam() throws Exception {
        String sb = String.join("\n", 
                "action aName(param int pi1, param int pi2);",
                "if (true) {",
                    "aName(pi1=1, pi2=2);",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneVariable() throws Exception {
        String sb = String.join("\n", 
                "action aName(var int{1,2} vi);",
                "if (true) {",
                    "aName();",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneVariablePassed() throws Exception {
        String sb = String.join("\n", 
                "action aName(var int{1,2} vi);",
                "if (true) {",
                    "aName(vi=1);",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';'");
    }

    @Test
    public void parseOneActionMixedParam() throws Exception {
        String sb = String.join("\n", 
                "action aName(param int pi, var int{1,2} vi);",
                "if (true) {",
                    "aName(pi=1);",
                "}"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
}
