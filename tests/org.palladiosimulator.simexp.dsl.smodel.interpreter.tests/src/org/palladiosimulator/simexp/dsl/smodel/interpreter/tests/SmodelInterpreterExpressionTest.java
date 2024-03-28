package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import javax.inject.Inject;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mocks.TestProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mocks.TestVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class SmodelInterpreterExpressionTest {
    public static final String MODEL_NAME_LINE = "modelName = \"name\";";

    private ParseHelper<Smodel> parserHelper;
    private SmodelInterpreter interpreter;
    private VariableValueProvider vvp;
    private ProbeValueProvider pvp;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Before
    public void setUp() {
        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Smodel>>() {
        }));

        vvp = new TestVariableValueProvider();
        pvp = new TestProbeValueProvider();
    }

    @Test
    public void testBoolExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = true;
                const bool value2 = false;
                if (value1||value2) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Assert.assertTrue(value);
        Assert.assertFalse(value2);
    }

    @Test
    public void testIntExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = 1;
                const int value2 = -1;
                if (value1+value2==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = (int) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = (int) interpreter.getValue(constant2);
        Assert.assertEquals(1, value);
        Assert.assertEquals(-1, value2);
    }

    @Test
    public void testFloatExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value1 = 1.5;
                const float value2 = .5e-1;
                if (value1+value2==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        float value = (float) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        float value2 = (float) interpreter.getValue(constant2);
        Assert.assertEquals(1.5f, value, 0.0f);
        Assert.assertEquals(.5e-1f, value2, 0.0f);
    }

    @Test
    public void testStringExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const string value = "word";
                if (value=="") {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        String value = (String) interpreter.getValue(constant);

        Assert.assertEquals("word", value);
    }

    @Test
    public void testConstantExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = 1;
                const int value2 = value1 + 0;
                if (value2==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);
        int value = ((Number) interpreter.getValue(constant)).intValue();
        Constant constant2 = model.getConstants()
            .get(1);

        int value2 = ((Number) interpreter.getValue(constant2)).intValue();

        Assert.assertEquals(1, value);
        Assert.assertEquals(value, value2);
    }

    @Test
    public void testVariableExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                optimizable int{2, 1} value1;
                optimizable int[0, 1, 1] value2;
                if (value1+value2==0) {}
                """;

        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Optimizable variable = model.getOptimizables()
            .get(0);

        int value = ((Number) interpreter.getValue(variable)).intValue();

        Optimizable variable2 = model.getOptimizables()
            .get(1);
        int value2 = ((Number) interpreter.getValue(variable2)).intValue();
        Assert.assertEquals(2, value);
        Assert.assertEquals(0, value2);
    }

    @Test
    public void testProbeExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                probe bool value: id="someId";
                if (value) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Probe probe = model.getProbes()
            .get(0);

        boolean value = (boolean) interpreter.getValue(probe);

        Assert.assertTrue(value);
    }

    @Test
    public void testOrExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = true || true;
                const bool value2 = true || false;
                const bool value3 = false || true;
                const bool value4 = false || false;
                if (value1||value2||value3||value4) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Constant constant3 = model.getConstants()
            .get(2);
        boolean value3 = (boolean) interpreter.getValue(constant3);
        Constant constant4 = model.getConstants()
            .get(3);
        boolean value4 = (boolean) interpreter.getValue(constant4);
        Assert.assertTrue(value);
        Assert.assertTrue(value2);
        Assert.assertTrue(value3);
        Assert.assertFalse(value4);
    }

    @Test
    public void testAndExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = true && true;
                const bool value2 = true && false;
                const bool value3 = false && true;
                const bool value4 = false && false;
                if (value1||value2||value3||value4) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Constant constant3 = model.getConstants()
            .get(2);
        boolean value3 = (boolean) interpreter.getValue(constant3);
        Constant constant4 = model.getConstants()
            .get(3);
        boolean value4 = (boolean) interpreter.getValue(constant4);

        Assert.assertTrue(value);
        Assert.assertFalse(value2);
        Assert.assertFalse(value3);
        Assert.assertFalse(value4);
    }

    @Test
    public void testNotExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = !false;
                const bool value2 = !true;
                if (value1||value2) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Assert.assertTrue(value);
        Assert.assertFalse(value2);
    }

    @Test
    public void testEqualExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = "word" == "word";
                const bool value2 = "word" == "anotherWord";
                if (value1||value2) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Assert.assertTrue(value);
        Assert.assertFalse(value2);
    }

    @Test
    public void testUnequalExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = 1.5 != 2.0;
                const bool value2 = 1.0 != 1.0;
                if (value1||value2) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Assert.assertTrue(value);
        Assert.assertFalse(value2);
    }

    @Test
    public void testSmallerExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = 1 < 2;
                const bool value2 = 1 < 1;
                const bool value3 = 2 < 1;
                if (value1||value2||value3) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Constant constant3 = model.getConstants()
            .get(2);
        boolean value3 = (boolean) interpreter.getValue(constant3);
        Assert.assertTrue(value);
        Assert.assertFalse(value2);
        Assert.assertFalse(value3);
    }

    @Test
    public void testSmallerOrEqualExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = 1 <= 2;
                const bool value2 = 1 <= 1;
                const bool value3 = 2 <= 1;
                if (value1||value2||value3) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Constant constant3 = model.getConstants()
            .get(2);
        boolean value3 = (boolean) interpreter.getValue(constant3);
        Assert.assertTrue(value);
        Assert.assertTrue(value2);
        Assert.assertFalse(value3);
    }

    @Test
    public void testGreaterOrEqualExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = 2 >= 1;
                const bool value2 = 1 >= 1;
                const bool value3 = 1 >= 2;
                if (value1||value2||value3) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Constant constant3 = model.getConstants()
            .get(2);
        boolean value3 = (boolean) interpreter.getValue(constant3);

        Assert.assertTrue(value);
        Assert.assertTrue(value2);
        Assert.assertFalse(value3);
    }

    @Test
    public void testGreaterExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = 2 > 1;
                const bool value2 = 1 > 1;
                const bool value3 = 1 > 2;
                if (value1||value2||value3) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Constant constant3 = model.getConstants()
            .get(2);
        boolean value3 = (boolean) interpreter.getValue(constant3);
        Assert.assertTrue(value);
        Assert.assertFalse(value2);
        Assert.assertFalse(value3);
    }

    @Test
    public void testAdditionExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = 1 + 0;
                const int value2 = 0 + 1;
                const int value3 = (1 + 2) + 3;
                const int value4 = 1 + (2 + 3);
                if (value1+value2+value3+value4==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = ((Number) interpreter.getValue(constant)).intValue();

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Constant constant3 = model.getConstants()
            .get(2);
        int value3 = ((Number) interpreter.getValue(constant3)).intValue();
        Constant constant4 = model.getConstants()
            .get(3);
        int value4 = ((Number) interpreter.getValue(constant4)).intValue();
        Assert.assertEquals(1, value);
        Assert.assertEquals(1, value2);
        Assert.assertEquals(6, value3);
        Assert.assertEquals(6, value4);
    }

    @Test
    public void testSubtractionExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = 1 - 0;
                const int value2 = 0 - 1;
                const int value3 = (1 - 2) - 3;
                const int value4 = 1 - (2 - 3);
                if (value1+value2+value3+value4==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = ((Number) interpreter.getValue(constant)).intValue();

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Constant constant3 = model.getConstants()
            .get(2);
        int value3 = ((Number) interpreter.getValue(constant3)).intValue();
        Constant constant4 = model.getConstants()
            .get(3);
        int value4 = ((Number) interpreter.getValue(constant4)).intValue();
        Assert.assertEquals(1, value);
        Assert.assertEquals(-1, value2);
        Assert.assertEquals(-4, value3);
        Assert.assertEquals(2, value4);
    }

    @Test
    public void testInversionExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = -1;
                const int value2 = -(-1);
                if (value1+value2==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = ((Number) interpreter.getValue(constant)).intValue();

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Assert.assertEquals(-1, value);
        Assert.assertEquals(1, value2);
    }

    @Test
    public void testMultiplicationExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = 0 * 1;
                const int value2 = 2 * 1;
                const int value3 = 1 * 2;
                const int value4 = -1 * 1;
                const int value5 = 1 * -1;
                const int value6 = (1 * 2) * 3;
                const int value7 = 1 * (2 * 3);
                if (value1+value2+value3+value4+value5+value6+value7==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = ((Number) interpreter.getValue(constant)).intValue();

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Constant constant3 = model.getConstants()
            .get(2);
        int value3 = ((Number) interpreter.getValue(constant3)).intValue();
        Constant constant4 = model.getConstants()
            .get(3);
        int value4 = ((Number) interpreter.getValue(constant4)).intValue();
        Constant constant5 = model.getConstants()
            .get(4);
        int value5 = ((Number) interpreter.getValue(constant5)).intValue();
        Constant constant6 = model.getConstants()
            .get(5);
        int value6 = ((Number) interpreter.getValue(constant6)).intValue();
        Constant constant7 = model.getConstants()
            .get(6);
        int value7 = ((Number) interpreter.getValue(constant7)).intValue();
        Assert.assertEquals(0, value);
        Assert.assertEquals(2, value2);
        Assert.assertEquals(2, value3);
        Assert.assertEquals(-1, value4);
        Assert.assertEquals(-1, value5);
        Assert.assertEquals(6, value6);
        Assert.assertEquals(6, value7);
    }

    @Test
    public void testDivisionExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value1 = 2 / 2;
                const float value2 = 1 / 2;
                const float value3 = 2 / 1;
                const float value4 = -1 / 1;
                const float value5 = 1 / -1;
                const float value6 = (1 / 2) / 4;
                const float value7 = 1 / (2 / 4);
                const float value8 = 0 / 0;
                const float value9 = 1 / 0;
                const float value10 = -1 / 0;
                if (value1+value2+value3+value4+value5+value6+value7+value8+value9+value10==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        float value = ((Number) interpreter.getValue(constant)).floatValue();

        Constant constant2 = model.getConstants()
            .get(1);
        float value2 = ((Number) interpreter.getValue(constant2)).floatValue();
        Constant constant3 = model.getConstants()
            .get(2);
        float value3 = ((Number) interpreter.getValue(constant3)).floatValue();
        Constant constant4 = model.getConstants()
            .get(3);
        float value4 = ((Number) interpreter.getValue(constant4)).floatValue();
        Constant constant5 = model.getConstants()
            .get(4);
        float value5 = ((Number) interpreter.getValue(constant5)).floatValue();
        Constant constant6 = model.getConstants()
            .get(5);
        float value6 = ((Number) interpreter.getValue(constant6)).floatValue();
        Constant constant7 = model.getConstants()
            .get(6);
        float value7 = ((Number) interpreter.getValue(constant7)).floatValue();
        Constant constant8 = model.getConstants()
            .get(7);
        float value8 = ((Number) interpreter.getValue(constant8)).floatValue();
        Constant constant9 = model.getConstants()
            .get(8);
        float value9 = ((Number) interpreter.getValue(constant9)).floatValue();
        Constant constant10 = model.getConstants()
            .get(9);
        float value10 = ((Number) interpreter.getValue(constant10)).floatValue();
        Assert.assertEquals(1f, value, 0.0f);
        Assert.assertEquals(0.5f, value2, 0.0f);
        Assert.assertEquals(2f, value3, 0.0f);
        Assert.assertEquals(-1f, value4, 0.0f);
        Assert.assertEquals(-1f, value5, 0.0f);
        Assert.assertEquals(0.125f, value6, 0.0f);
        Assert.assertEquals(2f, value7, 0.0f);
        Assert.assertEquals(Float.NaN, value8, 0.0f);
        Assert.assertEquals(Float.POSITIVE_INFINITY, value9, 0.0f);
        Assert.assertEquals(Float.NEGATIVE_INFINITY, value10, 0.0f);
    }

    @Test
    public void testModuloExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 =  1 % 1;
                const int value2 = 4 % 2;
                const int value3 = 2 % 4;
                const int value4 = -1 % 2;
                const int value5 = 1 % -2;
                if (value1+value2+value3+value4+value5==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = ((Number) interpreter.getValue(constant)).intValue();

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Constant constant3 = model.getConstants()
            .get(2);
        int value3 = ((Number) interpreter.getValue(constant3)).intValue();
        Constant constant4 = model.getConstants()
            .get(3);
        int value4 = ((Number) interpreter.getValue(constant4)).intValue();
        Constant constant5 = model.getConstants()
            .get(4);
        int value5 = ((Number) interpreter.getValue(constant5)).intValue();
        Assert.assertEquals(0, value);
        Assert.assertEquals(0, value2);
        Assert.assertEquals(2, value3);
        Assert.assertEquals(-1, value4);
        Assert.assertEquals(1, value5);
    }

    @Test
    public void testDistributivity() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = 2 * (3 + 4);
                const int value2 = 2 * 3 + 2 * 4;
                if (value1+value2==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = ((Number) interpreter.getValue(constant)).intValue();

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Assert.assertEquals(14, value);
        Assert.assertEquals(value, value2);
    }

    @Test
    public void testLogicPrecedence() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const bool value1 = true || true && false;
                const bool value2 = (true || true) && false;
                if(value1||value2) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        boolean value = (boolean) interpreter.getValue(constant);

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Assert.assertTrue(value);
        Assert.assertFalse(value2);
    }

    @Test
    public void testArithmeticPrecedence() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int value1 = 1 + 1 * 2;
                const int value2 = (1 + 1) * 2;
                if (value1+value2==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        int value = ((Number) interpreter.getValue(constant)).intValue();

        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Assert.assertEquals(3, value);
        Assert.assertEquals(4, value2);
    }

    @Test
    public void testArithmeticPrecedence2() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value1 = 2 - 1 / 2;
                const float value2 = (2 - 1) / 2;
                if (value1+value2==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        float value = ((Number) interpreter.getValue(constant)).floatValue();

        Constant constant2 = model.getConstants()
            .get(1);
        float value2 = ((Number) interpreter.getValue(constant2)).floatValue();
        Assert.assertEquals(1.5f, value, 0.0f);
        Assert.assertEquals(0.5f, value2, 0.0f);
    }

    @Test
    public void testFloatValuePrecision() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value1 = 1.4e-45;
                const float value2 = 3.4028235e38;
                const float value3 = 1.17549435e-38;
                if (value1+value2+value3==0) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        float value = ((Number) interpreter.getValue(constant)).floatValue();

        Constant constant2 = model.getConstants()
            .get(1);
        float value2 = ((Number) interpreter.getValue(constant2)).floatValue();
        Constant constant3 = model.getConstants()
            .get(2);
        float value3 = ((Number) interpreter.getValue(constant3)).floatValue();
        Assert.assertEquals(Float.MIN_VALUE, value, 0.0f);
        Assert.assertEquals(Float.MAX_VALUE, value2, 0.0f);
        Assert.assertEquals(Float.MIN_NORMAL, value3, 0.0f);
    }

    @Test
    public void testComplexExpressionValue() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const float value1 = -(2 + 3 * 10 / 2) - 3;
                const bool value2 = !((value1 < 0 || value1 >= 10) && value1 == -20);
                if (value2) {}
                """;
        Smodel model = parserHelper.parse(sb);
        validationTestHelper.assertNoIssues(model);
        interpreter = new SmodelInterpreter(model, vvp, pvp);
        Constant constant = model.getConstants()
            .get(0);

        float value = ((Number) interpreter.getValue(constant)).floatValue();

        Constant constant2 = model.getConstants()
            .get(1);
        boolean value2 = (boolean) interpreter.getValue(constant2);
        Assert.assertEquals(-20f, value, 0.0f);
        Assert.assertFalse(value2);
    }
}
