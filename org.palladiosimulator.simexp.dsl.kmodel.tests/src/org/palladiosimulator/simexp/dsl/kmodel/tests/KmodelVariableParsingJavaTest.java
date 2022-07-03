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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelVariableParsingJavaTest {
    @Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolVariable() throws Exception {
        String sb = String.join("\n", 
                "var bool condition;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        Variable variable = statement.getVar();
        
        Assert.assertEquals("condition", variable.getName());
        Assert.assertEquals(DataType.BOOL, variable.getDataType());
    }
    
    @Test
    public void parseSingleIntVariable() throws Exception {
        String sb = String.join("\n", 
                "var int count;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        Variable variable = statement.getVar();
        
        Assert.assertEquals("count", variable.getName());
        Assert.assertEquals(DataType.INT, variable.getDataType());
    }
    
    @Test
    public void parseSingleFloatVariable() throws Exception {
        String sb = String.join("\n", 
                "var float number;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        Variable variable = statement.getVar();
        
        Assert.assertEquals("number", variable.getName());
        Assert.assertEquals(DataType.FLOAT, variable.getDataType());
    }
    
    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = String.join("\n", 
                "var string word;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        Variable variable = statement.getVar();
        
        Assert.assertEquals("word", variable.getName());
        Assert.assertEquals(DataType.STRING, variable.getDataType());
    }
    
    @Test
    public void parseTwoVariables() throws Exception {
        String sb = String.join("\n", 
                "var int count;",
                "var string word;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Statement> statements = model.getStatements();
        Assert.assertEquals(2, statements.size());
        Statement firstStatement = statements.get(0);
        Variable firstVariable = firstStatement.getVar();
        Statement secondStatement = statements.get(1);
        Variable secondVariable = secondStatement.getVar();
        
        Assert.assertEquals("count", firstVariable.getName());
        Assert.assertEquals(DataType.INT, firstVariable.getDataType());
        Assert.assertEquals("word", secondVariable.getName());
        Assert.assertEquals(DataType.STRING, secondVariable.getDataType());
    }
    
    @Test
    public void parseVariableWithInvalidType() throws Exception {
    	String sb = String.join("\n", 
    			"var something name;");
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 2, "no viable alternative at input 'something'",
    			"extraneous input 'name' expecting ';'");
    }
    
    @Test
    public void parseVariableWithoutName() throws Exception {
    	String sb = String.join("\n", 
    			"var bool;");
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 1, "missing RULE_ID at ';'");
    }
    
    @Test
    public void parseVariableWithNumberAsName() throws Exception {
    	String sb = String.join("\n", 
    			"var int 1;");
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '1' expecting RULE_ID");
    }
    
    @Test
    public void parseVariableWithSymbolAsName() throws Exception {
    	String sb = String.join("\n", 
    			"var bool ?;");
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '?' expecting RULE_ID");
    }
    
    @Test
    public void parseVariableWithKeywordAsName() throws Exception {
    	String sb = String.join("\n", 
    			"var float var;");
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'var' expecting RULE_ID");
    }
    
    @Test
    public void parseVariableWithTypeAsName() throws Exception {
    	String sb = String.join("\n", 
    			"var string int;");
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'int' expecting RULE_ID");
    }
    
    @Test
    public void parseTwoVariablesWithSameName() throws Exception {
    	String sb = String.join("\n", 
    			"var bool variable;",
    			"var float variable;");
    	
    	KModel model = parserHelper.parse(sb);

    	KmodelTestUtil.assertModelWithoutErrors(model);
    	
    	List<Issue> issues = validationTestHelper.validate(model);
    	Assert.assertEquals(2, issues.size());
    	
    	Assert.assertEquals("Duplicate Variable 'variable'", issues.get(0).getMessage());
    	Assert.assertEquals(1, issues.get(0).getLineNumber().intValue());
    	Assert.assertEquals("Duplicate Variable 'variable'", issues.get(1).getMessage());
    	Assert.assertEquals(2, issues.get(1).getLineNumber().intValue());
    }
}

