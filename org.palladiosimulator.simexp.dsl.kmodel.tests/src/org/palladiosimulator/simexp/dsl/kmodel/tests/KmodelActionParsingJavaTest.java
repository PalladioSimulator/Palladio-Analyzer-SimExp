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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelActionParsingJavaTest {
	@Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseActionWithBoolParameter() throws Exception {
        String sb = String.join("\n", 
                "action decreaseQuality(var bool decrease);"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
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
                "action setNumCPUs(var int numCPUs);"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
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
                "action scaleOut(var float balancingFactor);"
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
    }
    
    @Test
    public void parseActionWithStringParameter() throws Exception {
        String sb = String.join("\n", 
                "action setConfiguration(var string name);"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
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
                "action scaleOut(var float scaleOutFactor);",
        		"action scaleIn(var float scaleInFactor);"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(2, actions.size());
        
        Action firstAction = actions.get(0);
        Assert.assertEquals("scaleOut", firstAction.getName());
        Assert.assertEquals(DataType.FLOAT, firstAction.getParameter().getDataType());
        Assert.assertEquals("scaleOutFactor", firstAction.getParameter().getName());
        
        Action secondAction = actions.get(1);
        Assert.assertEquals("scaleIn", secondAction.getName());
        Assert.assertEquals(DataType.FLOAT, secondAction.getParameter().getDataType());
        Assert.assertEquals("scaleInFactor", secondAction.getParameter().getName());
    }
    
    @Test
    public void parseTwoActionsWithSameParameterName() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(var float balancingFactor);",
        		"action scaleIn(var float balancingFactor);"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        List<Issue> issues = validationTestHelper.validate(model);
        Assert.assertTrue(issues.isEmpty());
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(2, actions.size());
        
        Action firstAction = actions.get(0);
        Assert.assertEquals("scaleOut", firstAction.getName());
        Assert.assertEquals(DataType.FLOAT, firstAction.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", firstAction.getParameter().getName());
        
        Action secondAction = actions.get(1);
        Assert.assertEquals("scaleIn", secondAction.getName());
        Assert.assertEquals(DataType.FLOAT, secondAction.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", secondAction.getParameter().getName());
    }
    
    @Test
    public void parseActionCallWithLiteral() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(var float balancingFactor);"
                , "if(true){"
                , "scaleOut(1.0);"
                , "}"
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
        
        Statement actionCall = statements.get(0).getStatements().get(0);
        Assert.assertEquals(actionCall.getAction(), action);
        
        Expression actionArgument = actionCall.getArgument();
        Assert.assertTrue(actionArgument.getLiteral() instanceof FloatLiteral);
        Assert.assertEquals(((FloatLiteral) actionArgument.getLiteral()).getValue(), "1.0");
    }
    
    @Test
    public void parseActionCallWithVariable() throws Exception {
        String sb = String.join("\n", 
                "var float argument;"
                , "action scaleOut(var float balancingFactor);"
                , "if(true){"
                , "scaleOut(argument);"
                , "}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Action> actions = model.getActions();
        Assert.assertEquals(1, actions.size());
        
        Action actionDeclaration = actions.get(0);
        Assert.assertEquals("scaleOut", actionDeclaration.getName());
        Assert.assertEquals(DataType.FLOAT, actionDeclaration.getParameter().getDataType());
        Assert.assertEquals("balancingFactor", actionDeclaration.getParameter().getName());
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        
        Statement actionCall = statements.get(0).getStatements().get(0);
        Assert.assertEquals(actionCall.getAction(), actionDeclaration);
        Assert.assertEquals(actionDeclaration, actionCall.getAction());
        
        Expression actionArgument = actionCall.getArgument();
        Assert.assertEquals("argument", actionArgument.getField().getName());
        Assert.assertEquals(DataType.FLOAT, actionArgument.getField().getDataType());
    }
    
    @Test
    public void parseActionCallWithWrongLiteralType() throws Exception {
        String sb = String.join("\n", 
                "action scaleOut(var float balancingFactor);"
                , "if(true) {"
                , "scaleOut(true);"
                , "}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected an argument of type 'float'. Got 'bool' instead.");
    }
    
    @Test
    public void parseActionCallWithWrongVariableType() throws Exception {
        String sb = String.join("\n", 
                "var int factor;",
                "action scaleOut(var float balancingFactor);"
                , "if(true){"
                , "scaleOut(factor);"
                , "}"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected an argument of type 'float'. Got 'int' instead.");
    }
    
    @Test
    public void parseActionWithoutParameter() throws Exception {
        String sb = String.join("\n", 
                "action setTextualMode();"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 2, "no viable alternative at input ')'", 
        		"mismatched input '<EOF>' expecting RULE_ID");
    }
    
    @Test
    public void parseActionWithTwoParameters() throws Exception {
        String sb = String.join("\n", 
                "action adapt(var bool param1, var string param2);"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ',' expecting ')'");
    }
    
    @Test
    public void parseTwoActionsWithSameName() throws Exception {
    	String sb = String.join("\n"
    	        , "action adapt(var int parameter);"
    	        , "action adapt(var bool param2);"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, 
        		"Duplicate Action 'adapt'", "Duplicate Action 'adapt'");
    }
    
    @Test
    public void parseLocalActionDeclaration() throws Exception {
    	String sb = String.join("\n", 
    			"if(true){",
    			"action adapt(var int parameter);",
    			"}");
    	
    	KModel model = parserHelper.parse(sb);

    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'action' expecting '}'");
    }
    
    @Test
    public void parseActionCallOutsideIf() throws Exception {
    	String sb = String.join("\n", 
    			"action adapt(var int parameter);",
    			"adapt(1);");
    	
    	KModel model = parserHelper.parse(sb);

    	KmodelTestUtil.assertErrorMessages(model, 1, "missing EOF at 'adapt'");
    }
}
