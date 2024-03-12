package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.kmodel.KmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.KmodelInterpreter;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.RuntimeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks.TestProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks.TestRuntimeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks.TestVariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Runtime;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class KmodelInterpreterExpressionTest {

    private ParseHelper<Kmodel> parserHelper;
    private KmodelInterpreter interpreter;
    private VariableValueProvider vvp;
    private ProbeValueProvider pvp;
    private RuntimeValueProvider rvp;

    @Before
    public void setUp() {
        Injector injector = new KmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Kmodel>>() {
        }));

        vvp = new TestVariableValueProvider();
        pvp = new TestProbeValueProvider();
        rvp = new TestRuntimeValueProvider();
    }

    @Test
    public void testBoolExpressionValue() throws Exception {
        String sb = String.join("\n", "const bool value = true;", "const bool value2 = false;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value = 1;", "const int value2 = -1;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const float value = 1.5;", "const float value2 = .5e-1;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const string value = \"word\";");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

        Constant constant = model.getConstants()
            .get(0);
        String value = (String) interpreter.getValue(constant);

        Assert.assertEquals("word", value);
    }

    @Test
    public void testConstantExpressionValue() throws Exception {
        String sb = String.join("\n", "const int value = 1;", "const int value2 = value;",
                "const int value3 = value2;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

        Constant constant = model.getConstants()
            .get(0);
        int value = ((Number) interpreter.getValue(constant)).intValue();
        Constant constant2 = model.getConstants()
            .get(1);
        int value2 = ((Number) interpreter.getValue(constant2)).intValue();
        Constant constant3 = model.getConstants()
            .get(2);
        int value3 = ((Number) interpreter.getValue(constant3)).intValue();

        Assert.assertEquals(1, value);
        Assert.assertEquals(value, value2);
        Assert.assertEquals(value2, value3);
    }

    @Test
    public void testVariableExpressionValue() throws Exception {
        String sb = String.join("\n", "var int{2, 1} value;", "var int[0, 1, 1] value2;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

        Variable variable = model.getVariables()
            .get(0);
        int value = ((Number) interpreter.getValue(variable)).intValue();
        Variable variable2 = model.getVariables()
            .get(1);
        int value2 = ((Number) interpreter.getValue(variable2)).intValue();

        Assert.assertEquals(2, value);
        Assert.assertEquals(0, value2);
    }

    @Test
    public void testProbeExpressionValue() throws Exception {
        String sb = String.join("\n,", "probe bool value: \"someId\";");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

        Probe probe = model.getProbes()
            .get(0);
        boolean value = (boolean) interpreter.getValue(probe);

        Assert.assertTrue(value);
    }

    @Test
    public void testRuntimeExpressionValue() throws Exception {
        String sb = String.join("\n,", "runtime int value: simple: a[0].b;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

        Runtime runtime = model.getRuntimes()
            .get(0);
        int value = ((Number) interpreter.getValue(runtime)).intValue();

        Assert.assertEquals(1, value);
    }

    @Test
    public void testOrExpressionValue() throws Exception {
        String sb = String.join("\n", "const bool value = true || true;", "const bool value2 = true || false;",
                "const bool value3 = false || true;", "const bool value4 = false || false;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = true && true;", "const bool value2 = true && false;",
                "const bool value3 = false && true;", "const bool value4 = false && false;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = !false;", "const bool value2 = !true;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = \"word\" == \"word\";",
                "const bool value2 = \"word\" == \"anotherWord\";");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = 1.5 != 2.0;", "const bool value2 = 1.0 != 1.0;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = 1 < 2;", "const bool value2 = 1 < 1;",
                "const bool value3 = 2 < 1;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = 1 <= 2;", "const bool value2 = 1 <= 1;",
                "const bool value3 = 2 <= 1;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = 2 >= 1;", "const bool value2 = 1 >= 1;",
                "const bool value3 = 1 >= 2;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = 2 > 1;", "const bool value2 = 1 > 1;",
                "const bool value3 = 1 > 2;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value = 1 + 0;", "const int value2 = 0 + 1;",
                "const int value3 = (1 + 2) + 3;", "const int value4 = 1 + (2 + 3);");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value = 1 - 0;", "const int value2 = 0 - 1;",
                "const int value3 = (1 - 2) - 3;", "const int value4 = 1 - (2 - 3);");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value = -1;", "const int value2 = -(-1);");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value = 0 * 1", "const int value2 = 2 * 1;",
                "const int value3 = 1 * 2;", "const int value4 = -1 * 1;", "const int value5 = 1 * -1;",
                "const int value6 = (1 * 2) * 3;", "const int value7 = 1 * (2 * 3);");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const float value = 2 / 2;", "const float value2 = 1 / 2;",
                "const float value3 = 2 / 1;", "const float value4 = -1 / 1", "const float value5 = 1 / -1",
                "const float value6 = (1 / 2) / 4;", "const float value7 = 1 / (2 / 4);", "const float value8 = 0 / 0;",
                "const float value9 = 1 / 0;", "const float value10 = -1 / 0;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value =  1 % 1;", "const int value2 = 4 % 2;",
                "const int value3 = 2 % 4;", "const int value4 = -1 % 2;", "const int value5 = 1 % -2");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value = 2 * (3 + 4)", "const int value = 2 * 3 + 2 * 4;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const bool value = true || true && false;",
                "const bool value2 = (true || true) && false;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const int value = 1 + 1 * 2;", "const int value2 = (1 + 1) * 2;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const float value = 2 - 1 / 2;", "const float value2 = (2 - 1) / 2;");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const float value = 1.4e-45;", "const float value2 = 3.4028235e38;",
                "const float value3 = 1.17549435e-38");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
        String sb = String.join("\n", "const float value = -(2 + 3 * 10 / 2) - 3;",
                "const bool value2 = !((value < 0 || value >= 10) && value == -20);");

        Kmodel model = parserHelper.parse(sb);
        interpreter = new KmodelInterpreter(model, vvp, pvp, rvp);

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
