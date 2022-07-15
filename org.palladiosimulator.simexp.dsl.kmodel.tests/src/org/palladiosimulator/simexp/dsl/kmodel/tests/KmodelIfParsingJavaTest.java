package org.palladiosimulator.simexp.dsl.kmodel.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelIfParsingJavaTest {
	@Inject private ParseHelper<Kmodel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseIfStatementWithLiteralCondition() throws Exception {
        String sb = String.join("\n", 
                "if (true) {}"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement statement = statements.get(0);
        Expression condition = KmodelTestUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) condition.getLiteral()).isValue());
        
        EList<Statement> thenStatements = statements.get(0).getStatements();
        Assert.assertTrue(thenStatements.isEmpty());
    }
    
    @Test
    public void parseIfStatementWithFieldCondition() throws Exception {
        String sb = String.join("\n",
        		"const bool condition = true;",
                "if (condition) {}"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        assertEquals(1, variables.size());
        
        Field boolConditionVar = variables.get(0);
        Assert.assertEquals("condition", boolConditionVar.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionVar.getDataType());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement statement = statements.get(0);
        Expression condition = KmodelTestUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertEquals(boolConditionVar, condition.getFieldRef());
        Assert.assertEquals(DataType.BOOL, condition.getFieldRef().getDataType());
        Assert.assertEquals("condition", condition.getFieldRef().getName());
        
        EList<Statement> thenStatements = statements.get(0).getStatements();
        Assert.assertTrue(thenStatements.isEmpty());
    }
    
    @Test
    public void parseConsecutiveIfStatements() throws Exception {
        String sb = String.join("\n",
        		"if (true) {}",
                "if (true) {}"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(2, statements.size());
        
        Statement firstStatement = statements.get(0);
        Expression firstCondition = KmodelTestUtil.getNextExpressionWithContent(firstStatement.getCondition());
        Assert.assertTrue(firstCondition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) firstCondition.getLiteral()).isValue());
        
        EList<Statement> firstThenStatements = statements.get(0).getStatements();
        Assert.assertTrue(firstThenStatements.isEmpty());
        
        Statement secondStatement = statements.get(1);
        Expression secondCondition = KmodelTestUtil.getNextExpressionWithContent(secondStatement.getCondition());
        Assert.assertTrue(secondCondition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) secondCondition.getLiteral()).isValue());
        
        EList<Statement> secondThenStatements = statements.get(1).getStatements();
        Assert.assertTrue(secondThenStatements.isEmpty());
        
    }
    
    @Test
    public void parseNestedIfStatements() throws Exception {
        String sb = String.join("\n",
        		"const bool condition = true;",
        		"if (condition) {",
        		"if (condition) {}",
        		"}"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        assertEquals(1, fields.size());
        
        Field boolConditionField = fields.get(0);
        Assert.assertEquals("condition", boolConditionField.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionField.getDataType());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement outerStmt = statements.get(0);
        Expression outerCondition = KmodelTestUtil.getNextExpressionWithContent(outerStmt.getCondition());
        Field outerIfConditionField = outerCondition.getFieldRef();
        Assert.assertEquals(boolConditionField, outerIfConditionField);
        
        EList<Statement> outerThenStatements = outerStmt.getStatements();
        Assert.assertEquals(1, outerThenStatements.size());
        
        Statement innerStmt = outerThenStatements.get(0);
        Expression innerCondition = KmodelTestUtil.getNextExpressionWithContent(innerStmt.getCondition());
        Field innerIfConditionField = innerCondition.getFieldRef();
        Assert.assertEquals(boolConditionField, innerIfConditionField);
        
        EList<Statement> innerThenStatements = innerStmt.getStatements();
        Assert.assertTrue(innerThenStatements.isEmpty());
    }
    
    @Test
    public void parseIfStatementWithActionCall() throws Exception {
    	String sb = String.join("\n",
    			"action scaleOut(param float balancingFactor);",
    			"if (false) {",
    			"scaleOut(1.0);",
    			"}"
    	);
    	
    	Kmodel model = parserHelper.parse(sb);
    	KmodelTestUtil.assertModelWithoutErrors(model);
    	KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement statement = statements.get(0);
        Expression condition = KmodelTestUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(false, ((BoolLiteral) condition.getLiteral()).isValue());
        
        EList<Statement> thenStatements = statements.get(0).getStatements();
        Assert.assertEquals(1, thenStatements.size());
    }
    
    @Test
    public void parseIfStatementWithWrongLiteralType() throws Exception {
        String sb = String.join("\n", 
                "if (\"condition\") {}"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Expected a value of type 'bool', got 'string' instead.");
    }
    
    @Test
    public void parseIfStatementWithWrongFieldType() throws Exception {
        String sb = String.join("\n",
        		"const string condition = \"true\";",
                "if (condition) {}"
        );
        
        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"Expected a value of type 'bool', got 'string' instead.");
    }
    
    
    @Test
    public void parseComplexStatement() throws Exception {
        String sb = String.join("\n"
                ,"//Variable declarations"
                ,"var float i = {1.0, 2.0};"
                ,"// action declaration"
                , "action a(param float factor);"
                , "action anotherA(param float factor, var float anotherFactor=(1,2,1));"
                
                ,"// rule block"
                ,"if (true) {"
                ,"    a(i);       // execute action"
                ,    "anotherA(i); // execute another action"
                ,"}"
        );
        
        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
    
    
}
