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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Operation;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelExpressionParsingJavaTest {
	@Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseSimpleLiteralExpression() throws Exception {
        String sb = String.join("\n", 
                "const int constant = 1;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        
        Expression literal = expression.getLiteral();
        Assert.assertTrue(literal instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) literal).getValue());
    }
    
    @Test
    public void parseSimpleFieldExpression() throws Exception {
        String sb = String.join("\n",
        		"const int a = 1;",
                "const int b = a;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant firstConstant = (Constant) fields.get(0);
        Expression firstExpression = KmodelTestUtil.getNextExpressionWithContent(firstConstant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(firstExpression));
        
        Expression firstValue = firstExpression.getLiteral();
        Assert.assertTrue(firstValue instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) firstValue).getValue());
        
        Constant secondConstant = (Constant) fields.get(1);
        Expression secondExpression = KmodelTestUtil.getNextExpressionWithContent(secondConstant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(secondExpression));
        
        Field secondValue = secondExpression.getFieldRef();
        Assert.assertEquals(firstConstant, secondValue);
    }
    
    @Test
    public void parseSimpleExpressionWithBrackets() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = (1);"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        Assert.assertNull(expression.getLiteral());
        Assert.assertNotNull(expression.getExpr());
        
        Expression inner = KmodelTestUtil.getNextExpressionWithContent(expression.getExpr());
        Expression literal = inner.getLiteral();
        Assert.assertTrue(literal instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) literal).getValue());
    }
    
    @Test
    public void parseDisjunctionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = true || false;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.OR, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) leftLiteral).isValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Expression rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof BoolLiteral);
        Assert.assertEquals(false, ((BoolLiteral) rightLiteral).isValue());
    }
    
    @Test
    public void parseConjunctionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = true && false;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.AND, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) leftLiteral).isValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Expression rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof BoolLiteral);
        Assert.assertEquals(false, ((BoolLiteral) rightLiteral).isValue());
    }
    
    @Test
    public void parseEqualityExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = \"some\" == \"thing\";"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.EQUAL, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof StringLiteral);
        Assert.assertEquals("some", ((StringLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Expression rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof StringLiteral);
        Assert.assertEquals("thing", ((StringLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseNegationExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = !true;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.NOT, expression.getOp());
        Assert.assertNull(expression.getRight());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) leftLiteral).isValue());
    }
    
    @Test
    public void parseComparisonExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = 1 <= 2;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.SMALLER_OR_EQUAL, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Expression rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseAdditionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = 1 + 2;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.PLUS, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Expression rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseAdditiveInversionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = -1;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.MINUS, expression.getOp());
        Assert.assertNull(expression.getRight());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
    }
    
    @Test
    public void parseMultiplicationExpression() throws Exception {
    	String sb = String.join("\n", 
                "const float constant = 1.0 * 2;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.FLOAT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.MULTIPLY, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Expression leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof FloatLiteral);
        Assert.assertEquals(1, ((FloatLiteral) leftLiteral).getValue(), 0.0f);
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Expression rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseComplexExpression() throws Exception {
    	String sb = String.join("\n",
    			"const int a = 1;",
                "const bool constant = -a > 1 == !(true || false && (a * (1 + a)) != 2.0);"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
    
    @Test
    public void parseDisjunctionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = true || 1.0;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Expected a value of type 'bool'. Got 'float' instead.");
    }
    
    @Test
    public void parseConjunctionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = \"true\" && false;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'bool'. Got 'string' instead.");
    }
    
    @Test
    public void parseEqualityWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = \"1.0\" == 1.0;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, 
        		"Cannot compare the equality of a 'string' value with a 'float' value.",
        		"Cannot compare the equality of a 'string' value with a 'float' value.");
    }
    
    @Test
    public void parseNegationWithInvalidType() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = !1;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot negate a 'int' value.");
    }
    
    @Test
    public void parseComparisonWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = 1 >= false;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot compare a 'int' value with a 'bool' value.");
    }
    
    @Test
    public void parseAdditionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const int constant = true + 1;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot add or subtract a 'bool' value with a 'int' value.");
    }
    
    @Test
    public void parseAdditiveInverseWithInvalidType() throws Exception {
        String sb = String.join("\n", 
                "const int constant = -false;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Cannot invert a 'bool' value.");
    }
    
    @Test
    public void parseMultiplicationWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const float constant = 1.5 * \"2\";"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot multiply or divide a 'float' value with a 'string' value.");
    }
    
    @Test
    public void parseExpressionWithWrongBrackets() throws Exception {
        String sb = String.join("\n", 
                "const string word = ((\"word\");"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input '('");
    }
}
