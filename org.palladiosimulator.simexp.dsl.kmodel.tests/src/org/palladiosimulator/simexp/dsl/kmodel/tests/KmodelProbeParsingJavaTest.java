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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelProbeParsingJavaTest {
    @Inject private ParseHelper<KModel> parserHelper;
    
    @Inject private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolProbe() throws Exception {
        String sb = String.join("\n", 
                "probe someId as bool condition;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        
        Probe probe = (Probe) field;
        Assert.assertEquals("condition", probe.getName());
        Assert.assertEquals(DataType.BOOL, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }
    
    @Test
    public void parseSingleIntProbe() throws Exception {
        String sb = String.join("\n", 
                "probe someId as int count;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        
        Probe probe = (Probe) field;
        Assert.assertEquals("count", probe.getName());
        Assert.assertEquals(DataType.INT, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }
    
    @Test
    public void parseSingleFloatVariable() throws Exception {
        String sb = String.join("\n", 
                "probe someId as float number;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        
        Probe probe = (Probe) field;
        Assert.assertEquals("number", probe.getName());
        Assert.assertEquals(DataType.FLOAT, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }
    
    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = String.join("\n", 
                "probe someId as string word;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        
        Probe probe = (Probe) field;
        Assert.assertEquals("word", probe.getName());
        Assert.assertEquals(DataType.STRING, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }
    
    @Test
    public void parseTwoVariables() throws Exception {
        String sb = String.join("\n", 
                "probe someId as int count;",
                "probe someOtherId as string word;"
        );
        
        KModel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        
        EList<Field> fields = model.getFields();
        Assert.assertEquals(2, fields.size());
        
        Field firstField = fields.get(0);
        Assert.assertTrue(firstField instanceof Probe);
        
        Probe firstProbe = (Probe) firstField;
        Assert.assertEquals("count", firstProbe.getName());
        Assert.assertEquals(DataType.INT, firstProbe.getDataType());
        Assert.assertEquals("someId", firstProbe.getId());
        
        Field secondField = fields.get(1);
        Assert.assertTrue(secondField instanceof Probe);
        
        Probe secondProbe = (Probe) secondField;
        Assert.assertEquals("word", secondProbe.getName());
        Assert.assertEquals(DataType.STRING, secondProbe.getDataType());
        Assert.assertEquals("someOtherId", secondProbe.getId());
    }
    
    @Test
    public void parseProbeWithValue() throws Exception {
    	String sb = String.join("\n", 
                "probe someId as int number = 1;"
        );
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }
    
    @Test
    public void parseProbeWithoutId() throws Exception {
    	String sb = String.join("\n", 
                "probe bool condition;"
        );
    	
    	KModel model = parserHelper.parse(sb);
    	
    	KmodelTestUtil.assertErrorMessages(model, 2, "extraneous input 'bool' expecting RULE_ID",
    			"mismatched input ';' expecting 'as'");
    }
    
    @Test
    public void parseLocalProbe() throws Exception {
    	String sb = String.join("\n", 
    			"if(true){",
    			"probe someId as int number;",
    			"}");
    	
    	KModel model = parserHelper.parse(sb);

    	KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'probe' expecting '}'");
    }
}


