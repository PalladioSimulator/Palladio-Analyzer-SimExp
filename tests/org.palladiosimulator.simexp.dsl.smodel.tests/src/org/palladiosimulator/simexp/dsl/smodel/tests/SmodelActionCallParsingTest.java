package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.palladiosimulator.simexp.dsl.smodel.test.util.EcoreAssert.assertThat;

import javax.inject.Inject;

import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelActionCallParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseActionCallNoParameters() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action a();
                if(true) {
                    a();
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Action expectedAction = smodelCreator.createAction("a");
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = createActionCall(expectedAction);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseActionCallWithLiteral() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action a(param double pd);
                if(true) {
                    a(pd=1.0);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Action expectedAction = smodelCreator.createAction("a");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = smodelCreator.createParameter("pd", DataType.DOUBLE);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = createActionCall(expectedAction);
        ParameterValue expectedParameterValue = createParameterValue(expectedParameter,
                smodelCreator.createDoubleLiteral(1.0));
        expectedActionCall.getArguments()
            .add(expectedParameterValue);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseActionCallWithField() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable double{0.0} argument;
                action scaleOut(param double balancingFactor);
                if(true){
                    scaleOut(balancingFactor=argument);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createDoubleLiteral(0.0));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("argument", DataType.DOUBLE, expectedBounds);
        Action expectedAction = smodelCreator.createAction("scaleOut");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = smodelCreator.createParameter("balancingFactor", DataType.DOUBLE);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = createActionCall(expectedAction);
        ParameterValue expectedParameterValue = createParameterValue(expectedParameter, null);
        Expression expectedParameterArgument = SmodelFactory.eINSTANCE.createExpression();
        expectedParameterArgument.setFieldRef(expectedOptimizable);
        expectedParameterValue.setArgument(expectedParameterArgument);
        expectedActionCall.getArguments()
            .add(expectedParameterValue);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseActionCallWithOnlyOptimizable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(optimizable double{1.0} balancingFactor);
                if (true) {
                    scaleOut();
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Action expectedAction = smodelCreator.createAction("scaleOut");
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createDoubleLiteral(1.0));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("balancingFactor", DataType.DOUBLE,
                expectedBounds);
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        expectedActionArguments.getOptimizables()
            .add(expectedOptimizable);
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = createActionCall(expectedAction);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseActionCallParameterAndOptimizable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param bool enable, optimizable double{1.0} balancingFactor);
                if (true) {
                    scaleOut(enable=true);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Action expectedAction = smodelCreator.createAction("scaleOut");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = smodelCreator.createParameter("enable", DataType.BOOL);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createDoubleLiteral(1.0));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("balancingFactor", DataType.DOUBLE,
                expectedBounds);
        expectedActionArguments.getOptimizables()
            .add(expectedOptimizable);
        IfStatement exptectedStatement = createIfStatement();
        ActionCall expectedActionCall = createActionCall(expectedAction);
        ParameterValue expectedParameterValue = createParameterValue(expectedParameter,
                smodelCreator.createBoolLiteral(true));
        expectedActionCall.getArguments()
            .add(expectedParameterValue);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseActionCallWithWrongParameterTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param double parameter);
                if(true) {
                    adapt(parameter=true);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.ACTION_CALL, null,
                "Expected a value of type 'int' or 'double', got 'bool' instead.");
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

        validationTestHelper.assertError(model, SmodelPackage.Literals.ACTION_CALL, null,
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

        validationTestHelper.assertError(model, SmodelPackage.Literals.ACTION_CALL, null,
                "Expected 1 arguments, got 0 instead.");
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

        validationTestHelper.assertError(model, SmodelPackage.Literals.PARAMETER_VALUE, Diagnostic.LINKING_DIAGNOSTIC,
                "Couldn't resolve reference to Parameter 'variable'.");
        validationTestHelper.assertError(model, SmodelPackage.Literals.ACTION_CALL, null,
                "Expected 1 arguments, got 2 instead.");
    }

    @Test
    public void parseActionCallWithParameterSelfReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action adapt(param double factor);
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

        validationTestHelper.assertError(model, SmodelPackage.Literals.SMODEL, Diagnostic.SYNTAX_DIAGNOSTIC,
                "missing EOF at 'adapt'");
    }

    private IfStatement createIfStatement() {
        IfStatement statement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression condition = smodelCreator.createLiteralBoolExpression(true);
        statement.setCondition(condition);
        return statement;
    }

    private ActionCall createActionCall(Action action) {
        ActionCall actionCall = SmodelFactory.eINSTANCE.createActionCall();
        actionCall.setActionRef(action);
        return actionCall;
    }

    private ParameterValue createParameterValue(Parameter parameter, Literal argument) {
        ParameterValue parameterValue = SmodelFactory.eINSTANCE.createParameterValue();
        parameterValue.setParamRef(parameter);
        if (argument != null) {
            Expression parameterArgument = SmodelFactory.eINSTANCE.createExpression();
            parameterArgument.setLiteral(argument);
            parameterValue.setArgument(parameterArgument);
        }
        return parameterValue;
    }
}
