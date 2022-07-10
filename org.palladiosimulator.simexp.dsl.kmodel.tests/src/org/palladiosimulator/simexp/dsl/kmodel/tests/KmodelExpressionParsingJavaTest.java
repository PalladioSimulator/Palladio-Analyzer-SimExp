package org.palladiosimulator.simexp.dsl.kmodel.tests;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelExpressionParsingJavaTest {
	@Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;
    
    @Test
    public void parseSimpleLiteralExpression() throws Exception {
        String sb = String.join("\n", 
                "const int constant = 1;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        // TODO
    }
    
    @Test
    public void parseSimpleFieldExpression() throws Exception {
        String sb = String.join("\n",
        		"var int variable;",
                "const int a = variable;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        // TODO
    }
    
    @Test
    public void parseSimpleExpressionWithBrackets() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = (1);"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseDisjunctionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = true | false;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseConjunctionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = true & false;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseEqualityExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = \"some\" == \"thing\";"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseNegationExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = !true;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseComparisonExpression() throws Exception {
    	String sb = String.join("\n", 
                "const bool constant = 1 <= 2;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseAdditionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = 1 + 2;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseAdditiveInversionExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = -1;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseMultiplicationExpression() throws Exception {
    	String sb = String.join("\n", 
                "const int constant = 1 * 2;"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
     // TODO
    }
    
    @Test
    public void parseComplexExpression() throws Exception {
    	String sb = String.join("\n",
    			"const int a = 1;",
                "const bool constant = -a > 1 == !(true | false & (a * (1 + a)) != 2.0);"
        );
    	
    	KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
    }
    
    @Test
    public void parseDisjunctionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = true | 1.0;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'bool'. Got 'float' instead.");
    }
    
    @Test
    public void parseConjunctionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = \"true\" & false;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Expected a value of type 'bool'. Got 'string' instead.");
    }
    
    @Test
    public void parseEqualityWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = \"1.0\" == 1.0;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, 
        		"Cannot compare the equality of a 'string' value with a 'float' value.",
        		"Cannot compare the equality of a 'string' value with a 'float' value.");
    }
    
    @Test
    public void parseNegationWithInvalidType() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = !1;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot negate a 'int' value.");
    }
    
    @Test
    public void parseComparisonWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const bool constant = 1 >= false;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot compare a 'int' value with a 'bool' value.");
    }
    
    @Test
    public void parseAdditionWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const int constant = true + 1;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot add or subtract a 'bool' value with a 'int' value.");
    }
    
    @Test
    public void parseAdditiveInverseWithInvalidType() throws Exception {
        String sb = String.join("\n", 
                "const int constant = -false;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, 
        		"Expected a value of type 'int'. Got 'bool' instead.",
        		"Cannot invert a 'bool' value.");
    }
    
    @Test
    public void parseMultiplicationWithInvalidTypes() throws Exception {
        String sb = String.join("\n", 
                "const float constant = 1.5 * \"2\";"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, 
        		"Cannot multiply or divide a 'float' value with a 'string' value.");
    }
    
    @Test
    public void parseExpressionWithWrongBrackets() throws Exception {
        String sb = String.join("\n", 
                "const string word = ((\"word\");"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertErrorMessages(model, 1, "missing ')' at ';'");
    }
}
