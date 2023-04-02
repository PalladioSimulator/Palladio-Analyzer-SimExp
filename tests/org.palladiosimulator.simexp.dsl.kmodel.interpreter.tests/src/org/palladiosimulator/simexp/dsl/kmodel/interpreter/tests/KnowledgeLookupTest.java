package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.kmodel.KmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.KnowledgeLookup;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.RuntimeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Runtime;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class KnowledgeLookupTest {
	
	private ParseHelper<Kmodel> parserHelper;    
    
    @Before
    public void setUp() {
    	Injector injector = new KmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
    	parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Kmodel>>() {}));
    }
    
    @Test
    public void testLookupFieldWithIndex() throws Exception {
    	String sbTest = String.join("\n",
    			"const int a = 2;",
    			"const int b = 1;");
    	Kmodel testModel = parserHelper.parse(sbTest);
    	Field a = testModel.getFields().get(0);
    	Field b = testModel.getFields().get(1);
    	
    	String sb = String.join("\n", 
                "runtime int rint: simple: fields[1];"
        );
    	Kmodel model = parserHelper.parse(sb);
    	Runtime runtime = (Runtime) model.getFields().get(0);
    	
    	RuntimeValueProvider rvp = new KnowledgeLookup(testModel);
    	Object value = rvp.getValue(runtime);
    	Assert.assertEquals(b, value);
    }
    
    @Test
    public void testLookupFieldWithPredicate() throws Exception {
    	String sbTest = String.join("\n",
    			"const int a = 2;",
    			"const int b = 1;");
    	Kmodel testModel = parserHelper.parse(sbTest);
    	Field a = testModel.getFields().get(0);
    	Field b = testModel.getFields().get(1);
    	
    	String sb = String.join("\n", 
                "runtime int rint: simple: fields{name=\"b\"};"
        );
    	Kmodel model = parserHelper.parse(sb);
    	Runtime runtime = (Runtime) model.getFields().get(0);
    	
    	RuntimeValueProvider rvp = new KnowledgeLookup(testModel);
    	Object value = rvp.getValue(runtime);
    	Assert.assertEquals(b, value);
    }
    
    @Test
    public void testLookupFieldName() throws Exception {
    	String sbTest = String.join("\n",
    			"const int a = 1;");
    	Kmodel testModel = parserHelper.parse(sbTest);
    	Constant a = (Constant) testModel.getFields().get(0);
    	
    	String sb = String.join("\n", 
                "runtime int rint: simple: fields[0].name;"
        );
    	Kmodel model = parserHelper.parse(sb);
    	Runtime runtime = (Runtime) model.getFields().get(0);
    	
    	RuntimeValueProvider rvp = new KnowledgeLookup(testModel);
    	Object value = rvp.getValue(runtime);
    	Assert.assertEquals("a", value);
    }
}
