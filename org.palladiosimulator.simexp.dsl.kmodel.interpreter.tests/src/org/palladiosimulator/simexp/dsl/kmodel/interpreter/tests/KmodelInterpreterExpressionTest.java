package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.kmodel.KmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.KmodelInterpreter;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks.TestProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks.TestVariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class KmodelInterpreterExpressionTest {
	
	private ParseHelper<Kmodel> parserHelper;
	private KmodelInterpreter interpreter;
	private ProbeValueProvider pvp;
	private VariableValueProvider vvp;
    
    @Before
    public void setUp() {
    	Injector injector = new KmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
    	parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Kmodel>>() {}));
    	
        pvp = new TestProbeValueProvider();
        vvp = new TestVariableValueProvider();
    }
    
    @Test
    public void testBoolExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = true;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	
    	Assert.assertTrue(value);
    }
    
    @Test
    public void testIntExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const int value = 1;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	int value = (int) interpreter.getValue(constant);
    	
    	Assert.assertEquals(1, value);
    }
    
    @Test
    public void testFloatExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const float value = 1.5;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	KmodelInterpreter interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	float value = (float) interpreter.getValue(constant);
    	
    	Assert.assertEquals(1.5f, value, 0.0f);
    }
    
    @Test
    public void testStringExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const string value = \"word\";");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	String value = (String) interpreter.getValue(constant);
    	
    	Assert.assertEquals("word", value);
    }
    
    @Test
    public void testOrExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = true || true;",
    			"const bool value2 = true || false;",
    			"const bool value3 = false || true;",
    			"const bool value4 = false || false;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	Constant constant3 = (Constant) model.getFields().get(2);
    	boolean value3 = (boolean) interpreter.getValue(constant3);
    	Constant constant4 = (Constant) model.getFields().get(3);
    	boolean value4 = (boolean) interpreter.getValue(constant4);
    	
    	Assert.assertTrue(value);
    	Assert.assertTrue(value2);
    	Assert.assertTrue(value3);
    	Assert.assertFalse(value4);
    }
    
    @Test
    public void testAndExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = true && true;",
    			"const bool value2 = true && false;",
    			"const bool value3 = false && true;",
    			"const bool value4 = false && false;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	Constant constant3 = (Constant) model.getFields().get(2);
    	boolean value3 = (boolean) interpreter.getValue(constant3);
    	Constant constant4 = (Constant) model.getFields().get(3);
    	boolean value4 = (boolean) interpreter.getValue(constant4);
    	
    	Assert.assertTrue(value);
    	Assert.assertFalse(value2);
    	Assert.assertFalse(value3);
    	Assert.assertFalse(value4);
    }
    
    @Test
    public void testNotExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = !false;",
    			"const bool value2 = !true;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	
    	Assert.assertTrue(value);
    	Assert.assertFalse(value2);
    }
    
    @Test
    public void testEqualExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = \"word\" == \"word\";",
    			"const bool value2 = \"word\" == \"anotherWord\";");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	
    	Assert.assertTrue(value);
    	Assert.assertFalse(value2);
    }
    
    @Test
    public void testUnequalExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = 1.5 != 2.0;",
    			"const bool value2 = 1.0 != 1.0;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	
    	Assert.assertTrue(value);
    	Assert.assertFalse(value2);
    }
    
    @Test
    public void testSmallerExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = 1 < 2;",
    			"const bool value2 = 1 < 1;",
    			"const bool value3 = 2 < 1;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	Constant constant3 = (Constant) model.getFields().get(2);
    	boolean value3 = (boolean) interpreter.getValue(constant3);
    	
    	Assert.assertTrue(value);
    	Assert.assertFalse(value2);
    	Assert.assertFalse(value3);
    }
    
    @Test
    public void testSmallerOrEqualExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = 1 <= 2;",
    			"const bool value2 = 1 <= 1;",
    			"const bool value3 = 2 <= 1;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	Constant constant3 = (Constant) model.getFields().get(2);
    	boolean value3 = (boolean) interpreter.getValue(constant3);
    	
    	Assert.assertTrue(value);
    	Assert.assertTrue(value2);
    	Assert.assertFalse(value3);
    }
    
    @Test
    public void testGreaterOrEqualExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = 2 >= 1;",
    			"const bool value2 = 1 >= 1;",
    			"const bool value3 = 1 >= 2;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	Constant constant3 = (Constant) model.getFields().get(2);
    	boolean value3 = (boolean) interpreter.getValue(constant3);
    	
    	Assert.assertTrue(value);
    	Assert.assertTrue(value2);
    	Assert.assertFalse(value3);
    }
    
    @Test
    public void testGreaterExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const bool value = 2 > 1;",
    			"const bool value2 = 1 > 1;",
    			"const bool value3 = 1 > 2;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	boolean value = (boolean) interpreter.getValue(constant);
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	Constant constant3 = (Constant) model.getFields().get(2);
    	boolean value3 = (boolean) interpreter.getValue(constant3);
    	
    	Assert.assertTrue(value);
    	Assert.assertFalse(value2);
    	Assert.assertFalse(value3);
    }
    
    @Test
    public void testAdditionExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const int value = 1 + 1;",
    			"const int value2 = value + 2;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	int value = ((Number) interpreter.getValue(constant)).intValue();
    	Constant constant2 = (Constant) model.getFields().get(1);
    	int value2 = ((Number) interpreter.getValue(constant2)).intValue();
    	
    	Assert.assertEquals(2, value);
    	Assert.assertEquals(4, value2);
    }
    
    @Test
    public void testSubtractionExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const int value = 2 - 1;",
    			"const int value2 = value - 1;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	KmodelInterpreter interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	int value = ((Number) interpreter.getValue(constant)).intValue();
    	Constant constant2 = (Constant) model.getFields().get(1);
    	int value2 = ((Number) interpreter.getValue(constant2)).intValue();
    	
    	Assert.assertEquals(1, value);
    	Assert.assertEquals(0, value2);
    }
    
    @Test
    public void testInversionExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const int value = -1;",
    			"const int value2 = -value;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	int value = ((Number) interpreter.getValue(constant)).intValue();
    	Constant constant2 = (Constant) model.getFields().get(1);
    	int value2 = ((Number) interpreter.getValue(constant2)).intValue();
    	
    	Assert.assertEquals(-1, value);
    	Assert.assertEquals(1, value2);
    }
    
    @Test
    public void testMultiplicationExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const int value = 2 * 2;",
    			"const int value2 = value * 2;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	int value = ((Number) interpreter.getValue(constant)).intValue();
    	Constant constant2 = (Constant) model.getFields().get(1);
    	int value2 = ((Number) interpreter.getValue(constant2)).intValue();
    	
    	Assert.assertEquals(4, value);
    	Assert.assertEquals(8, value2);
    }
    
    @Test
    public void testDivisionExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const float value = 4 / 2;",
    			"const float value2 = value / 4;");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	float value = ((Number) interpreter.getValue(constant)).floatValue();
    	Constant constant2 = (Constant) model.getFields().get(1);
    	float value2 = ((Number) interpreter.getValue(constant2)).floatValue();
    	
    	Assert.assertEquals(2f, value, 0.0f);
    	Assert.assertEquals(0.5f, value2, 0.0f);
    }
    
    @Test
    public void testComplexExpressionValue() throws Exception {
    	String sb = String.join("\n", 
    			"const float value = -(2 + 3 * 10 / 2) - 3;",
    			"const bool value2 = !((value < 0 || value >= 10) && value == -20);");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Constant constant = (Constant) model.getFields().get(0);
    	float value = ((Number) interpreter.getValue(constant)).floatValue();
    	Constant constant2 = (Constant) model.getFields().get(1);
    	boolean value2 = (boolean) interpreter.getValue(constant2);
    	
    	Assert.assertEquals(-20f, value, 0.0f);
    	Assert.assertFalse(value2);
    }
}
