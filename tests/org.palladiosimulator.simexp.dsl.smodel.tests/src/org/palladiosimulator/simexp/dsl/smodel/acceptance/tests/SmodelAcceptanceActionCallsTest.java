package org.palladiosimulator.simexp.dsl.smodel.acceptance.tests;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelAcceptanceActionCallsTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseOneActionNoParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName();
                if (true) {
                    aName();
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi);
                if (true) {
                    aName(pi=2);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneParamConst() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int ci = 1;
                action aName(param int pi);
                if (true) {
                    aName(pi=ci);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionMultipleParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi1, param int pi2);
                if (true) {
                    aName(pi1=1, pi2=2);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable int{1,2} vi);
                if (true) {
                    aName();
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionOneVariablePassed() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable int{1,2} vi);
                if (true) {
                    aName(vi=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2,
                "Couldn't resolve reference to Parameter 'vi'.", "Expected 0 arguments, got 1 instead.");
    }

    @Test
    public void parseOneActionMixedParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi, optimizable int{1,2} vi);
                if (true) {
                    aName(pi=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseOneActionMixedParams() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi, optimizable int{1,2} vi, optimizable int{1,2} vi2);
                if (true) {
                    aName(pi=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseMultipleActionsNoParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName();
                action bName();
                if (true) {
                    aName();
                    bName();
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseMultipleActionsOneParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pia);
                action bName(param int pib);
                if (true) {
                    aName(pia=2);
                    bName(pib=2);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseMultipleActionsMixedParams() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pia, optimizable int{1,2} via);
                action bName(param int pib, optimizable int{1,2} vib);
                if (true) {
                    aName(pia=2);
                    bName(pib=2);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
}
