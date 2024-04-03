package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Before;
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

    /*
     * @Test public void testBoolExpressionValue() throws Exception { String sb = MODEL_NAME_LINE +
     * """ const bool value = true; const bool value2 = false; """; Smodel model =
     * parserHelper.parse(sb); validationTestHelper.assertNoErrors(model); EList<Constant> constants
     * = model.getConstants(); Constant constant1 = constants.get(0); Constant constant2 =
     * constants.get(1);
     * 
     * boolean actualCalculatedValue1 = calculator.calculateBoolean(constant1.getValue()); boolean
     * actualCalculatedValue2 = calculator.calculateBoolean(constant2.getValue());
     * 
     * assertTrue(actualCalculatedValue1); assertFalse(actualCalculatedValue2); }
     * 
     * @Test public void testIntExpressionValue() throws Exception { String sb = MODEL_NAME_LINE +
     * """ const int value = 1; const int value2 = -1; """;
     * 
     * Smodel model = parserHelper.parse(sb); validationTestHelper.assertNoErrors(model);
     * EList<Constant> constants = model.getConstants(); Constant constant1 = constants.get(0);
     * Constant constant2 = constants.get(1);
     * 
     * int actualCalculatedInt1 = calculator.calculateInteger(constant1.getValue()); int
     * actualCalculatedInt2 = calculator.calculateInteger(constant2.getValue());
     * 
     * assertEquals(1, actualCalculatedInt1); assertEquals(-1, actualCalculatedInt2); }
     * 
     * @Test public void testFloatExpressionValue() throws Exception { String sb = MODEL_NAME_LINE +
     * """ const float value = 1.5; const float value2 = .5e-1; """;
     * 
     * Smodel model = parserHelper.parse(sb); validationTestHelper.assertNoErrors(model);
     * EList<Constant> constants = model.getConstants(); Constant constant1 = constants.get(0);
     * Constant constant2 = constants.get(1);
     * 
     * float actualCalculatedFloat1 = (float) calculator.calculateDouble(constant1.getValue());
     * float actualCalculatedFloat2 = (float) calculator.calculateDouble(constant2.getValue());
     * 
     * assertEquals(1.5f, actualCalculatedFloat1, 0.0f); assertEquals(.5e-1f,
     * actualCalculatedFloat2, 0.0f); }
     * 
     * @Test public void testStringExpressionValue() throws Exception { String sb = MODEL_NAME_LINE
     * + """ const string value = "word"; """;
     * 
     * Smodel model = parserHelper.parse(sb); validationTestHelper.assertNoErrors(model);
     * EList<Constant> constants = model.getConstants(); Constant constant1 = constants.get(0);
     * 
     * String actualCalculatedString = calculator.calculateString(constant1.getValue());
     * 
     * assertEquals("word", actualCalculatedString); }
     * 
     * @Test public void testConstantExpressionValue() throws Exception { String sb =
     * MODEL_NAME_LINE + """ const int value = 1; const int value2 = value; const int value3 =
     * value2; """;
     * 
     * Smodel model = parserHelper.parse(sb);
     */
    /*
     * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
     * 
     * Constant constant = model.getConstants() .get(0); int value = ((Number)
     * interpreter.getValue(constant)).intValue(); Constant constant2 = model.getConstants()
     * .get(1); int value2 = ((Number) interpreter.getValue(constant2)).intValue(); Constant
     * constant3 = model.getConstants() .get(2); int value3 = ((Number)
     * interpreter.getValue(constant3)).intValue();
     * 
     * Assert.assertEquals(1, value); Assert.assertEquals(value, value2);
     * Assert.assertEquals(value2, value3);
     */

    /*
     * validationTestHelper.assertNoErrors(model); EList<Constant> constants = model.getConstants();
     * Constant constant1 = constants.get(0); Constant constant2 = constants.get(1); Constant
     * constant3 = constants.get(2);
     * 
     * int actualCalculatedInt1 = calculator.calculateInteger(constant1.getValue()); int
     * actualCalculatedInt2 = calculator.calculateInteger(constant2.getValue()); int
     * actualCalculatedInt3 = calculator.calculateInteger(constant3.getValue());
     * 
     * assertEquals(1, actualCalculatedInt1); assertEquals(actualCalculatedInt1,
     * actualCalculatedInt2); assertEquals(actualCalculatedInt2, actualCalculatedInt3);
     * 
     * }
     * 
     * @Test public void testVariableExpressionValue() throws Exception { String sb =
     * MODEL_NAME_LINE + """ optimizable int{2, 1} value; optimizable int[0, 1, 1] value2; """;
     * 
     * Smodel model = parserHelper.parse(sb);
     */
    /*
     * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
     * 
     * Optimizable variable = model.getOptimizables() .get(0); int value = ((Number)
     * interpreter.getValue(variable)).intValue(); Optimizable variable2 = model.getOptimizables()
     * .get(1); int value2 = ((Number) interpreter.getValue(variable2)).intValue();
     * 
     * Assert.assertEquals(2, value); Assert.assertEquals(0, value2);
     */
    /*
     * }
     * 
     * @Test public void testProbeExpressionValue() throws Exception { String sb = MODEL_NAME_LINE +
     * """ probe bool value: "someId"; """;
     * 
     * Smodel model = parserHelper.parse(sb);
     */
    /*
     * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
     * 
     * Probe probe = model.getProbes() .get(0); boolean value = (boolean)
     * interpreter.getValue(probe);
     * 
     * Assert.assertTrue(value);
     */
    /*
     * }
     * 
     * @Test public void testRuntimeExpressionValue() throws Exception { String sb = MODEL_NAME_LINE
     * + """ runtime int value: simple: a[0].b; """;
     * 
     * Smodel model = parserHelper.parse(sb);
     */
    /*
     * interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
     * 
     * Runtime runtime = model.getRuntimes() .get(0); int value = ((Number)
     * interpreter.getValue(runtime)).intValue();
     * 
     * Assert.assertEquals(1, value);
     */
    // }

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
    public void testBoolNotExpression1() throws Exception {
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
    public void testBoolNotExpression2() throws Exception {
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

    @Test
    public void testDivisionExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 2 / 2;
                const float value2 = 1 / 2;
                const float value3 = 2 / 1;
                const float value4 = -1 / 1;
                const float value5 = 1 / -1
                const float value6 = (1 / 2) / 4;
                const float value7 = 1 / (2 / 4);
                const float value8 = 0 / 0;
                const float value9 = 1 / 0;
                const float value10 = -1 / 0;
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        float value = ((Number) interpreter.getValue(constant)).floatValue();
//        Constant constant2 = model.getConstants()
//            .get(1);
//        float value2 = ((Number) interpreter.getValue(constant2)).floatValue();
//        Constant constant3 = model.getConstants()
//            .get(2);
//        float value3 = ((Number) interpreter.getValue(constant3)).floatValue();
//        Constant constant4 = model.getConstants()
//            .get(3);
//        float value4 = ((Number) interpreter.getValue(constant4)).floatValue();
//        Constant constant5 = model.getConstants()
//            .get(4);
//        float value5 = ((Number) interpreter.getValue(constant5)).floatValue();
//        Constant constant6 = model.getConstants()
//            .get(5);
//        float value6 = ((Number) interpreter.getValue(constant6)).floatValue();
//        Constant constant7 = model.getConstants()
//            .get(6);
//        float value7 = ((Number) interpreter.getValue(constant7)).floatValue();
//        Constant constant8 = model.getConstants()
//            .get(7);
//        float value8 = ((Number) interpreter.getValue(constant8)).floatValue();
//        Constant constant9 = model.getConstants()
//            .get(8);
//        float value9 = ((Number) interpreter.getValue(constant9)).floatValue();
//        Constant constant10 = model.getConstants()
//            .get(9);
//        float value10 = ((Number) interpreter.getValue(constant10)).floatValue();
//
//        Assert.assertEquals(1f, value, 0.0f);
//        Assert.assertEquals(0.5f, value2, 0.0f);
//        Assert.assertEquals(2f, value3, 0.0f);
//        Assert.assertEquals(-1f, value4, 0.0f);
//        Assert.assertEquals(-1f, value5, 0.0f);
//        Assert.assertEquals(0.125f, value6, 0.0f);
//        Assert.assertEquals(2f, value7, 0.0f);
//        Assert.assertEquals(Float.NaN, value8, 0.0f);
//        Assert.assertEquals(Float.POSITIVE_INFINITY, value9, 0.0f);
//        Assert.assertEquals(Float.NEGATIVE_INFINITY, value10, 0.0f);
    }

    @Test
    public void testModuloExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value =  1 % 1;
                const int value2 = 4 % 2;
                const int value3 = 2 % 4;
                const int value4 = -1 % 2;
                const int value5 = 1 % -2;
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        int value = ((Number) interpreter.getValue(constant)).intValue();
//        Constant constant2 = model.getConstants()
//            .get(1);
//        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
//        Constant constant3 = model.getConstants()
//            .get(2);
//        int value3 = ((Number) interpreter.getValue(constant3)).intValue();
//        Constant constant4 = model.getConstants()
//            .get(3);
//        int value4 = ((Number) interpreter.getValue(constant4)).intValue();
//        Constant constant5 = model.getConstants()
//            .get(4);
//        int value5 = ((Number) interpreter.getValue(constant5)).intValue();
//
//        Assert.assertEquals(0, value);
//        Assert.assertEquals(0, value2);
//        Assert.assertEquals(2, value3);
//        Assert.assertEquals(-1, value4);
//        Assert.assertEquals(1, value5);
    }

    @Test
    public void testDistributivity() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 2 * (3 + 4);
                const int value = 2 * 3 + 2 * 4;
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        int value = ((Number) interpreter.getValue(constant)).intValue();
//        Constant constant2 = model.getConstants()
//            .get(1);
//        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
//
//        Assert.assertEquals(14, value);
//        Assert.assertEquals(value, value2);
    }

    @Test
    public void testLogicPrecedence() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value = true || true && false;
                const bool value2 = (true || true) && false;
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        boolean value = (boolean) interpreter.getValue(constant);
//        Constant constant2 = model.getConstants()
//            .get(1);
//        boolean value2 = (boolean) interpreter.getValue(constant2);
//
//        Assert.assertTrue(value);
//        Assert.assertFalse(value2);
    }

    @Test
    public void testArithmeticPrecedence() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value = 1 + 1 * 2;
                const int value2 = (1 + 1) * 2;
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        int value = ((Number) interpreter.getValue(constant)).intValue();
//        Constant constant2 = model.getConstants()
//            .get(1);
//        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
//
//        Assert.assertEquals(3, value);
//        Assert.assertEquals(4, value2);
    }

    @Test
    public void testArithmeticPrecedence2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 2 - 1 / 2;
                const float value2 = (2 - 1) / 2;
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        float value = ((Number) interpreter.getValue(constant)).floatValue();
//        Constant constant2 = model.getConstants()
//            .get(1);
//        float value2 = ((Number) interpreter.getValue(constant2)).floatValue();
//
//        Assert.assertEquals(1.5f, value, 0.0f);
//        Assert.assertEquals(0.5f, value2, 0.0f);
    }

    @Test
    public void testFloatValuePrecision() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = 1.4e-45;
                const float value2 = 3.4028235e38;
                const float value3 = 1.17549435e-38
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        float value = ((Number) interpreter.getValue(constant)).floatValue();
//        Constant constant2 = model.getConstants()
//            .get(1);
//        float value2 = ((Number) interpreter.getValue(constant2)).floatValue();
//        Constant constant3 = model.getConstants()
//            .get(2);
//        float value3 = ((Number) interpreter.getValue(constant3)).floatValue();
//
//        Assert.assertEquals(Float.MIN_VALUE, value, 0.0f);
//        Assert.assertEquals(Float.MAX_VALUE, value2, 0.0f);
//        Assert.assertEquals(Float.MIN_NORMAL, value3, 0.0f);
    }

    @Test
    public void testComplexExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value = -(2 + 3 * 10 / 2) - 3;
                const bool value2 = !((value < 0 || value >= 10) && value == -20);
                """;

        Smodel model = parserHelper.parse(sb);
//        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
//
//        Constant constant = model.getConstants()
//            .get(0);
//        float value = ((Number) interpreter.getValue(constant)).floatValue();
//        Constant constant2 = model.getConstants()
//            .get(1);
//        boolean value2 = (boolean) interpreter.getValue(constant2);
//
//        Assert.assertEquals(-20f, value, 0.0f);
//        Assert.assertFalse(value2);
    }

    private Constant getFirstConstant(Smodel model) {
        EList<Constant> constants = model.getConstants();
        Constant constant = constants.get(0);
        return constant;
    }
}
