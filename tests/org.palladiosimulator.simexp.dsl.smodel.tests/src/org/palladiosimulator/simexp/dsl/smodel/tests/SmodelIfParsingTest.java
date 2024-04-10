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
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelIfParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseIfStatementWithLiteralCondition() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseIfStatementWithFieldCondition() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool condition = true;
                if (condition) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Constant expectedConstant = smodelCreator.createConstant("condition", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedLiteralCondition = SmodelFactory.eINSTANCE.createExpression();
        exptectedLiteralCondition.setFieldRef(expectedConstant);
        exptectedStatement.setCondition(exptectedLiteralCondition);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseConsecutiveIfStatements() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {}
                if (true) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        IfStatement exptectedStatement1 = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition1 = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement1.setCondition(exptectedCondition1);
        IfStatement exptectedStatement2 = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition2 = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement2.setCondition(exptectedCondition2);
        assertThat(model.getStatements()).containsExactly(exptectedStatement1, exptectedStatement2);
    }

    @Test
    public void parseNestedIfStatements() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {
                    if (true) {}
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        IfStatement exptectedStatement1 = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition1 = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement1.setCondition(exptectedCondition1);
        IfStatement exptectedStatement2 = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition2 = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement2.setCondition(exptectedCondition2);
        exptectedStatement1.getThenStatements()
            .add(exptectedStatement2);
        assertThat(model.getStatements()).containsExactly(exptectedStatement1);
    }

    @Test
    public void parseIfElseStatement() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {
                } else {
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseNestedIfElseStatement() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {
                } else {
                    if (true) {}
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        IfStatement exptectedStatement1 = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition1 = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement1.setCondition(exptectedCondition1);
        IfStatement exptectedStatement2 = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition2 = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement2.setCondition(exptectedCondition2);
        exptectedStatement1.getElseStatements()
            .add(exptectedStatement2);
        assertThat(model.getStatements()).containsExactly(exptectedStatement1);
    }

    @Test
    public void parseIfStatementWithActionCall() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param bool p);
                if (true) {
                    scaleOut(p=true);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Action expectedAction = smodelCreator.createAction("scaleOut");
        ActionArguments expectedActionArguments = expectedAction.getArguments();
        Parameter expectedParameter = smodelCreator.createParameter("p", DataType.BOOL);
        expectedActionArguments.getParameters()
            .add(expectedParameter);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        ActionCall expectedActionCall = SmodelFactory.eINSTANCE.createActionCall();
        expectedActionCall.setActionRef(expectedAction);
        ParameterValue expectedParameterValue = SmodelFactory.eINSTANCE.createParameterValue();
        expectedParameterValue.setParamRef(expectedParameter);
        Expression exptectedParameterValue = smodelCreator.createLiteralBoolExpression(true);
        expectedParameterValue.setArgument(exptectedParameterValue);
        expectedActionCall.getArguments()
            .add(expectedParameterValue);
        exptectedStatement.getThenStatements()
            .add(expectedActionCall);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseIfStatementWithWrongLiteralType() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if ("condition") {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.IF_STATEMENT, null,
                "Expected a value of type 'bool', got 'string' instead.");
    }

    @Test
    public void parseIfStatementWithWrongFieldType() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const string condition = "true";
                if (condition) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.IF_STATEMENT, null,
                "Expected a value of type 'bool', got 'string' instead.");
    }
}
