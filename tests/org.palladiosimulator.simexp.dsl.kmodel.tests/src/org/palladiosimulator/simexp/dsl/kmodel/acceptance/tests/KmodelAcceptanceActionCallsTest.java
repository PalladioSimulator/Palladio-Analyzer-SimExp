package org.palladiosimulator.simexp.dsl.kmodel.acceptance.tests;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelAcceptanceActionCallsTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseOneActionNoParam() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName();
                if (true) {
                    aName();
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneParam() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi);
                if (true) {
                    aName(pi=2);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneParamConst() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const int ci = 1;
                action aName(param int pi);
                if (true) {
                    aName(pi=ci);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionMultipleParam() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi1, param int pi2);
                if (true) {
                    aName(pi1=1, pi2=2);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneVariable() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(var int{1,2} vi);
                if (true) {
                    aName();
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneVariablePassed() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(var int{1,2} vi);
                if (true) {
                    aName(vi=1);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2,
                "Couldn't resolve reference to Parameter 'vi'.", "Expected 0 arguments, got 1 instead.");
    }

    @Test
    public void parseOneActionMixedParam() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi, var int{1,2} vi);
                if (true) {
                    aName(pi=1);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionMixedParams() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi, var int{1,2} vi, var int{1,2} vi2);
                if (true) {
                    aName(pi=1);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseMultipleActionsNoParam() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName();
                action bName();
                if (true) {
                    aName();
                    bName();
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseMultipleActionsOneParam() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pia);
                action bName(param int pib);
                if (true) {
                    aName(pia=2);
                    bName(pib=2);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseMultipleActionsMixedParams() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pia, var int{1,2} via);
                action bName(param int pib, var int{1,2} vib);
                if (true) {
                    aName(pia=2);
                    bName(pib=2);
                }
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
}
