package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Statement;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;
import org.palladiosimulator.simexp.dsl.smodel.util.ExpressionUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelIfParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    private ExpressionUtil expressionUtil = new ExpressionUtil();

    @Test
    public void parseIfStatementWithLiteralCondition() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {}
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        IfStatement statement = (IfStatement) statements.get(0);
        Assert.assertTrue(!statement.isWithElse());
        Expression condition = expressionUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) condition.getLiteral()).isTrue());
        EList<Statement> thenStatements = statement.getThenStatements();
        Assert.assertTrue(thenStatements.isEmpty());
    }

    @Test
    public void parseIfStatementWithFieldCondition() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool condition = true;
                if (condition) {}
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> constants = model.getConstants();
        assertEquals(1, constants.size());
        Constant boolConditionVar = constants.get(0);
        Assert.assertEquals("condition", boolConditionVar.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionVar.getDataType());
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        IfStatement statement = (IfStatement) statements.get(0);
        Assert.assertTrue(!statement.isWithElse());
        Expression condition = expressionUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertEquals(boolConditionVar, condition.getFieldRef());
        Assert.assertEquals(DataType.BOOL, condition.getFieldRef()
            .getDataType());
        Assert.assertEquals("condition", condition.getFieldRef()
            .getName());
        EList<Statement> thenStatements = statement.getThenStatements();
        Assert.assertTrue(thenStatements.isEmpty());
    }

    @Test
    public void parseConsecutiveIfStatements() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {}
                if (true) {}
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(2, statements.size());
        IfStatement firstStatement = (IfStatement) statements.get(0);
        Assert.assertTrue(!firstStatement.isWithElse());
        Expression firstCondition = expressionUtil.getNextExpressionWithContent(firstStatement.getCondition());
        Assert.assertTrue(firstCondition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) firstCondition.getLiteral()).isTrue());
        EList<Statement> firstThenStatements = firstStatement.getThenStatements();
        Assert.assertTrue(firstThenStatements.isEmpty());
        IfStatement secondStatement = (IfStatement) statements.get(1);
        Assert.assertTrue(!secondStatement.isWithElse());
        Expression secondCondition = expressionUtil.getNextExpressionWithContent(secondStatement.getCondition());
        Assert.assertTrue(secondCondition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) secondCondition.getLiteral()).isTrue());
        EList<Statement> secondThenStatements = secondStatement.getThenStatements();
        Assert.assertTrue(secondThenStatements.isEmpty());
    }

    @Test
    public void parseNestedIfStatements() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool condition = true;
                if (condition) {
                    if (condition) {}
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> constants = model.getConstants();
        assertEquals(1, constants.size());
        Constant boolConditionField = constants.get(0);
        Assert.assertEquals("condition", boolConditionField.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionField.getDataType());
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        IfStatement outerStatement = (IfStatement) statements.get(0);
        Assert.assertTrue(!outerStatement.isWithElse());
        Expression outerCondition = expressionUtil.getNextExpressionWithContent(outerStatement.getCondition());
        Field outerIfConditionField = outerCondition.getFieldRef();
        Assert.assertEquals(boolConditionField, outerIfConditionField);
        EList<Statement> outerThenStatements = outerStatement.getThenStatements();
        Assert.assertEquals(1, outerThenStatements.size());
        IfStatement innerStatement = (IfStatement) outerThenStatements.get(0);
        Assert.assertTrue(!innerStatement.isWithElse());
        Expression innerCondition = expressionUtil.getNextExpressionWithContent(innerStatement.getCondition());
        Field innerIfConditionField = innerCondition.getFieldRef();
        Assert.assertEquals(boolConditionField, innerIfConditionField);
        EList<Statement> innerThenStatements = innerStatement.getThenStatements();
        Assert.assertTrue(innerThenStatements.isEmpty());
    }

    @Test
    public void parseIfElseStatement() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if (true) {} else {}
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        IfStatement statement = (IfStatement) statements.get(0);
        Assert.assertTrue(statement.isWithElse());
        Expression condition = expressionUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) condition.getLiteral()).isTrue());
        EList<Statement> thenStatements = statement.getThenStatements();
        Assert.assertTrue(thenStatements.isEmpty());
        EList<Statement> elseStatements = statement.getElseStatements();
        Assert.assertTrue(elseStatements.isEmpty());
    }

    @Test
    public void parseNestedIfElseStatement() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool condition = true;
                if (condition) {} else {
                    if (condition) {}
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> constants = model.getConstants();
        assertEquals(1, constants.size());
        Constant boolConditionField = constants.get(0);
        Assert.assertEquals("condition", boolConditionField.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionField.getDataType());
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        IfStatement outerStatement = (IfStatement) statements.get(0);
        Assert.assertTrue(outerStatement.isWithElse());
        Expression outerCondition = expressionUtil.getNextExpressionWithContent(outerStatement.getCondition());
        Field outerIfConditionField = outerCondition.getFieldRef();
        Assert.assertEquals(boolConditionField, outerIfConditionField);
        EList<Statement> outerThenStatements = outerStatement.getThenStatements();
        Assert.assertTrue(outerThenStatements.isEmpty());
        EList<Statement> outerElseStatements = outerStatement.getElseStatements();
        Assert.assertEquals(1, outerElseStatements.size());
        IfStatement innerStatement = (IfStatement) outerElseStatements.get(0);
        Assert.assertTrue(!innerStatement.isWithElse());
        Expression innerCondition = expressionUtil.getNextExpressionWithContent(innerStatement.getCondition());
        Field innerIfConditionField = innerCondition.getFieldRef();
        Assert.assertEquals(boolConditionField, innerIfConditionField);
        EList<Statement> innerThenStatements = innerStatement.getThenStatements();
        Assert.assertTrue(innerThenStatements.isEmpty());
    }

    @Test
    public void parseIfStatementWithActionCall() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action scaleOut(param float balancingFactor);
                if (false) {
                    scaleOut(balancingFactor=1.0);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        IfStatement statement = (IfStatement) statements.get(0);
        Assert.assertTrue(!statement.isWithElse());
        Expression condition = expressionUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(false, ((BoolLiteral) condition.getLiteral()).isTrue());
        EList<Statement> thenStatements = statement.getThenStatements();
        Assert.assertEquals(1, thenStatements.size());
    }

    @Test
    public void parseIfStatementWithWrongLiteralType() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if ("condition") {}
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'bool', got 'string' instead.");
    }

    @Test
    public void parseIfStatementWithWrongFieldType() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const string condition = "true";
                if (condition) {}
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'bool', got 'string' instead.");
    }

    @Test
    public void parseComplexStatement() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                //Variable declarations
                optimizable float{1.0, 2.0} i;
                // action declaration
                action a(param float factor);
                action anotherA(param float factor, optimizable float[1,2,1] anotherFactor);
                // rule block
                if (true) {
                    a(factor=i);        // execute action
                    anotherA(factor=i); // execute another action
                }
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
}