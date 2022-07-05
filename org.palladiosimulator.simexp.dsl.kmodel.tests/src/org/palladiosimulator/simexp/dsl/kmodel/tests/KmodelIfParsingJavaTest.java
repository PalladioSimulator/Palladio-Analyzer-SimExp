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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BodyStatement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolConstant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelIfParsingJavaTest {
	@Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseIfStatementWithConstantCondition() throws Exception {
        String sb = String.join("\n", 
                "if (true) {}"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Expression condition = statements.get(0).getCondition();
        EList<Statement> thenStatements = statements.get(0).getBody().getActions();
        Assert.assertTrue(condition.getConstant() instanceof BoolConstant);
        Assert.assertEquals(((BoolConstant) condition.getConstant()).getValue(), "true");
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
        assertEquals(1, model.getVariables().size());
        Variable boolConditionVar = model.getVariables().get(0).getVar();
        Assert.assertEquals("condition", boolConditionVar.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionVar.getDataType());
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Expression conditionExpr = statements.get(0).getCondition();
        Assert.assertEquals(boolConditionVar, conditionExpr.getVariable());
        Assert.assertEquals(DataType.BOOL, conditionExpr.getVariable().getDataType());
        Assert.assertEquals("condition", conditionExpr.getVariable().getName());
        EList<Statement> thenStatements = statements.get(0).getBody().getActions();
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
        EList<Statement> firstThenStatements = statements.get(0).getBody().getActions();
        Assert.assertTrue(firstCondition.getConstant() instanceof BoolConstant);
        Assert.assertEquals(((BoolConstant) firstCondition.getConstant()).getValue(), "true");
        Assert.assertTrue(firstThenStatements.isEmpty());
        Expression secondCondition = statements.get(1).getCondition();
        EList<Statement> secondThenStatements = statements.get(1).getBody().getActions();
        Assert.assertTrue(secondCondition.getConstant() instanceof BoolConstant);
        Assert.assertEquals(((BoolConstant) secondCondition.getConstant()).getValue(), "true");
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
        assertEquals(1, model.getVariables().size());
        Variable boolConditionVar = model.getVariables().get(0).getVar();
        Assert.assertEquals("condition", boolConditionVar.getName());
        Assert.assertEquals(DataType.BOOL, boolConditionVar.getDataType());
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Statement outerIfStmt = statements.get(0);
        Expression outerIfCondition = outerIfStmt.getCondition();
        Variable outerIfConditionVar = outerIfCondition.getVariable();
        Assert.assertEquals(boolConditionVar, outerIfConditionVar);
        EList<Statement> outerThenStatements = outerIfStmt.getBody().getNested();
        Assert.assertEquals(1, outerThenStatements.size());
        Expression innerIfCondition = outerThenStatements.get(0).getCondition();
        Assert.assertEquals("condition", innerIfCondition.getVariable().getName());
        Assert.assertEquals(boolConditionVar, outerThenStatements.get(0).getCondition().getVariable());
        BodyStatement innerThenStatements = outerThenStatements.get(0).getBody();
        Assert.assertTrue(innerThenStatements.getActions().isEmpty());
    }
    
    @Test
    public void parseIfStatementWithActionCall() throws Exception {
    	String sb = String.join("\n",
    			"action scaleOut(float balancingFactor);",
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
        Assert.assertTrue(condition.getConstant() instanceof BoolConstant);
        Assert.assertEquals(((BoolConstant) condition.getConstant()).getValue(), "false");
        BodyStatement thenStatements = statements.get(0).getBody();
        EList<Statement> thenStmtActions = thenStatements.getActions();
        Assert.assertEquals(1, thenStmtActions.size());
        Assert.assertEquals("scaleOut", thenStmtActions.get(0).getAction().getName());
    }
    
    @Test
    public void parseIfStatementWithWrongConstantType() throws Exception {
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
                , "action a(float factor);"
                , "action anotherA(float factor);"
                
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
