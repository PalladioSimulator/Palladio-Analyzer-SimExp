package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.palladiosimulator.simexp.dsl.smodel.test.util.EcoreAssert.assertThat;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelActionParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseActionWithoutParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action setTextualMode();
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction = createAction("setTextualMode");
        assertThat(model.getActions()).containsExactly(expectedAction);
    }

    @Test
    public void parseActionWithBoolParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action decreaseQuality(param bool decrease);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction = createAction("decreaseQuality");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = createParameter("decrease", DataType.BOOL);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        assertThat(model.getActions()).containsExactly(expectedAction);
    }

    @Test
    public void parseActionWithIntParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action setNumCPUs(param int numCPUs);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction = createAction("setNumCPUs");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = createParameter("numCPUs", DataType.INT);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        assertThat(model.getActions()).containsExactly(expectedAction);
    }

    @Test
    public void parseActionWithDoubleParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param double balancingFactor);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction = createAction("scaleOut");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = createParameter("balancingFactor", DataType.DOUBLE);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        assertThat(model.getActions()).containsExactly(expectedAction);
    }

    @Test
    public void parseActionWithStringParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action setConfiguration(param string name);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction = createAction("setConfiguration");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = createParameter("name", DataType.STRING);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        assertThat(model.getActions()).containsExactly(expectedAction);
    }

    @Test
    public void parseActionWithTwoParameters() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scale(param double factor, param bool in);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction = createAction("scale");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter1 = createParameter("factor", DataType.DOUBLE);
        expectedActionArguments.getParameters()
            .add(expectedParameter1);
        Parameter expectedParameter2 = createParameter("in", DataType.BOOL);
        expectedActionArguments.getParameters()
            .add(expectedParameter2);
        assertThat(model.getActions()).containsExactly(expectedAction);
    }

    @Test
    public void parseActionWithOptimizeble() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(optimizable double{1.25, 2.5} balancingFactor);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction = createAction("scaleOut");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        SetBounds expectedBounds = createSetBoundsBool(createDoubleLiteral(1.25), createDoubleLiteral(2.5));
        Optimizable expectedOptimizable = createOptimizable("balancingFactor", DataType.DOUBLE, expectedBounds);
        expectedActionArguments.getOptimizables()
            .add(expectedOptimizable);
        assertThat(model.getActions()).containsExactly(expectedAction);
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

        validationTestHelper.assertNoIssues(model);
        Action expectedAction = createAction("aName");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = createParameter("pi", DataType.INT);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        SetBounds expectedBounds = createSetBoundsBool(createIntLiteral(1), createIntLiteral(2));
        Optimizable expectedOptimizable = createOptimizable("vi", DataType.INT, expectedBounds);
        expectedActionArguments.getOptimizables()
            .add(expectedOptimizable);
        assertThat(model.getActions()).containsExactly(expectedAction);
    }

    @Test
    public void parseTwoActions() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut();
                action scaleIn();
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction1 = createAction("scaleOut");
        Action expectedAction2 = createAction("scaleIn");
        assertThat(model.getActions()).containsExactly(expectedAction1, expectedAction2);
    }

    @Test
    public void parseTwoActionsWithSameParameterName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param double balancingFactor);
                action scaleIn(param double balancingFactor);
                """;
        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Action expectedAction1 = createAction("scaleOut");
        ActionArguments expectedActionArguments1 = expectedAction1.getArguments();
        Parameter expectedParameter1 = createParameter("balancingFactor", DataType.DOUBLE);
        expectedActionArguments1.getParameters()
            .add(expectedParameter1);
        Action expectedAction2 = createAction("scaleIn");
        ActionArguments expectedActionArguments2 = expectedAction2.getArguments();
        Parameter expectedParameter2 = createParameter("balancingFactor", DataType.DOUBLE);
        expectedActionArguments2.getParameters()
            .add(expectedParameter2);
        assertThat(model.getActions()).containsExactly(expectedAction1, expectedAction2);
    }

    @Test
    public void parseTwoActionsWithSameName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param int parameter);
                action adapt(param bool parameter2);
                """;

        Smodel model = parserHelper.parse(sb);

        // TODO
        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Action 'adapt'",
                "Duplicate Action 'adapt'");
    }

    @Test
    public void parseLocalActionDeclaration() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if(true){
                    action adapt(param int parameter);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'action' expecting '}'");
    }

    @Test
    public void parseActionWithSameParameterName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param int parameter, param double parameter);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.PARAMETER, null,
                "Duplicate Parameter 'parameter'", "Duplicate Parameter 'parameter'");
    }

    @Test
    public void parseActionWithSameParameterVariableName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param int parameter, optimizable int{1,2,3} parameter);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.ACTION_ARGUMENTS, null,
                "Duplicate Optimizable 'parameter'");
    }

    @Test
    public void parseActionWithSameOptimizableName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(optimizable int{1,2,3} parameter, optimizable int{1,2,3} parameter);
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.ACTION_ARGUMENTS, null,
                "Duplicate Optimizable 'parameter'");
    }

    @Test
    public void parseActionWithWrongParameterOrder() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(optimizable int{1} variable, param float parameter);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'param' expecting 'optimizable'");
    }

    @Test
    public void parseOneActionBoolOptimizableNoBounds() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable bool vb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input 'vb'");
    }

    private SetBounds createSetBoundsBool(Literal... values) {
        SetBounds bounds = SmodelFactory.eINSTANCE.createSetBounds();
        for (Literal value : values) {
            bounds.getValues()
                .add(value);
        }
        return bounds;
    }

    private Optimizable createOptimizable(String name, DataType type, Bounds bounds) {
        Optimizable optimizable = SmodelFactory.eINSTANCE.createOptimizable();
        optimizable.setName(name);
        optimizable.setDataType(type);
        optimizable.setValues(bounds);
        return optimizable;
    }

    private Parameter createParameter(String name, DataType type) {
        Parameter parameter = SmodelFactory.eINSTANCE.createParameter();
        parameter.setName(name);
        parameter.setDataType(type);
        return parameter;
    }

    private Action createAction(String name) {
        Action action = SmodelFactory.eINSTANCE.createAction();
        action.setName(name);
        ActionArguments actionArguments = SmodelFactory.eINSTANCE.createActionArguments();
        action.setArguments(actionArguments);
        return action;
    }

    private IntLiteral createIntLiteral(int value) {
        IntLiteral literal = SmodelFactory.eINSTANCE.createIntLiteral();
        literal.setValue(value);
        return literal;
    }

    private DoubleLiteral createDoubleLiteral(double value) {
        DoubleLiteral literal = SmodelFactory.eINSTANCE.createDoubleLiteral();
        literal.setValue(value);
        return literal;
    }
}
