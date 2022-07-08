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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
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
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variableDecl = model.getFields();
        Assert.assertEquals(1, variableDecl.size());
        
        Field variable = variableDecl.get(0);
        Assert.assertTrue(variable instanceof Variable);
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
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variableDecl = model.getFields();
        Assert.assertEquals(1, variableDecl.size());
        
        Field variable = variableDecl.get(0);
        Assert.assertTrue(variable instanceof Variable);
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
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variableDecl = model.getFields();
        Assert.assertEquals(1, variableDecl.size());
        
        Field variable = variableDecl.get(0);
        Assert.assertTrue(variable instanceof Variable);
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
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variableDecl = model.getFields();
        Assert.assertEquals(1, variableDecl.size());
        
        Field variable = variableDecl.get(0);
        Assert.assertTrue(variable instanceof Variable);
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
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> variableDecl = model.getFields();
        Assert.assertEquals(2, variableDecl.size());
        
        Field firstVariable = variableDecl.get(0);
        Assert.assertTrue(firstVariable instanceof Variable);
        Assert.assertEquals("count", firstVariable.getName());
        Assert.assertEquals(DataType.INT, firstVariable.getDataType());
        
        Field secondVariable = variableDecl.get(1);
        Assert.assertTrue(secondVariable instanceof Variable);
        Assert.assertEquals("word", secondVariable.getName());
        Assert.assertEquals(DataType.STRING, secondVariable.getDataType());
    }
    
    @Test
    public void parseVariableWithValue() throws Exception {
    	String sb = String.join("\n", 
                "var int number = 1;"
        );
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }
    
    @Test
    public void parseLocalVariable() throws Exception {
    	String sb = String.join("\n", 
    			"if(true){",
    			"var int variable;",
    			"}");
    	
    	KModel model = parserHelper.parse(sb);

    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'var' expecting '}'");
    }
}

