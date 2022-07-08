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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelFieldParsingJavaTest {
	@Inject private ParseHelper<KModel> parserHelper;
	
	@Inject private ValidationTestHelper validationTestHelper;

    @Test
    public void parseAllDifferentFieldTypes() throws Exception {
        String sb = String.join("\n", 
                "var bool condition;",
                "const int one = 1;",
                "probe someId as float aliasName;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(3, fields.size());
        
        Field firstField = fields.get(0);
        Assert.assertTrue(firstField instanceof Variable);
        
        Field secondField = fields.get(1);
        Assert.assertTrue(secondField instanceof Constant);
        
        Field thirdField = fields.get(2);
        Assert.assertTrue(thirdField instanceof Probe);
    }
    
    @Test
    public void parseFieldWithoutFieldType() throws Exception {
        String sb = String.join("\n",
        		"var int variable;",
                "int someField;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 1, "missing EOF at 'int'");
    }
    
    @Test
    public void parseFieldWithInvalidFieldType() throws Exception {
        String sb = String.join("\n",
        		"var int variable;",
                "field int someField;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 1, "missing EOF at 'field'");
    }
    
    @Test
    public void parseFieldWithoutDataType() throws Exception {
        String sb = String.join("\n", 
                "var someField;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input 'someField'");
    }
    
    @Test
    public void parseFieldWithInvalidDataType() throws Exception {
        String sb = String.join("\n", 
                "var type someField;"
        );
        
        KModel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 2, "no viable alternative at input 'type'",
        		"extraneous input 'someField' expecting ';'");
    }
    
    @Test
    public void parseFieldWithoutName() throws Exception {
        String sb = String.join("\n", 
                "var int;"
        );
        
        KModel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "missing RULE_ID at ';'");
    }
    
    @Test
    public void parseFieldWithInvalidName() throws Exception {
        String sb = String.join("\n", 
                "var int 123;"
        );
        
        KModel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '123' expecting RULE_ID");
    }
    
    @Test
    public void parseFieldWithTokenAsName() throws Exception {
        String sb = String.join("\n", 
                "var int const;"
        );
        
        KModel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 2, "mismatched input 'const' expecting RULE_ID",
        		"no viable alternative at input ';'");
    }
    
    @Test
    public void parseSameFieldsWithSameName() throws Exception {
        String sb = String.join("\n", 
                "const int number = 1;",
                "const int number = 1;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'number'", "Duplicate Field 'number'");
    }
    
    @Test
    public void parseDifferentFieldsWithSameName() throws Exception {
        String sb = String.join("\n", 
                "var string word;",
                "probe someId as string word;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'word'", "Duplicate Field 'word'");
    }
}
