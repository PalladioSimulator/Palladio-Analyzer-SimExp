package org.palladiosimulator.simexp.dsl.kmodel.tests;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelConstantParsingJavaTest {
    @Inject private ParseHelper<Kmodel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolConstant() throws Exception {
        String sb = String.join("\n", 
                "const bool condition = true;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Constant);
        
        Constant constant = (Constant) field;
        Assert.assertEquals("condition", constant.getName());
        Assert.assertEquals(DataType.BOOL, constant.getDataType());
        
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue()).getLiteral();
        Assert.assertTrue(value instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) value).isTrue());
    }
    
    @Test
    public void parseSingleIntConstant() throws Exception {
        String sb = String.join("\n", 
                "const int one = 1;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Constant);
        
        Constant constant = (Constant) field;
        Assert.assertEquals("one", constant.getName());
        Assert.assertEquals(DataType.INT, constant.getDataType());
        
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue()).getLiteral();
        Assert.assertTrue(value instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) value).getValue());
    }
    
    @Test
    public void parseSingleFloatConstant() throws Exception {
        String sb = String.join("\n", 
                "const float one = 1.0;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Constant);
        
        Constant constant = (Constant) field;
        Assert.assertEquals("one", constant.getName());
        Assert.assertEquals(DataType.FLOAT, constant.getDataType());
        
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue()).getLiteral();
        Assert.assertTrue(value instanceof FloatLiteral);
        Assert.assertEquals(1, ((FloatLiteral) value).getValue(), 0.0f);
    }
    
    @Test
    public void parseSingleStringConstant() throws Exception {
        String sb = String.join("\n", 
                "const string word = \"word\";"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Constant);
        
        Constant constant = (Constant) field;
        Assert.assertEquals("word", constant.getName());
        Assert.assertEquals(DataType.STRING, constant.getDataType());
        
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue()).getLiteral();
        Assert.assertTrue(value instanceof StringLiteral);
        Assert.assertEquals("word", ((StringLiteral) value).getValue());
    }
    
    @Test
    public void parseTwoConstants() throws Exception {
        String sb = String.join("\n", 
                "const int count = 1;",
                "const string word = \"word\";"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(2, fields.size());
        
        Field firstField = fields.get(0);
        Assert.assertTrue(firstField instanceof Constant);
        
        Constant firstConstant = (Constant) firstField;
        Assert.assertEquals("count", firstConstant.getName());
        Assert.assertEquals(DataType.INT, firstConstant.getDataType());

        Literal firstValue = KmodelTestUtil.getNextExpressionWithContent(firstConstant.getValue()).getLiteral();
        Assert.assertTrue(firstValue instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) firstValue).getValue());
        
        Field secondField = fields.get(1);
        Assert.assertTrue(secondField instanceof Constant);
        
        Constant secondConstant = (Constant) secondField;
        Assert.assertEquals("word", secondConstant.getName());
        Assert.assertEquals(DataType.STRING, secondConstant.getDataType());
        
        Literal secondValue = KmodelTestUtil.getNextExpressionWithContent(secondConstant.getValue()).getLiteral();
        Assert.assertTrue(secondValue instanceof StringLiteral);
        Assert.assertEquals("word", ((StringLiteral) secondValue).getValue());
    }
    
    @Test
    public void parseConstantWithConstantValue() throws Exception {
    	String sb = String.join("\n", 
                "const int const1 = 1;",
                "const int const2 = const1;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(2, fields.size());
        
        Field firstField = fields.get(0);
        Assert.assertTrue(firstField instanceof Constant);
        
        Constant firstConstant = (Constant) firstField;
        Assert.assertEquals("const1", firstConstant.getName());
        Assert.assertEquals(DataType.INT, firstConstant.getDataType());

        Literal firstValue = KmodelTestUtil.getNextExpressionWithContent(firstConstant.getValue()).getLiteral();
        Assert.assertTrue(firstValue instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) firstValue).getValue());
        
        Field secondField = fields.get(1);
        Assert.assertTrue(secondField instanceof Constant);
        
        Constant secondConstant = (Constant) secondField;
        Assert.assertEquals("const2", secondConstant.getName());
        Assert.assertEquals(DataType.INT, secondConstant.getDataType());
        
        Field fieldReference = KmodelTestUtil.getNextExpressionWithContent(secondConstant.getValue()).getFieldRef();
        Literal secondValue = KmodelTestUtil.getNextExpressionWithContent(((Constant) fieldReference).getValue()).getLiteral();
        Assert.assertEquals(firstConstant, fieldReference);
        Assert.assertEquals(((IntLiteral) firstValue).getValue(), ((IntLiteral) secondValue).getValue());
    }
    
    @Test
    public void parseConstantWithoutValue() throws Exception {
    	String sb = String.join("\n", 
                "const int noValue;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
    	
        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }
    
    @Test
    public void parseConstantWithVariableValue() throws Exception {
    	String sb = String.join("\n",
    			"var int{0} variable;",
                "const int constant = variable;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Cannot assign an expression containing a variable or probe value to a constant.");
    }
    
    @Test
    public void parseConstantWithProbeValue() throws Exception {
    	String sb = String.join("\n",
    			"probe int someProbe : \"someId\";",
                "const int constant = someProbe;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Cannot assign an expression containing a variable or probe value to a constant.");
    }
    
    @Test
    public void parseConstantWithExpressionContainingVariable() throws Exception {
    	String sb = String.join("\n",
    			"var bool{true} variable;",
                "const bool constant = variable && true;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Cannot assign an expression containing a variable or probe value to a constant.");
    }
    
    @Test
    public void parseConstantWithSelfReference() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = false || constant;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Couldn't resolve reference to Field 'constant'.");
    }
    
    @Test
    public void parseConstantsWithCyclicReference() throws Exception {
    	String sb = String.join("\n", 
                "const int const1 = const2;",
                "const int const2 = const1;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Couldn't resolve reference to Field 'const2'.");
    }
    
    @Test
    public void parseConstantWithWrongValueType() throws Exception {
    	String sb = String.join("\n", 
                "const int number = true;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Expected a value of type 'int', got 'bool' instead.");
    }
}
