package org.palladiosimulator.simexp.dsl.kmodel.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.validation.Issue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ActionDeclaration;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;

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
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Expression condition = statements.get(0).getCondition();
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(((BoolLiteral) condition.getLiteral()).getValue(), "true");
        
        EList<Statement> thenStatements = statements.get(0).getBody().getStatements();
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
        
        EList<Field> variables = model.getVariables();
        assertEquals(1, model.getVariables().size());
        
        Field boolConditionVar = variables.get(0);
        Assert.assertEquals("condition", boolConditionVar.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionVar.getDataType());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Expression condition = statements.get(0).getCondition();
        Assert.assertEquals(boolConditionVar, condition.getField());
        Assert.assertEquals(DataType.BOOL, condition.getField().getDataType());
        Assert.assertEquals("condition", condition.getField().getName());
        
        EList<Statement> thenStatements = statements.get(0).getBody().getStatements();
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
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(2, statements.size());
        
        Expression firstCondition = statements.get(0).getCondition();
        Assert.assertTrue(firstCondition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(((BoolLiteral) firstCondition.getLiteral()).getValue(), "true");
        
        EList<Statement> firstThenStatements = statements.get(0).getBody().getStatements();
        Assert.assertTrue(firstThenStatements.isEmpty());
        
        Expression secondCondition = statements.get(1).getCondition();
        Assert.assertTrue(secondCondition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(((BoolLiteral) secondCondition.getLiteral()).getValue(), "true");
        
        EList<Statement> secondThenStatements = statements.get(1).getBody().getStatements();
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
        
        EList<Field> variables = model.getVariables();
        assertEquals(1, variables.size());
        
        Field boolConditionVar = variables.get(0);
        Assert.assertEquals("condition", boolConditionVar.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionVar.getDataType());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement outerIfStmt = statements.get(0);
        Field outerIfConditionVar = outerIfStmt.getCondition().getField();
        Assert.assertEquals(boolConditionVar, outerIfConditionVar);
        
        EList<Statement> outerThenStatements = outerIfStmt.getBody().getStatements();
        Assert.assertEquals(1, outerThenStatements.size());
        
        Statement innerIfStmt = outerThenStatements.get(0);
        Field innerIfConditionVar = innerIfStmt.getCondition().getField();
        Assert.assertEquals(boolConditionVar, innerIfConditionVar);
        
        EList<Statement> innerThenStatements = innerIfStmt.getBody().getStatements();
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
    	
    	EList<ActionDeclaration> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        
        ActionDeclaration action = actions.get(0);
        Assert.assertEquals("scaleOut", action.getName());
        Assert.assertEquals(DataType.FLOAT, action.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", action.getParameter().getName());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Expression condition = statements.get(0).getCondition();
        Assert.assertTrue(condition.getLiteral() instanceof BoolLiteral);
        Assert.assertEquals(((BoolLiteral) condition.getLiteral()).getValue(), "false");
        
        EList<Statement> thenStatements = statements.get(0).getBody().getStatements();
        Assert.assertEquals(1, thenStatements.size());
        
        Statement actionCall = thenStatements.get(0);
        Assert.assertEquals(action, actionCall.getAction());
        
        Expression actionArgument = actionCall.getArgument();
        Assert.assertTrue(actionArgument.getLiteral() instanceof FloatLiteral);
        Assert.assertEquals(((FloatLiteral) actionArgument.getLiteral()).getValue(), "1.0");
    }
    
    @Test
    public void parseIfStatementWithWrongLiteralType() throws Exception {
        String sb = String.join("\n", 
                "if (\"condition\") {}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        List<Issue> issues = validationTestHelper.validate(model);
        Assert.assertEquals("The condition must be of type 'bool'. Got 'string' instead.", issues.get(0).getMessage());
        Assert.assertEquals(1, issues.get(0).getLineNumber().intValue());
    }
    
    @Test
    public void parseIfStatementWithWrongVariableType() throws Exception {
        String sb = String.join("\n",
        		"var string condition;",
                "if (condition) {}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        List<Issue> issues = validationTestHelper.validate(model);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals("The condition must be of type 'bool'. Got 'string' instead.", issues.get(0).getMessage());
        Assert.assertEquals(2, issues.get(0).getLineNumber().intValue());
    }
    
    
    @Test
    public void parseComplexStatement() throws Exception {
        String sb = String.join("\n"
                ,"//Variable declarations"
                ,"var int i;"
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

    }
    
    
}
