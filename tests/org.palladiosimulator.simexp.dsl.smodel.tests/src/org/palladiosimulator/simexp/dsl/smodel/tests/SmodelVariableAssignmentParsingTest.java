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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.VariableAssignment;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelVariableAssignmentParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

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
        Variable expectedVariable = smodelCreator.createVariable("condition", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = smodelCreator.createLiteralBoolExpression(false);
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
        Constant exptectedConstant = smodelCreator.createConstant("constant", DataType.INT,
                smodelCreator.createIntLiteral(1));
        Variable expectedVariable = smodelCreator.createVariable("variable", DataType.INT,
                smodelCreator.createIntLiteral(0));
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setFieldRef(exptectedConstant);
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
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createIntLiteral(0));
        Optimizable exptectedOptimizable = smodelCreator.createOptimizable("opti", DataType.INT, expectedBounds);
        Variable expectedVariable = smodelCreator.createVariable("variable", DataType.INT,
                smodelCreator.createIntLiteral(0));
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setFieldRef(exptectedOptimizable);
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
        Probe exptectedProbe = smodelCreator.createProbe("someProbe", DataType.INT, ProbeAdressingKind.ID, "someId");
        Variable expectedVariable = smodelCreator.createVariable("variable", DataType.INT,
                smodelCreator.createIntLiteral(0));
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setFieldRef(exptectedProbe);
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
        Variable expectedVariable = smodelCreator.createVariable("variable", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        IfStatement exptectedStatement = SmodelFactory.eINSTANCE.createIfStatement();
        Expression exptectedCondition = smodelCreator.createLiteralBoolExpression(true);
        exptectedStatement.setCondition(exptectedCondition);
        VariableAssignment expectedAssignment = SmodelFactory.eINSTANCE.createVariableAssignment();
        expectedAssignment.setVariableRef(expectedVariable);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setFieldRef(expectedVariable);
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
}
