package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class ExpressionCalculatorTest {
    private static final String MODEL_NAME_LINE = "modelName = \"name\";";
    private static final double DOUBLE_EPSILON = 1e-15;

    private ExpressionCalculator calculator;

    private ParseHelper<Smodel> parserHelper;
    private ValidationTestHelper validationTestHelper;

    @Mock
    private IFieldValueProvider fieldValueProvider;

    @Before
    public void setUp() {
        initMocks(this);
        calculator = new ExpressionCalculator(fieldValueProvider);

        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Smodel>>() {
        }));
        validationTestHelper = injector.getInstance(ValidationTestHelper.class);
    }

    @Test
    public void testBoolExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolOrExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true || true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolOrExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true || false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolOrExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = false || true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolOrExpression4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = false || false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolAndExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true && true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolAndExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true && false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolAndExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = false && true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolAndExpression4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = false && false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolComplementExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = !false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolComplementExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = !true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolEqualExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true == true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolEqualExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true == false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolEqualExpressionInt1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 == 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolEqualExpressionInt2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 == 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolEqualExpressionDouble1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1.0 == 1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolEqualExpressionDouble2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1.0 == 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertTrue(actualCalculatedValue);
    }

    @Test
    public void testBoolEqualExpressionDouble3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 == 1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertTrue(actualCalculatedValue);
    }

    @Test
    public void testBoolEqualExpressionDoublePrecision1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 0.1 + 0.2 == 0.3;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertTrue(actualCalculatedValue);
    }

    @Test
    public void testBoolEqualExpressionString1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = "a" == "a";
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolEqualExpressionString2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = "a" == "b";
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolUnequalExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true != true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolUnequalExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true != false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolUnequalExpressionString1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = "a" != "a";
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolUnequalExpressionString2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = "a" != "b";
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolSmallerExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 < 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolSmallerExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 < 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolSmallerExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 2 < 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolSmallerOrEqualExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 <= 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolSmallerOrEqualExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 <= 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolSmallerOrEqualExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 2 <= 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolGreaterOrEqualExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 2 >= 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolGreaterOrEqualExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 >= 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolGreaterOrEqualExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 >= 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolGreaterExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 2 > 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolGreaterExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 > 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testBoolGreaterExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = 1 > 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isFalse();
    }

    @Test
    public void testIntAssignmentExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1);
    }

    @Test
    public void testIntAdditionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 1 + 0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1);
    }

    @Test
    public void testIntAdditionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = -1 + 3;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2);
    }

    @Test
    public void testIntAdditionExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 3 + -1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2);
    }

    @Test
    public void testDoubleAssignmentExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0);
    }

    @Test
    public void testDoubleAssignmentExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0);
    }

    @Test
    public void testDoubleAdditionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 1.0 + 0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0);
    }

    @Test
    public void testDoubleAdditionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = -1.0 + 3.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.0);
    }

    @Test
    public void testDoubleAdditionExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 3.0 + -1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.0);
    }

    @Test
    public void testDoubleAdditionPrecision1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 0.1 + 0.2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(0.3, offset(DOUBLE_EPSILON));
    }

    @Test
    public void testIntSubtractionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 1 - 0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1);
    }

    @Test
    public void testIntSubtractionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = -1 - -3;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2);
    }

    @Test
    public void testIntSubtractionExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 3 - -1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4);
    }

    @Test
    public void testDoubleSubtractionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 1.0 - 0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0);
    }

    @Test
    public void testDoubleSubtractionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = -1.0 - -3.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.0);
    }

    @Test
    public void testDoubleSubtractionExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 3.0 - -1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0);
    }

    @Test
    public void testIntInversionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = -(-1);
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1);
    }

    @Test
    public void testIntMultiplicationExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 * 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2);
    }

    public class DComp {
        double value;
    }

    @Test
    public void testDoubleMultiplicationPrecision1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 2.1 * 1.1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.31, offset(DOUBLE_EPSILON));
    }

    @Test
    public void testIntDivisionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 / 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1);
    }

    @Test
    public void testIntDivisionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = -1 / 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(-1);
    }

    @Test
    public void testIntDivisionExpressionByZero() throws Exception {
        String sb = MODEL_NAME_LINE + """
                 const int value = 1 / 0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        assertThatExceptionOfType(ArithmeticException.class).isThrownBy(() -> {
            calculator.calculateInteger(constant.getValue());
        })
            .withMessage("/ by zero");
    }

    @Test
    public void testDoubleDivisionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 2.0 / 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0f);
    }

    @Test
    public void testDoubleDivisionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = -1.0 / 1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(-1.0f);
    }

    @Test
    public void testDoubleDivisionExpressionByZero1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                 const double value = 0.0 / 0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isNaN();
    }

    @Test
    public void testDoubleDivisionExpressionByZero2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                 const double value = 1.0 / 0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    public void testDoubleDivisionExpressionByZero3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                 const double value = -1.0 / 0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testDoubleDivisionExpressionByZero4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                 const double value = 1.0 / -0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testIntModuloExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value =  4 % 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(0);
    }

    @Test
    public void testIntModuloExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value =  -1 % 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(-1);
    }

    @Test
    public void testIntModuloExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value =  1 % -2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1);
    }

    @Test
    public void testBoolPrecedence1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true || false && false || false;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testBoolPrecedence2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = !true || true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertThat(actualCalculatedValue).isTrue();
    }

    @Test
    public void testIntPrecedence1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 1 + 1 * 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(3);
    }

    @Test
    public void testIntPrecedence2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 * 1 + 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(3);
    }

    @Test
    public void testIntPrecedence3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 - 1 / 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2);
    }

    @Test
    public void testIntPrecedence4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 / 1 + 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4);
    }

    @Test
    public void testIntPrecedenceParenthesis1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = (1 + 1) * 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4);
    }

    @Test
    public void testIntPrecedenceParenthesis2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 * (1 + 1);
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4);
    }

    @Test
    public void testIntPrecedenceParenthesis3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = (2 - 1) / 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(0);
    }

    @Test
    public void testIntPrecedenceParenthesis4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 / (1 + 2);
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(0);
    }

    @Test
    public void testDoublePrecedence1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 1.0 + 1.0 * 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(3.0);
    }

    @Test
    public void testDoublePrecedence2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 2.0 * 1.0 + 1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(3.0);
    }

    @Test
    public void testDoublePrecedence3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 2.0 - 1.0 / 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.5);
    }

    @Test
    public void testDoublePrecedence4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 2.0 / 1.0 + 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0);
    }

    @Test
    public void testDoublePrecedenceParenthesis1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = (1.0 + 1.0) * 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0);
    }

    @Test
    public void testDoublePrecedenceParenthesis2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 2.0 * (1.0 + 1.0);
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0f);
    }

    @Test
    public void testDoublePrecedenceParenthesis3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = (2.0 - 1.0) / 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(0.5f);
    }

    @Test
    public void testDoublePrecedenceParenthesis4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const double value = 3.0 / (1.0 + 2.0);
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        double actualCalculatedValue = calculator.calculateDouble(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0);
    }

    private Constant getFirstConstant(Smodel model) {
        EList<Constant> constants = model.getConstants();
        Constant constant = constants.get(0);
        return constant;
    }
}
