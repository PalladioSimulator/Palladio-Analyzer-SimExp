package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class ExpressionCalculatorTest {
    public static final String MODEL_NAME_LINE = "modelName = \"name\";";

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
    public void testBoolOrExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true || true;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        boolean actualCalculatedValue = calculator.calculateBoolean(constant.getValue());

        assertTrue(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertTrue(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertFalse(actualCalculatedValue);
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

        assertEquals(1, actualCalculatedValue);
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

        assertEquals(2, actualCalculatedValue);
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

        assertEquals(2, actualCalculatedValue);
    }

    @Test
    public void testFloatAdditionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 1.0 + 0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0f);
    }

    @Test
    public void testFloatAdditionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = -1.0 + 3.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.0f);
    }

    @Test
    public void testFloatAdditionExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 3.0 + -1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.0f);
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

        assertEquals(1, actualCalculatedValue);
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

        assertEquals(2, actualCalculatedValue);
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

        assertEquals(4, actualCalculatedValue);
    }

    @Test
    public void testFloatSubtractionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 1.0 - 0.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0f);
    }

    @Test
    public void testFloatSubtractionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = -1.0 - -3.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.0f);
    }

    @Test
    public void testFloatSubtractionExpression3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 3.0 - -1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0f);
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

        assertEquals(1, actualCalculatedValue);
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

        assertEquals(2, actualCalculatedValue);
    }

    @Test
    public void testFloatMultiplicationExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 2.1 * 1.1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(2.31f);
    }

    @Ignore
    @Test
    public void testIntDivisionExpression1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 / 2;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertEquals(1, actualCalculatedValue);
    }

    @Ignore
    @Test
    public void testIntDivisionExpression2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = -1 / 1;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        int actualCalculatedValue = calculator.calculateInteger(constant.getValue());

        assertEquals(-1, actualCalculatedValue);
    }

    @Ignore
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
                const bool value = !false || false;
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

    @Ignore
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

    @Ignore
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

    @Ignore
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

    @Ignore
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
    public void testFloatPrecedence1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 1.0 + 1.0 * 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(3.0f);
    }

    @Test
    public void testFloatPrecedence2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 2.0 * 1.0 + 1.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(3.0f);
    }

    @Test
    public void testFloatPrecedence3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 2.0 - 1.0 / 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.5f);
    }

    @Test
    public void testFloatPrecedence4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 2.0 / 1.0 + 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0f);
    }

    @Test
    public void testFloatPrecedenceParenthesis1() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = (1.0 + 1.0) * 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0f);
    }

    @Test
    public void testFloatPrecedenceParenthesis2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 2.0 * (1.0 + 1.0);
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(4.0f);
    }

    @Test
    public void testFloatPrecedenceParenthesis3() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = (2.0 - 1.0) / 2.0;
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(0.5f);
    }

    @Test
    public void testFloatPrecedenceParenthesis4() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 3.0 / (1.0 + 2.0);
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoErrors(model);
        Constant constant = getFirstConstant(model);

        float actualCalculatedValue = calculator.calculateFloat(constant.getValue());

        assertThat(actualCalculatedValue).isEqualTo(1.0f);
    }

    private Constant getFirstConstant(Smodel model) {
        EList<Constant> constants = model.getConstants();
        Constant constant = constants.get(0);
        return constant;
    }
}
