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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Bounds;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Range;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelVariableParsingJavaTest {
    @Inject private ParseHelper<Kmodel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolVariable() throws Exception {
        String sb = String.join("\n", 
                "var bool{true} condition;"
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
                "var int{1} count;"
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
                "var float{1.0} number;"
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
                "var string{\"word\"} word;"
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
                "var int{1} count;",
                "var string{\"word\"} word;"
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
                "var int{1, 2, 3} count;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field field = variables.get(0);
        Assert.assertTrue(field instanceof Variable);
        
        Variable variable = (Variable) field;
        Bounds bounds = variable.getValues();
        Assert.assertTrue(bounds instanceof Array);
        
        Array valueArray = (Array) bounds;
        List<Literal> values = valueArray.getValues();
        Assert.assertEquals(3, values.size());
        
        int firstValue = ((IntLiteral) values.get(0)).getValue();
        Assert.assertEquals(1, firstValue);
        
        int secondValue = ((IntLiteral) values.get(1)).getValue();
        Assert.assertEquals(2, secondValue);
        
        int thirdValue = ((IntLiteral) values.get(2)).getValue();
        Assert.assertEquals(3, thirdValue);
    }
    
    @Test
    public void parseVariableWithValueRange() throws Exception {
    	String sb = String.join("\n", 
                "var float[1.0, 2.0, 0.1] values;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        Assert.assertEquals(1, variables.size());
        
        Field field = variables.get(0);
        Assert.assertTrue(field instanceof Variable);
        
        Variable variable = (Variable) field;
        Bounds bounds = variable.getValues();
        Assert.assertTrue(bounds instanceof Range);
        
        Range valueRange = (Range) bounds;
        float startValue = ((FloatLiteral) valueRange.getStartValue()).getValue();
        Assert.assertEquals(1, startValue, 0.0f);
        
        float endValue = ((FloatLiteral) valueRange.getEndValue()).getValue();
        Assert.assertEquals(2, endValue, 0.0f);
        
        float stepSize = ((FloatLiteral) valueRange.getStepSize()).getValue();
        Assert.assertEquals(0.1f, stepSize, 0.0f);
    }
    
    @Test
    public void parseVariableWithWrongValueTypes() throws Exception {
    	String sb = String.join("\n", 
                "var string{true, 1, 2.5} list;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);

        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 3, 
        		"Expected a value of type 'string', got 'bool' instead.",
        		"Expected a value of type 'string', got 'int' instead.",
        		"Expected a value of type 'string', got 'float' instead.");
    }
    
    @Test
    public void parseNonNumberVariableWithRange() throws Exception {
    	String sb = String.join("\n", 
                "var bool[true, false, true] range;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);

        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot assign a range to a variable of the type 'bool'.");
    }
}
