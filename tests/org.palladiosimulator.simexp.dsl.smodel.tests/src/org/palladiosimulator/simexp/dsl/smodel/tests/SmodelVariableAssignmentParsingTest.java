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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Array;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.VariableAssignment;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelVariableAssignmentParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBool() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var bool condition = true;
                if (true) {
                    condition = false;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Variable expectedVariable = createBoolVariable("condition", true);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = createLiteralBoolValue(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = createLiteralBoolValue(false);
        expectedAssignment.setValue(expectedExpression);
        exptectedStatement.getThenStatements()
            .add(expectedAssignment);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseWithConstantValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1;
                var int variable = 0;
                if (true) {
                    variable = constant;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Constant exptectedConstant = SmodelFactory.eINSTANCE.createConstant();
        exptectedConstant.setName("constant");
        exptectedConstant.setDataType(DataType.INT);
        Expression expectedExpression1 = SmodelFactory.eINSTANCE.createExpression();
        IntLiteral expectedLiteral1 = SmodelFactory.eINSTANCE.createIntLiteral();
        expectedLiteral1.setValue(1);
        Expression expectedLiteralExpression1 = SmodelFactory.eINSTANCE.createExpression();
        expectedLiteralExpression1.setLiteral(expectedLiteral1);
        expectedExpression1.setLeft(expectedLiteralExpression1);
        exptectedConstant.setValue(expectedExpression1);
        Variable expectedVariable = createIntVariable("variable", 0);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = createLiteralBoolValue(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        Expression expectedLiteralExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedLiteralExpression.setFieldRef(exptectedConstant);
        expectedExpression.setLeft(expectedLiteralExpression);
        expectedAssignment.setValue(expectedExpression);
        exptectedStatement.getThenStatements()
            .add(expectedAssignment);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseWithOptimizableValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{0} opti;
                var int variable = 0;
                if (true) {
                    variable = opti;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Optimizable exptectedOptimizable = SmodelFactory.eINSTANCE.createOptimizable();
        exptectedOptimizable.setName("opti");
        exptectedOptimizable.setDataType(DataType.INT);
        Array expectedBounds = SmodelFactory.eINSTANCE.createArray();
        expectedBounds.getValues()
            .add(createIntLiteral(0));
        exptectedOptimizable.setValues(expectedBounds);
        Variable expectedVariable = createIntVariable("variable", 0);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = createLiteralBoolValue(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        Expression expectedLiteralExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedLiteralExpression.setFieldRef(exptectedOptimizable);
        expectedExpression.setLeft(expectedLiteralExpression);
        expectedAssignment.setValue(expectedExpression);
        exptectedStatement.getThenStatements()
            .add(expectedAssignment);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseWithProbeValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int someProbe : id = "someId";
                var int variable = 0;
                if (true) {
                    variable = someProbe;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Probe exptectedProbe = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("someProbe");
        exptectedProbe.setDataType(DataType.INT);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        Variable expectedVariable = createIntVariable("variable", 0);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = createLiteralBoolValue(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        Expression expectedLiteralExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedLiteralExpression.setFieldRef(exptectedProbe);
        expectedExpression.setLeft(expectedLiteralExpression);
        expectedAssignment.setValue(expectedExpression);
        exptectedStatement.getThenStatements()
            .add(expectedAssignment);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseWithSelfReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var bool variable = true;
                if (true) {
                    variable = variable;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Variable expectedVariable = createBoolVariable("variable", true);
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = createLiteralBoolValue(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        Expression expectedLiteralExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedLiteralExpression.setFieldRef(expectedVariable);
        expectedExpression.setLeft(expectedLiteralExpression);
        expectedAssignment.setValue(expectedExpression);
        exptectedStatement.getThenStatements()
            .add(expectedAssignment);
        assertThat(model.getStatements()).containsExactly(exptectedStatement);
    }

    @Test
    public void parseIntWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int variable = 0;
                if (true) {
                    variable = ;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE_ASSIGNMENT,
                Diagnostic.SYNTAX_DIAGNOSTIC, "no viable alternative at input ';'");
    }

    @Test
    public void parseBoolWithIntValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var bool variable = true;
                if (true) {
                    variable = 1;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE_ASSIGNMENT, null,
                "Expected a value of type 'bool', got 'int' instead.");
    }

    @Test
    public void parseIntWithBoolValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int variable = 0;
                if (true) {
                    variable = true;
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE_ASSIGNMENT, null,
                "Expected a value of type 'int', got 'bool' instead.");
    }

    private Variable createBoolVariable(String name, boolean value) {
        Variable variable = SmodelFactory.eINSTANCE.createVariable();
        variable.setName(name);
        variable.setDataType(DataType.BOOL);
        Expression expression = createLiteralBoolValue(value);
        variable.setValue(expression);
        return variable;
    }

    private Variable createIntVariable(String name, int value) {
        Variable variable = SmodelFactory.eINSTANCE.createVariable();
        variable.setName(name);
        variable.setDataType(DataType.INT);
        Expression expression = createLiteralIntValue(value);
        variable.setValue(expression);
        return variable;
    }

    private Expression createLiteralIntValue(int value) {
        Expression condition = SmodelFactory.eINSTANCE.createExpression();
        Expression literalExpression = createLiteralIntExpression(value);
        condition.setLeft(literalExpression);
        return condition;
    }

    private Expression createLiteralIntExpression(int value) {
        Expression literalExpression = SmodelFactory.eINSTANCE.createExpression();
        IntLiteral literal = createIntLiteral(value);
        literalExpression.setLiteral(literal);
        return literalExpression;
    }

    private IntLiteral createIntLiteral(int value) {
        IntLiteral literal = SmodelFactory.eINSTANCE.createIntLiteral();
        literal.setValue(value);
        return literal;
    }

    private Expression createLiteralBoolValue(boolean value) {
        Expression condition = SmodelFactory.eINSTANCE.createExpression();
        Expression literalExpression = createLiteralBoolExpression(value);
        condition.setLeft(literalExpression);
        return condition;
    }

    private Expression createLiteralBoolExpression(boolean value) {
        Expression literalExpression = SmodelFactory.eINSTANCE.createExpression();
        BoolLiteral literal = createBoolLiteral(value);
        literalExpression.setLiteral(literal);
        return literalExpression;
    }

    private BoolLiteral createBoolLiteral(boolean value) {
        BoolLiteral literal = SmodelFactory.eINSTANCE.createBoolLiteral();
        literal.setTrue(value);
        return literal;
    }
}
