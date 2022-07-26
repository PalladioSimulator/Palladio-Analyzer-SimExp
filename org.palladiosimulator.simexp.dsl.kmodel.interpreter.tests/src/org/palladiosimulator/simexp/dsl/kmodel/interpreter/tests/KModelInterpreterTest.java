package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import static org.junit.Assert.assertFalse;

import java.util.List;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IfStatement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KmodelFactory;

public class KModelInterpreterTest {
    
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
    public void testSimpleModel() throws Exception {
        
        /**
         * sample model:
         * if (true) {
         * 
         * }
         * */
        
        KmodelFactory kmodelFactory = KmodelFactory.eINSTANCE;
        Kmodel kmodel = kmodelFactory.createKmodel();
        Constant constant = kmodelFactory.createConstant();
        constant.setName("simpleCondition");
        constant.setDataType(DataType.BOOL);
        kmodel.getFields().add(constant);
        IfStatement rule = kmodelFactory.createIfStatement();
        Expression condExpr = kmodelFactory.createExpression();
        BoolLiteral boolLiteral = kmodelFactory.createBoolLiteral();
        condExpr.setLiteral(boolLiteral);
        rule.setCondition(condExpr);
        interpreter = new KmodelInterpreter(kmodel, pvp, vvp);
        
        boolean actual = interpreter.analyze();
        
        assertFalse(actual);
    }
    
    @Test
    public void testAnalyzeWithTrueCondition() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"if (true) {",
    			"adapt(parameter=1);",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Assert.assertTrue(interpreter.analyze());
    }
    
    @Test
    public void testAnalyzeWithSecondTrueCondition() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"if (false) {",
    			"adapt(parameter=1);",
    			"}",
    			"if (true) {",
    			"adapt(parameter=1);",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Assert.assertTrue(interpreter.analyze());
    }
    
    @Test
    public void testAnalyzeWithNoTrueCondition() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"if (false) {",
    			"adapt(parameter=1);",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Assert.assertFalse(interpreter.analyze());
    }
    
    @Test
    public void testAnalyzeWithoutCondition() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Assert.assertFalse(interpreter.analyze());
    }
    
    @Test
    public void testPlanWithOneActionCall() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"if (true) {",
    			"adapt(parameter=1);",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Action adapt = model.getActions().get(0);
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(1, actionsToExecute.size());
    	Assert.assertEquals(adapt, actionsToExecute.get(0));
    }
    
    @Test
    public void testPlanWithTwoActionCalls() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"action adapt2(param int parameter);",
    			"if (true) {",
    			"adapt(parameter=1);",
    			"adapt2(parameter=1);",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Action adapt = model.getActions().get(0);
    	Action adapt2 = model.getActions().get(1);
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(2, actionsToExecute.size());
    	Assert.assertEquals(adapt, actionsToExecute.get(0));
    	Assert.assertEquals(adapt2, actionsToExecute.get(1));
    }
    
    @Test
    public void testPlanWithTwoActionCallsOfSameAction() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"if (true) {",
    			"adapt(parameter=1);",
    			"adapt(parameter=2);",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Action adapt = model.getActions().get(0);
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(2, actionsToExecute.size());
    	Assert.assertEquals(adapt, actionsToExecute.get(0));
    	Assert.assertEquals(adapt, actionsToExecute.get(1));
    }
    
    @Test
    public void testPlanWithNestedActionCalls() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"action adapt2(param int parameter);",
    			"if (true) {",
    			"adapt(parameter=1);",
    			"if (true) {",
    			"adapt2(parameter=1);",
    			"}",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Action adapt = model.getActions().get(0);
    	Action adapt2 = model.getActions().get(1);
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(2, actionsToExecute.size());
    	Assert.assertEquals(adapt, actionsToExecute.get(0));
    	Assert.assertEquals(adapt2, actionsToExecute.get(1));
    }
    
    @Test
    public void testPlanWithFalseOuterCondition() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"action adapt2(param int parameter);",
    			"if (false) {",
    			"adapt(parameter=1);",
    			"if (true) {",
    			"adapt2(parameter=1);",
    			"}",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(0, actionsToExecute.size());
    }
    
    @Test
    public void testPlanWithFalseInnerCondition() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);",
    			"action adapt2(param int parameter);",
    			"if (true) {",
    			"adapt(parameter=1);",
    			"if (false) {",
    			"adapt2(parameter=1);",
    			"}",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Action adapt = model.getActions().get(0);
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(1, actionsToExecute.size());
    	Assert.assertEquals(adapt, actionsToExecute.get(0));
    }
    
    @Test
    public void testPlanWithoutActionCalls() throws Exception {
    	String sb = String.join("\n",
    			"action adapt(param int parameter);");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(0, actionsToExecute.size());
    }
    
    @Test
    public void testComplexAnalyzeAndPlan() throws Exception {
    	String sb = String.join("\n", 
    			"const int constant = 1;",
    			"const int anotherConstant = constant * 2;",
    			"var float{1.0, 2.0, 3.0} adaptationFactor;",
    			"probe float someProbe : abc123;",
    			"action adapt(param int parameter, param float factor);",
    			"action adapt2(param int parameter, var float[1,5,1] someRange);",
    			"action adapt3();",
    			"if (someProbe > 0 || someProbe <= -10) {",
    			"adapt(parameter=anotherConstant, factor=2.0);",
    			"if (someProbe > 0 && someProbe < 10) {",
    			"adapt2(parameter=adaptationFactor);",
    			"}",
    			"}",
    			"if (someProbe == 100) {",
    			"adapt3();",
    			"}");
    	
    	Kmodel model = parserHelper.parse(sb);
    	interpreter = new KmodelInterpreter(model, pvp, vvp);
    	
    	Assert.assertTrue(interpreter.analyze());
    	
    	Action adapt = model.getActions().get(0);
    	Action adapt2 = model.getActions().get(1);
    	List<Action> actionsToExecute = interpreter.plan();
    	
    	Assert.assertEquals(2, actionsToExecute.size());
    	Assert.assertEquals(adapt, actionsToExecute.get(0));
    	Assert.assertEquals(adapt2, actionsToExecute.get(1));
    }
}
