package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.GlobalStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;
import org.palladiosimulator.simexp.dsl.smodel.util.ExpressionUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelActionParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    private ExpressionUtil expressionUtil = new ExpressionUtil();

    @Test
    public void parseActionWithoutParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action setTextualMode();
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("setTextualMode", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(0, parameters.size());
    }

    @Test
    public void parseActionWithBoolParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action decreaseQuality(param bool decrease);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("decreaseQuality", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(1, parameters.size());
        Parameter parameter = parameters.get(0);
        Assert.assertEquals(DataType.BOOL, parameter.getDataType());
        Assert.assertEquals("decrease", parameter.getName());
    }

    @Test
    public void parseActionWithIntParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action setNumCPUs(param int numCPUs);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("setNumCPUs", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(1, parameters.size());
        Parameter parameter = parameters.get(0);
        Assert.assertEquals(DataType.INT, parameter.getDataType());
        Assert.assertEquals("numCPUs", parameter.getName());
    }

    @Test
    public void parseActionWithDoubleParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param double balancingFactor);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(1, parameters.size());
        Parameter parameter = parameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, parameter.getDataType());
        Assert.assertEquals("balancingFactor", parameter.getName());
    }

    @Test
    public void parseActionWithStringParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action setConfiguration(param string name);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("setConfiguration", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(1, parameters.size());
        Parameter parameter = parameters.get(0);
        Assert.assertEquals(DataType.STRING, parameter.getDataType());
        Assert.assertEquals("name", parameter.getName());
    }

    @Test
    public void parseActionWithTwoParameters() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scale(param double factor, param bool in);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("scale", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(2, parameters.size());
        Parameter firstParameter = parameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, firstParameter.getDataType());
        Assert.assertEquals("factor", firstParameter.getName());
        Parameter secondParameter = parameters.get(1);
        Assert.assertEquals(DataType.BOOL, secondParameter.getDataType());
        Assert.assertEquals("in", secondParameter.getName());
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
    public void parseActionWithVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(optimizable double{1.25, 2.5} balancingFactor);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(0, parameters.size());
        List<Optimizable> optimizables = action.getArguments()
            .getOptimizables();
        Assert.assertEquals(1, optimizables.size());
        Field variable = optimizables.get(0);
        Assert.assertEquals(DataType.DOUBLE, variable.getDataType());
        Assert.assertEquals("balancingFactor", variable.getName());
        Bounds bounds = ((Optimizable) variable).getValues();
        Assert.assertTrue(bounds instanceof SetBounds);
        List<Literal> values = ((SetBounds) bounds).getValues();
        Assert.assertEquals(2, values.size());
        double firstValue = ((DoubleLiteral) values.get(0)).getValue();
        Assert.assertEquals(1.25, firstValue, 0.0f);
        double secondValue = ((DoubleLiteral) values.get(1)).getValue();
        Assert.assertEquals(2.5, secondValue, 0.0f);
    }

    @Test
    public void parseActionWithParameterAndVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param int factor1, optimizable double{1.25, 2.5} factor2);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(1, parameters.size());
        Parameter parameter = parameters.get(0);
        Assert.assertEquals(DataType.INT, parameter.getDataType());
        Assert.assertEquals("factor1", parameter.getName());
        List<Optimizable> optimizables = action.getArguments()
            .getOptimizables();
        Assert.assertEquals(1, optimizables.size());
        Field variable = optimizables.get(0);
        Assert.assertEquals(DataType.DOUBLE, variable.getDataType());
        Assert.assertEquals("factor2", variable.getName());
        Bounds bounds = ((Optimizable) variable).getValues();
        Assert.assertTrue(bounds instanceof SetBounds);
        List<Literal> values = ((SetBounds) bounds).getValues();
        Assert.assertEquals(2, values.size());
        double firstValue = ((DoubleLiteral) values.get(0)).getValue();
        Assert.assertEquals(1.25, firstValue, 0.0f);
        double secondValue = ((DoubleLiteral) values.get(1)).getValue();
        Assert.assertEquals(2.5, secondValue, 0.0f);
    }

    @Test
    public void parseTwoActions() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param double scaleOutFactor);
                action scaleIn(param double scaleInFactor);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(2, actions.size());
        Action firstAction = actions.get(0);
        Assert.assertEquals("scaleOut", firstAction.getName());
        List<Parameter> firstParameters = firstAction.getArguments()
            .getParameters();
        Assert.assertEquals(1, firstParameters.size());
        Parameter firstParameter = firstParameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, firstParameter.getDataType());
        Assert.assertEquals("scaleOutFactor", firstParameter.getName());
        Action secondAction = actions.get(1);
        Assert.assertEquals("scaleIn", secondAction.getName());
        List<Parameter> secondParameters = secondAction.getArguments()
            .getParameters();
        Assert.assertEquals(1, secondParameters.size());
        Parameter secondParameter = secondParameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, secondParameter.getDataType());
        Assert.assertEquals("scaleInFactor", secondParameter.getName());
    }

    @Test
    public void parseTwoActionsWithSameParameterName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param double balancingFactor);
                action scaleIn(param double balancingFactor);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(2, actions.size());
        Action firstAction = actions.get(0);
        Assert.assertEquals("scaleOut", firstAction.getName());
        List<Parameter> firstParameters = firstAction.getArguments()
            .getParameters();
        Assert.assertEquals(1, firstParameters.size());
        Parameter firstParameter = firstParameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, firstParameter.getDataType());
        Assert.assertEquals("balancingFactor", firstParameter.getName());
        Action secondAction = actions.get(1);
        Assert.assertEquals("scaleIn", secondAction.getName());
        List<Parameter> secondParameters = secondAction.getArguments()
            .getParameters();
        Assert.assertEquals(1, secondParameters.size());
        Parameter secondParameter = secondParameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, secondParameter.getDataType());
        Assert.assertEquals("balancingFactor", secondParameter.getName());
    }

    @Test
    public void parseActionCallWithLiteral() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param double balancingFactor);
                if(true) {
                    scaleOut(balancingFactor=1.0);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(1, parameters.size());
        Parameter parameter = parameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, parameter.getDataType());
        Assert.assertEquals("balancingFactor", parameter.getName());
        EList<GlobalStatement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        ActionCall actionCall = (ActionCall) ((IfStatement) statements.get(0)).getThenStatements()
            .get(0);
        Assert.assertEquals(actionCall.getActionRef(), action);
        List<ParameterValue> arguments = actionCall.getArguments();
        Assert.assertEquals(1, arguments.size());
        Expression argument = expressionUtil.getNextExpressionWithContent(arguments.get(0)
            .getArgument());
        Assert.assertTrue(argument.getLiteral() instanceof DoubleLiteral);
        Assert.assertEquals(1, ((DoubleLiteral) argument.getLiteral()).getValue(), 0.0f);
    }

    @Test
    public void parseActionCallWithField() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable double{0} argument;
                action scaleOut(param double balancingFactor);
                if(true){
                    scaleOut(balancingFactor=argument);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(1, parameters.size());
        Parameter parameter = parameters.get(0);
        Assert.assertEquals(DataType.DOUBLE, parameter.getDataType());
        Assert.assertEquals("balancingFactor", parameter.getName());
        EList<GlobalStatement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        ActionCall actionCall = (ActionCall) ((IfStatement) statements.get(0)).getThenStatements()
            .get(0);
        Assert.assertEquals(actionCall.getActionRef(), action);
        List<ParameterValue> arguments = actionCall.getArguments();
        Assert.assertEquals(1, arguments.size());
        Expression argument = expressionUtil.getNextExpressionWithContent(arguments.get(0)
            .getArgument());
        Field argumentField = argument.getFieldRef();
        Assert.assertEquals("argument", argumentField.getName());
        Assert.assertEquals(DataType.DOUBLE, argumentField.getDataType());
    }

    @Test
    public void parseActionCallWithVariableParameter() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(optimizable double{1.25, 2.5} balancingFactor);
                if (true) {
                    scaleOut();
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        List<Parameter> parameters = action.getArguments()
            .getParameters();
        Assert.assertEquals(0, parameters.size());
        List<Optimizable> optimizables = action.getArguments()
            .getOptimizables();
        Assert.assertEquals(1, optimizables.size());
        Field variable = optimizables.get(0);
        Assert.assertEquals(DataType.DOUBLE, variable.getDataType());
        Assert.assertEquals("balancingFactor", variable.getName());
        List<GlobalStatement> statements = model.getStatements();
        ActionCall actionCall = (ActionCall) ((IfStatement) statements.get(0)).getThenStatements()
            .get(0);
        Action actionRef = actionCall.getActionRef();
        Assert.assertEquals(action, actionRef);
        List<ParameterValue> arguments = actionCall.getArguments();
        Assert.assertTrue(arguments.isEmpty());
    }

    @Test
    public void parseComplexActionCall() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE
                + """
                        const int constant = 1;
                        optimizable double[1, 2, 1] variable;
                        action adapt(param double param1, param int param2, param bool param3, optimizable int{1, 2, 3, 4} variable);
                        if(true) {
                            adapt(param1=variable, param2=(constant + 1), param3=(true && false));
                        }
                        """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }

    @Test
    public void parseTwoActionsWithSameName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param int parameter);
                action adapt(param bool parameter2);
                """;

        Smodel model = parserHelper.parse(sb);

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
    public void parseActionWithSameVariableName() throws Exception {
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
    public void parseActionCallWithWrongParameterTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param double parameter1, param bool parameter2);
                if(true) {
                    adapt(parameter1=true, parameter2=2);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2,
                "Expected a value of type 'int' or 'double', got 'bool' instead.",
                "Expected a value of type 'bool', got 'int' instead.");
    }

    @Test
    public void parseActionCallWithWrongArgumentOrder() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param int a, param int b);
                if (true) {
                    adapt(b=1, a=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Arguments must be provided in the order as declared.");
    }

    @Test
    public void parseActionCallWithTooFewArguments() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param double parameter);
                if (true) {
                    adapt();
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, "Expected 1 arguments, got 0 instead.");
    }

    @Test
    public void parseActionCallWithTooManyArguments() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param double parameter, optimizable double{0} variable);
                if (true) {
                    adapt(parameter=1.0, variable=1.0);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2,
                "Couldn't resolve reference to Parameter 'variable'.", "Expected 1 arguments, got 2 instead.");
    }

    @Test
    public void parseActionCallWithParameterSelfReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param float factor);
                if(true) {
                    adapt(factor=factor);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, Diagnostic.LINKING_DIAGNOSTIC,
                "Couldn't resolve reference to Field 'factor'.");
    }

    @Test
    public void parseActionCallOutsideIf() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param int parameter);
                adapt(parameter=1);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "missing EOF at 'adapt'");
    }

    @Test
    public void parseOneActionBoolVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable bool{true,false} vb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getArguments()
            .getParameters();
        assertEquals(0, parameters.size());
        EList<Optimizable> optimizables = action.getArguments()
            .getOptimizables();
        assertEquals(1, optimizables.size());
        Optimizable variable = optimizables.get(0);
        assertEquals("vb", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof SetBounds);
        SetBounds rangeArray = (SetBounds) bounds;
        BoolLiteral boolRange1 = (BoolLiteral) rangeArray.getValues()
            .get(0);
        BoolLiteral boolRange2 = (BoolLiteral) rangeArray.getValues()
            .get(1);
        List<Boolean> actualBoolBounds = Arrays.asList(boolRange1.isTrue(), boolRange2.isTrue());
        MatcherAssert.assertThat(actualBoolBounds, CoreMatchers.hasItems(true, false));
    }

    @Test
    public void parseOneActionBoolVariableNoBounds() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable bool vb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input 'vb'");
    }

}
