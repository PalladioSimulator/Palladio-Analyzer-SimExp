package org.palladiosimulator.simexp.dsl.kmodel.tests;

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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
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
        EList<Statement> thenStatements = statements.get(0).getStatements();
        
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
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(2, statements.size());
        Variable conditionVar = statements.get(0).getVar();
        Expression condition = statements.get(1).getCondition();
        EList<Statement> thenStatements = statements.get(1).getStatements();
        
        Assert.assertEquals(DataType.BOOL, conditionVar.getDataType());
        Assert.assertEquals("condition", conditionVar.getName());
        Assert.assertEquals(conditionVar, condition.getVariable());
        Assert.assertTrue(thenStatements.isEmpty());
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
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(2, statements.size());
        Variable conditionVar = statements.get(0).getVar();
        Expression outerIfCondition = statements.get(1).getCondition();
        EList<Statement> outerThenStatements = statements.get(1).getStatements();
        
        Assert.assertEquals(DataType.BOOL, conditionVar.getDataType());
        Assert.assertEquals("condition", conditionVar.getName());
        Assert.assertEquals(conditionVar, outerIfCondition.getVariable());
        Assert.assertEquals(1, outerThenStatements.size());
        
        Expression innerIfCondition = outerThenStatements.get(0).getCondition();
        EList<Statement> innerThenStatements = outerThenStatements.get(0).getStatements();
        Assert.assertTrue(innerThenStatements.isEmpty());
        
        Assert.assertEquals(conditionVar, innerIfCondition.getVariable());
        Assert.assertTrue(innerThenStatements.isEmpty());
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
    	
    	EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Expression condition = statements.get(0).getCondition();
        EList<Statement> thenStatements = statements.get(0).getStatements();
        
        Assert.assertEquals("scaleOut", action.getName());
        Assert.assertEquals(DataType.FLOAT, action.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", action.getParameter().getName());
        Assert.assertTrue(condition.getConstant() instanceof BoolConstant);
        Assert.assertEquals(((BoolConstant) condition.getConstant()).getValue(), "false");
        Assert.assertEquals(1, thenStatements.size());
        Assert.assertEquals(action, thenStatements.get(0).getAction());
    }
    
    @Test
    public void parseIfStatementWithWrongConstantType() throws Exception {
        String sb = String.join("\n", 
                "if (\"condition\") {}"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        List<Issue> issues = validationTestHelper.validate(model);
        Assert.assertEquals(1, issues.size());
        
        Assert.assertEquals("The condition must be of type 'bool'. Got 'string' instead.",
        		issues.get(0).getMessage());
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
        
        Assert.assertEquals("The condition must be of type 'bool'. Got 'string' instead.",
        		issues.get(0).getMessage());
        Assert.assertEquals(2, issues.get(0).getLineNumber().intValue());
    }
}
