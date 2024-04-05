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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelExpressionParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseSimpleExpressionWithBrackets() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = (1);
                """;

        Smodel model = parserHelper.parse(sb);

        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.INT,
                smodelCreator.createIntLiteral(1));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseOrExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = true || false;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.OR);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createBoolLiteral(true));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createBoolLiteral(false));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseAndExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = true && false;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.AND);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createBoolLiteral(true));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createBoolLiteral(false));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseEqualExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = "some" == "thing";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.EQUAL);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createStringLiteral("some"));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createStringLiteral("thing"));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseUnequalExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = "some" != "thing";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.UNEQUAL);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createStringLiteral("some"));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createStringLiteral("thing"));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseNotExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = !true;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.NOT);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createBoolLiteral(true));
        expectedExpression.setLeft(expectedExpressionLeft);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseSmallerExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = 1 < 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.SMALLER);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseSmallerOrEqualExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = 1 <= 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.SMALLER_OR_EQUAL);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseGreaterOrEqualExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = 1 >= 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.GREATER_OR_EQUAL);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseGreaterExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = 1 > 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.BOOL, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.GREATER);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseAdditionExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1 + 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.INT, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.PLUS);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseSubtractionExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1 - 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.INT, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.MINUS);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseAdditiveInverseExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = -1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.INT, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.MINUS);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseMultiplicationExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const double constant = 1.0 * 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.DOUBLE, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.MULTIPLY);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createDoubleLiteral(1.0));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseDivisionExpressionInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1 / 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.INT, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.DIVIDE);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseDivisionExpressionDouble() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const double constant = 1.0 / 2.0;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.DOUBLE, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.DIVIDE);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createDoubleLiteral(1.0));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createDoubleLiteral(2.0));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseModuloExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1 % 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("constant", DataType.INT, null);
        Expression expectedExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression.setOp(Operation.MODULO);
        Expression expectedExpressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expectedExpression.setLeft(expectedExpressionLeft);
        Expression expectedExpressionRight = SmodelFactory.eINSTANCE.createExpression();
        expectedExpressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expectedExpression.setRight(expectedExpressionRight);
        expectedConstant.setValue(expectedExpression);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseDisjunctionWithInvalidTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = true || 1.0;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'bool', got 'double' instead.");
    }

    @Test
    public void parseConjunctionWithInvalidTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = "true" && false;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'bool', got 'string' instead.");
    }

    @Test
    public void parseEqualityWithInvalidTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = "1.0" == 1.0;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'int' or 'double', got 'string' instead.");
    }

    @Test
    public void parseNegationWithInvalidType() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = !1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'bool', got 'int' instead.");
    }

    @Test
    public void parseComparisonWithInvalidTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = 1 >= false;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'int' or 'double', got 'bool' instead.");
    }

    @Test
    public void parseAdditionWithInvalidTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = true + 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'int' or 'double', got 'bool' instead.");
    }

    @Test
    public void parseAdditiveInverseWithInvalidType() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = -false;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'int' or 'double', got 'bool' instead.");
    }

    @Test
    public void parseMultiplicationWithInvalidTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const double constant = 1.5 * "2";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.EXPRESSION, null,
                "Expected a value of type 'int' or 'double', got 'string' instead.");
    }

    @Test
    public void parseExpressionWithWrongBrackets() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const string word = (("word");
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "missing ')' at ';'");
    }
}
