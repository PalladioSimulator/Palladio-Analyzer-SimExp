package org.palladiosimulator.simexp.dsl.kmodel.tests;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Array;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Growth;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Range;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.RangeWithGrowth;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ValueContainer;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelVariableParsingJavaTest {
    @Inject private ParseHelper<Kmodel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolVariable() throws Exception {
        String sb = String.join("\n", 
                "var bool condition = {true};"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field variable = variables.get(0);
        Assert.assertTrue(variable instanceof Variable);
        Assert.assertEquals("condition", variable.getName());
        Assert.assertEquals(DataType.BOOL, variable.getDataType());
    }
    
    @Test
    public void parseSingleIntVariable() throws Exception {
        String sb = String.join("\n", 
                "var int count = {1};"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field variable = variables.get(0);
        Assert.assertTrue(variable instanceof Variable);
        Assert.assertEquals("count", variable.getName());
        Assert.assertEquals(DataType.INT, variable.getDataType());
    }
    
    @Test
    public void parseSingleFloatVariable() throws Exception {
        String sb = String.join("\n", 
                "var float number = {1.0};"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field variable = variables.get(0);
        Assert.assertTrue(variable instanceof Variable);
        Assert.assertEquals("number", variable.getName());
        Assert.assertEquals(DataType.FLOAT, variable.getDataType());
    }
    
    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = String.join("\n", 
                "var string word = {\"word\"};"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field variable = variables.get(0);
        Assert.assertTrue(variable instanceof Variable);
        Assert.assertEquals("word", variable.getName());
        Assert.assertEquals(DataType.STRING, variable.getDataType());
    }
    
    @Test
    public void parseTwoVariables() throws Exception {
        String sb = String.join("\n", 
                "var int count = {1};",
                "var string word = {\"word\"};"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(2, variables.size());
        
        Field firstVariable = variables.get(0);
        Assert.assertTrue(firstVariable instanceof Variable);
        Assert.assertEquals("count", firstVariable.getName());
        Assert.assertEquals(DataType.INT, firstVariable.getDataType());
        
        Field secondVariable = variables.get(1);
        Assert.assertTrue(secondVariable instanceof Variable);
        Assert.assertEquals("word", secondVariable.getName());
        Assert.assertEquals(DataType.STRING, secondVariable.getDataType());
    }
    
    @Test
    public void parseVariableWithValueArray() throws Exception {
    	String sb = String.join("\n", 
                "var int count = {1, 2, 3};"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field field = variables.get(0);
        Assert.assertTrue(field instanceof Variable);
        
        Variable variable = (Variable) field;
        ValueContainer valueCollection = variable.getValues();
        Assert.assertTrue(valueCollection instanceof Array);
        
        Array valueArray = (Array) valueCollection;
        List<Expression> values = valueArray.getValues();
        Assert.assertEquals(3, values.size());
        
        int firstValue = ((IntLiteral) KmodelTestUtil.getNextExpressionWithContent(values.get(0)).getLiteral()).getValue();
        Assert.assertEquals(1, firstValue);
        
        int secondValue = ((IntLiteral) KmodelTestUtil.getNextExpressionWithContent(values.get(1)).getLiteral()).getValue();
        Assert.assertEquals(2, secondValue);
        
        int thirdValue = ((IntLiteral) KmodelTestUtil.getNextExpressionWithContent(values.get(2)).getLiteral()).getValue();
        Assert.assertEquals(3, thirdValue);
    }
    
    @Test
    public void parseVariableWithValueRange() throws Exception {
    	String sb = String.join("\n", 
                "var float values = (1.0, 2.0, 0.1);"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field field = variables.get(0);
        Assert.assertTrue(field instanceof Variable);
        
        Variable variable = (Variable) field;
        ValueContainer valueCollection = variable.getValues();
        Assert.assertTrue(valueCollection instanceof Range);
        
        Range valueRange = (Range) valueCollection;
        float startValue = ((FloatLiteral) KmodelTestUtil.getNextExpressionWithContent(valueRange.getStartValue()).getLiteral()).getValue();
        Assert.assertEquals(1, startValue, 0.0f);
        
        float endValue = ((FloatLiteral) KmodelTestUtil.getNextExpressionWithContent(valueRange.getEndValue()).getLiteral()).getValue();
        Assert.assertEquals(2, endValue, 0.0f);
        
        float stepSize = ((FloatLiteral) KmodelTestUtil.getNextExpressionWithContent(valueRange.getStepSize()).getLiteral()).getValue();
        Assert.assertEquals(0.1f, stepSize, 0.0f);
    }
    
    @Test
    public void parseVariableWithValueRangeWithGrowth() throws Exception {
    	String sb = String.join("\n", 
                "var float values =[1.0, 2.0, 10, EXPONENTIAL];"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field field = variables.get(0);
        Assert.assertTrue(field instanceof Variable);
        
        Variable variable = (Variable) field;
        ValueContainer valueCollection = variable.getValues();
        Assert.assertTrue(valueCollection instanceof RangeWithGrowth);
        
        RangeWithGrowth valueRange = (RangeWithGrowth) valueCollection;
        float startValue = ((FloatLiteral) KmodelTestUtil.getNextExpressionWithContent(valueRange.getStartValue()).getLiteral()).getValue();
        Assert.assertEquals(1, startValue, 0.0f);
        
        float endValue = ((FloatLiteral) KmodelTestUtil.getNextExpressionWithContent(valueRange.getEndValue()).getLiteral()).getValue();
        Assert.assertEquals(2, endValue, 0.0f);
        
        int numSteps = ((IntLiteral) KmodelTestUtil.getNextExpressionWithContent(valueRange.getNumSteps()).getLiteral()).getValue();
        Assert.assertEquals(10, numSteps);
        
        Growth growth = valueRange.getGrowth();
        Assert.assertEquals(Growth.EXPONENTIAL, growth);
    }
    
    @Test
    public void parseVariableWithWrongValueTypes() throws Exception {
    	String sb = String.join("\n", 
                "var string list = {true, 1, 2.5};"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);

        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected only values of type 'string', got {bool, int, float} instead.");
    }
    
    @Test
    public void parseNonNumberVariableWithRange() throws Exception {
    	String sb = String.join("\n", 
                "var bool range = (true, false, true);"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);

        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 4, 
        		"Cannot assign a value range to a variable of the type 'bool'.", 
        		"Expected a value of type 'int' or 'float', got 'bool' instead.", 
        		"Expected a value of type 'int' or 'float', got 'bool' instead.", 
        		"Expected a value of type 'int' or 'float', got 'bool' instead.");
    }
}

