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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Operation;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelExpressionParsingJavaTest {
	@Inject private ParseHelper<Kmodel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseSimpleLiteralExpression() throws Exception {
        String sb = String.join("\n", 
                "const int constant = 1;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        
        Literal literal = expression.getLiteral();
        Assert.assertTrue(literal instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) literal).getValue());
    }
    
    @Test
    public void parseSimpleFieldExpression() throws Exception {
        String sb = String.join("\n",
        		"const int a = 1;",
                "const int b = a;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant firstConstant = (Constant) fields.get(0);
        Expression firstExpression = KmodelTestUtil.getNextExpressionWithContent(firstConstant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(firstExpression));
        
        Literal firstValue = firstExpression.getLiteral();
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
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        
        Literal literal = expression.getLiteral();
        Assert.assertTrue(literal instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) literal).getValue());
    }
    
    @Test
    public void parseOrExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = true || false;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.OR, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) leftLiteral).isTrue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof BoolLiteral);
        Assert.assertEquals(false, ((BoolLiteral) rightLiteral).isTrue());
    }
    
    @Test
    public void parseAndExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = true && false;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.AND, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) leftLiteral).isTrue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof BoolLiteral);
        Assert.assertEquals(false, ((BoolLiteral) rightLiteral).isTrue());
    }
    
    @Test
    public void parseEqualExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = \"some\" == \"thing\";"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.EQUAL, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof StringLiteral);
        Assert.assertEquals("some", ((StringLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof StringLiteral);
        Assert.assertEquals("thing", ((StringLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseUnequalExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = \"some\" != \"thing\";"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.UNEQUAL, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof StringLiteral);
        Assert.assertEquals("some", ((StringLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof StringLiteral);
        Assert.assertEquals("thing", ((StringLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseNotExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = !true;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.NOT, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) leftLiteral).isTrue());
    }
    
    @Test
    public void parseSmallerExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = 1 < 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.SMALLER, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseSmallerOrEqualExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = 1 <= 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.SMALLER_OR_EQUAL, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseGreaterOrEqualExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = 1 >= 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.GREATER_OR_EQUAL, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseGreaterExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = 1 > 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.BOOL, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.GREATER, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseAdditionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = 1 + 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.PLUS, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseSubtractionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = 1 - 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.MINUS, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseAdditiveInverseExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = -1;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.MINUS, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
    }
    
    @Test
    public void parseMultiplicationExpression() throws Exception {
    	String sb = String.join("\n", 
                "const float constant = 1.0 * 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.FLOAT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.MULTIPLY, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof FloatLiteral);
        Assert.assertEquals(1, ((FloatLiteral) leftLiteral).getValue(), 0.0f);
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseDivisionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const float constant = 1 / 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.FLOAT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.DIVIDE, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue());
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseModuloExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = 1 % 2;"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        
        Constant constant = (Constant) fields.get(0);
        Expression expression = KmodelTestUtil.getNextExpressionWithContent(constant.getValue());
        Assert.assertEquals(DataType.INT, KmodelTestUtil.getDataType(expression));
        Assert.assertEquals(Operation.MODULO, expression.getOp());
        
        Expression left = KmodelTestUtil.getNextExpressionWithContent(expression.getLeft());
        Literal leftLiteral = left.getLiteral();
        Assert.assertTrue(leftLiteral instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) leftLiteral).getValue(), 0.0f);
        
        Expression right = KmodelTestUtil.getNextExpressionWithContent(expression.getRight());
        Literal rightLiteral = right.getLiteral();
        Assert.assertTrue(rightLiteral instanceof IntLiteral);
        Assert.assertEquals(2, ((IntLiteral) rightLiteral).getValue());
    }
    
    @Test
    public void parseComplexExpression() throws Exception {
    	String sb = String.join("\n",
    			"const int a = 1;",
                "const bool constant = -a > 1 == !(true || false && (a * (1 + a)) != 2.0);"
        );
    	
    	Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
    
    @Test
    public void parseDisjunctionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = true || 1.0;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Expected a value of type 'bool', got 'float' instead.");
    }
    
    @Test
    public void parseConjunctionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = \"true\" && false;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'bool', got 'string' instead.");
    }
    
    @Test
    public void parseEqualityWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = \"1.0\" == 1.0;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'int' or 'float', got 'string' instead.");
    }
    
    @Test
    public void parseNegationWithInvalidType() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = !1;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'bool', got 'int' instead.");
    }
    
    @Test
    public void parseComparisonWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = 1 >= false;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'int' or 'float', got 'bool' instead.");
    }
    
    @Test
    public void parseAdditionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const int constant = true + 1;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'int' or 'float', got 'bool' instead.");
    }
    
    @Test
    public void parseAdditiveInverseWithInvalidType() throws Exception {
        String sb = String.join("\n", 
                "const int constant = -false;"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Expected a value of type 'int' or 'float', got 'bool' instead.");
    }
    
    @Test
    public void parseMultiplicationWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const float constant = 1.5 * \"2\";"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'int' or 'float', got 'string' instead.");
    }
    
    @Test
    public void parseExpressionWithWrongBrackets() throws Exception {
        String sb = String.join("\n", 
                "const string word = ((\"word\");"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertErrorMessages(model, 1, "missing ')' at ';'");
    }
}