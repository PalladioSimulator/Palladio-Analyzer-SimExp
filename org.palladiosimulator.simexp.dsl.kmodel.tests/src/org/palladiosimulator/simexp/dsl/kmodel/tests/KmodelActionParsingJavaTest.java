package org.palladiosimulator.simexp.dsl.kmodel.tests;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.validation.Issue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatConstant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelActionParsingJavaTest {
	@Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseActionWithBoolParameter() throws Exception {
        String sb = String.join("\n", 
                "action decreaseQuality(bool decrease);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        
        Assert.assertEquals("decreaseQuality", action.getName());
        Assert.assertEquals(DataType.BOOL, action.getParameter().getDataType());
        Assert.assertEquals("decrease", action.getParameter().getName());
    }
    
    @Test
    public void parseActionWithIntParameter() throws Exception {
        String sb = String.join("\n", 
                "action setNumCPUs(int numCPUs);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        
        Assert.assertEquals("setNumCPUs", action.getName());
        Assert.assertEquals(DataType.INT, action.getParameter().getDataType());
        Assert.assertEquals("numCPUs", action.getParameter().getName());
    }
    
    @Test
    public void parseActionWithFloatParameter() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(float balancingFactor);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        
        Assert.assertEquals("scaleOut", action.getName());
        Assert.assertEquals(DataType.FLOAT, action.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", action.getParameter().getName());
    }
    
    @Test
    public void parseActionWithStringParameter() throws Exception {
        String sb = String.join("\n", 
                "action setConfiguration(string name);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        
        Assert.assertEquals("setConfiguration", action.getName());
        Assert.assertEquals(DataType.STRING, action.getParameter().getDataType());
        Assert.assertEquals("name", action.getParameter().getName());
    }

    @Test
    public void parseTwoActions() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(float scaleOutFactor);",
        		"action scaleIn(float scaleInFactor);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(2, actions.size());
        Action firstAction = actions.get(0);
        Action secondAction = actions.get(1);
        
        Assert.assertEquals("scaleOut", firstAction.getName());
        Assert.assertEquals(DataType.FLOAT, firstAction.getParameter().getDataType());
        Assert.assertEquals("scaleOutFactor", firstAction.getParameter().getName());
        Assert.assertEquals("scaleIn", secondAction.getName());
        Assert.assertEquals(DataType.FLOAT, secondAction.getParameter().getDataType());
        Assert.assertEquals("scaleInFactor", secondAction.getParameter().getName());
    }
    
    @Test
    public void parseTwoActionsWithSameParameterName() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(float balancingFactor);",
        		"action scaleIn(float balancingFactor);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        List<Issue> issues = validationTestHelper.validate(model);
        Assert.assertTrue(issues.isEmpty());
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(2, actions.size());
        Action firstAction = actions.get(0);
        Action secondAction = actions.get(1);
        
        Assert.assertEquals("scaleOut", firstAction.getName());
        Assert.assertEquals(DataType.FLOAT, firstAction.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", firstAction.getParameter().getName());
        Assert.assertEquals("scaleIn", secondAction.getName());
        Assert.assertEquals(DataType.FLOAT, secondAction.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", secondAction.getParameter().getName());
    }
    
    @Test
    public void parseActionCallWithConstant() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(float balancingFactor);",
                "scaleOut(1.0);"
        );
        
        // TODO
        // Argumente vom Typ Float MÜSSEN ein Komma haben, also es können keine Int
        // Werte übergeben werden.
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Action actionCall = statements.get(0).getAction();
        Expression actionArgument = statements.get(0).getArgument();
        
        Assert.assertEquals("scaleOut", action.getName());
        Assert.assertEquals(DataType.FLOAT, action.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", action.getParameter().getName());
        Assert.assertEquals(actionCall, action);
        Assert.assertTrue(actionArgument.getConstant() instanceof FloatConstant);
        Assert.assertEquals(((FloatConstant) actionArgument.getConstant()).getValue(), "1.0");
    }
    
    @Test
    public void parseActionCallWithVariable() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(float balancingFactor);",
                "var float argument;",
                "scaleOut(argument);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        Action action = actions.get(0);
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(2, statements.size());
        Variable variable = statements.get(0).getVar();
        Action actionCall = statements.get(1).getAction();
        Expression actionArgument = statements.get(1).getArgument();
        
        Assert.assertEquals("scaleOut", action.getName());
        Assert.assertEquals(DataType.FLOAT, action.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", action.getParameter().getName());
        Assert.assertEquals("argument", variable.getName());
        Assert.assertEquals(DataType.FLOAT, variable.getDataType());
        Assert.assertEquals(actionCall, action);
        Assert.assertEquals(actionArgument.getVariable(), variable);
    }
    
    @Test
    public void parseActionCallWithWrongConstantType() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(float balancingFactor);",
                "scaleOut(true);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        List<Issue> issues = validationTestHelper.validate(model);
        Assert.assertEquals(1, issues.size());
        
        Assert.assertEquals("Expected an argument of type 'float'. Got 'bool' instead.", 
        		issues.get(0).getMessage());
        Assert.assertEquals(2, issues.get(0).getLineNumber().intValue());
    }
    
    @Test
    public void parseActionCallWithWrongVariableType() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(float balancingFactor);",
                "var int factor;",
                "scaleOut(factor);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        List<Issue> issues = validationTestHelper.validate(model);
        Assert.assertEquals(1, issues.size());
        
        Assert.assertEquals("Expected an argument of type 'float'. Got 'int' instead.", 
        		issues.get(0).getMessage());
        Assert.assertEquals(3, issues.get(0).getLineNumber().intValue());
    }
    
    @Test
    public void parseActionWithoutParameter() throws Exception {
        String sb = String.join("\n", 
                "action setTextualMode();"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input ')'");
        
        // TODO
        // Soll eine Action ohne Parameter möglich sein?
    }
    
    @Test
    public void parseTwoActionsWithSameName() throws Exception {
    	String sb = String.join("\n", 
                "action adapt(int param);",
                "action adapt(float param2);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
    	
    	List<Issue> issues = validationTestHelper.validate(model);
    	Assert.assertEquals(2, issues.size());
    	
    	Assert.assertEquals("Duplicate Action 'adapt'", issues.get(0).getMessage());
    	Assert.assertEquals(1, issues.get(0).getLineNumber().intValue());
    	Assert.assertEquals("Duplicate Action 'adapt'", issues.get(1).getMessage());
    	Assert.assertEquals(2, issues.get(1).getLineNumber().intValue());
    }
}
