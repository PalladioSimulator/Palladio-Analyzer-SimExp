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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelIfParsingJavaTest {
	@Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseIfStatementWithLiteralCondition() throws Exception {
        String sb = String.join("\n", 
                "if (true) {}"
        );
        
        KModel model = parserHelper.parse(sb);
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
    public void parseIfStatementWithVariableCondition() throws Exception {
        String sb = String.join("\n",
        		"var bool condition;",
                "if (condition) {}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variables = model.getFields();
        assertEquals(1, variables.size());
        
        Field boolConditionVar = variables.get(0);
        Assert.assertTrue(boolConditionVar instanceof Variable);
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
        
        KModel model = parserHelper.parse(sb);
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
        		"var bool condition;",
        		"if (condition) {",
        		"if (condition) {}",
        		"}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        assertEquals(1, fields.size());
        
        Field boolConditionVar = fields.get(0);
        Assert.assertTrue(boolConditionVar instanceof Variable);
        Assert.assertEquals("condition", boolConditionVar.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionVar.getDataType());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement outerStmt = statements.get(0);
        Expression outerCondition = KmodelTestUtil.getNextExpressionWithContent(outerStmt.getCondition());
        Field outerIfConditionVar = outerCondition.getFieldRef();
        Assert.assertEquals(boolConditionVar, outerIfConditionVar);
        
        EList<Statement> outerThenStatements = outerStmt.getStatements();
        Assert.assertEquals(1, outerThenStatements.size());
        
        Statement innerStmt = outerThenStatements.get(0);
        Expression innerCondition = KmodelTestUtil.getNextExpressionWithContent(innerStmt.getCondition());
        Field innerIfConditionVar = innerCondition.getFieldRef();
        Assert.assertEquals(boolConditionVar, innerIfConditionVar);
        
        EList<Statement> innerThenStatements = innerStmt.getStatements();
        Assert.assertTrue(innerThenStatements.isEmpty());
    }
    
    @Test
    public void parseIfStatementWithActionCall() throws Exception {
    	String sb = String.join("\n",
    			"action scaleOut(var float balancingFactor);",
    			"if (false) {",
    			"scaleOut(1.0);",
    			"}"
    	);
    	
    	KModel model = parserHelper.parse(sb);
    	KmodelTestUtil.assertModelWithoutErrors(model);
    	KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    	
    	EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        
        Action action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        Assert.assertEquals(DataType.FLOAT, action.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", action.getParameter().getName());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement statement = statements.get(0);
        Expression condition = KmodelTestUtil.getNextExpressionWithContent(statement.getCondition());
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(false, ((BoolLiteral) condition.getLiteral()).isValue());
        
        EList<Statement> thenStatements = statements.get(0).getStatements();
        Assert.assertEquals(1, thenStatements.size());
        
        Statement actionCall = thenStatements.get(0);
        Assert.assertEquals(action, actionCall.getActionRef());
        
        Expression actionArgument = KmodelTestUtil.getNextExpressionWithContent(actionCall.getArgument());
        Assert.assertTrue(actionArgument.getLiteral() instanceof FloatLiteral);
        Assert.assertEquals(1, ((FloatLiteral) actionArgument.getLiteral()).getValue(), 0.0f);
    }
    
    @Test
    public void parseIfStatementWithWrongLiteralType() throws Exception {
        String sb = String.join("\n", 
                "if (\"condition\") {}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"The condition must be of type 'bool'. Got 'string' instead.");
    }
    
    @Test
    public void parseIfStatementWithWrongVariableType() throws Exception {
        String sb = String.join("\n",
        		"var string condition;",
                "if (condition) {}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
        		"The condition must be of type 'bool'. Got 'string' instead.");
    }
    
    
    @Test
    public void parseComplexStatement() throws Exception {
        String sb = String.join("\n"
                ,"//Variable declarations"
                ,"var float i;"
                ,"// action declaration"
                , "action a(var float factor);"
                , "action anotherA(var float factor);"
                
                ,"// rule block"
                ,"if (true) {"
                ,"    a(i);       // execute action"
                ,    "anotherA(i); // execute another action"
                ,"}"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
    
    
}
