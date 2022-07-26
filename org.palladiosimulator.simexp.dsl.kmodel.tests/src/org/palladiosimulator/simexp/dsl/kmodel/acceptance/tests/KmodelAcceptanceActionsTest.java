package org.palladiosimulator.simexp.dsl.kmodel.acceptance.tests;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Array;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Bounds;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KmodelFactory;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Parameter;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;
import org.palladiosimulator.simexp.dsl.kmodel.tests.KmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.kmodel.tests.KmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelAcceptanceActionsTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseOneActionNoParam() throws Exception {
        String sb = String.join("\n", 
                "action aName();"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        assertEquals(0, action.getParameterList().getParameters().size());
    }

    @Test
    public void parseMultipleActionNoParam() throws Exception {
        String sb = String.join("\n", 
                "action aName1();",
                "action aName2();"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(2, actions.size());
        Action action = actions.get(0);
        assertEquals("aName1", action.getName());
        assertEquals(0, action.getParameterList().getParameters().size());
        action = actions.get(1);
        assertEquals("aName2", action.getName());
        assertEquals(0, action.getParameterList().getParameters().size());
    }
    
    @Test
    public void parseOneActionBoolParam() throws Exception {
        String sb = String.join("\n", "action aName(param bool pb);");

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList().getParameters();
        assertEquals(1, parameters.size());
        EList<Field> variables = action.getParameterList().getVariables();
        assertEquals(0, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("pb", param.getName());
        assertEquals(DataType.BOOL, param.getDataType());
    }

    @Test
    public void parseOneActionStringParam() throws Exception {
        String sb = String.join("\n", "action aName(param string ps);");

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList().getParameters();
        assertEquals(1, parameters.size());
        EList<Field> variables = action.getParameterList().getVariables();
        assertEquals(0, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("ps", param.getName());
        assertEquals(DataType.STRING, param.getDataType());
    }

    @Test
    public void parseOneActionIntParam() throws Exception {
        String sb = String.join("\n", "action aName(param int pi);");

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList().getParameters();
        assertEquals(1, parameters.size());
        EList<Field> variables = action.getParameterList().getVariables();
        assertEquals(0, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("pi", param.getName());
        assertEquals(DataType.INT, param.getDataType());
    }

    @Test
    public void parseOneActionMultipleBoolParam() throws Exception {
        String sb = String.join("\n", 
                "action aName(param bool pb1, param bool pb2);"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList().getParameters();
        assertEquals(2, parameters.size());
        EList<Field> variables = action.getParameterList().getVariables();
        assertEquals(0, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("pb1", param.getName());
        assertEquals(DataType.BOOL, param.getDataType());
        param = parameters.get(1);
        assertEquals("pb2", param.getName());
        assertEquals(DataType.BOOL, param.getDataType());
    }

    @Test
    public void parseDuplicateParameterNames1() throws Exception {
        String sb = String.join("\n", 
                "action aName(param bool pb, param bool pb);"
                );

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, 
        		"Duplicate Parameter 'pb'", "Duplicate Parameter 'pb'");
    }

    @Test
    public void parseDuplicateParameterNames2() throws Exception {
        String sb = String.join("\n", 
                "action aName(param bool p, param int p);"
                );

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, 
        		"Duplicate Parameter 'p'", "Duplicate Parameter 'p'");
    }
    
    @Test
    public void parseOneActionBoolVariable() throws Exception {
        String sb = String.join("\n", 
                "action aName(var bool{true,false} vb);"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList().getParameters();
        assertEquals(0, parameters.size());
        EList<Field> variables = action.getParameterList().getVariables();
        assertEquals(1, variables.size());
        Variable variable = (Variable) variables.get(0);
        assertEquals("vb", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Array);
        Array rangeArray = (Array) bounds;
        // TODO check bounds; accept only literals
        //assertThat(rangeArray.getValues(), CoreMatchers.hasItems(items)
    }
    
    @Test
    public void parseOneActionBoolVariableNoBounds() throws Exception {
        String sb = String.join("\n", 
                "action aName(var bool vb);"
                );

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input 'vb'");
    }

    @Test
    public void parseOneActionMultipleVariable() throws Exception {
        String sb = String.join("\n", 
                "action aName(var bool{true,false} vb, var int{1,2} vi);"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList().getParameters();
        assertEquals(0, parameters.size());
        EList<Field> variables = action.getParameterList().getVariables();
        assertEquals(2, variables.size());
        Variable variable1 = (Variable) variables.get(0);
        assertEquals("vb", variable1.getName());
        assertEquals(DataType.BOOL, variable1.getDataType());
        Bounds bounds = variable1.getValues();
        assertTrue(bounds instanceof Array);
        Array rangeArray = (Array) bounds;
        // TODO check bounds; accept only literals
        Variable variable2 = (Variable) variables.get(1);
        assertEquals("vi", variable2.getName());
        assertEquals(DataType.INT, variable2.getDataType());
        Bounds bounds2 = variable2.getValues();
        assertTrue(bounds2 instanceof Array);
        Array rangeArray2 = (Array) bounds2;
        // TODO check bounds; accept only literals
        //assertThat(rangeArray.getValues(), CoreMatchers.hasItems(items)
    }
    
    @Test
    public void parseOneActionMixedParameters() throws Exception {
        String sb = String.join("\n", 
                "action aName(param bool pb, var bool{true,false} vb);"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList().getParameters();
        assertEquals(1, parameters.size());
        EList<Field> variables = action.getParameterList().getVariables();
        assertEquals(1, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("pb", param.getName());
        assertEquals(DataType.BOOL, param.getDataType());
        Variable variable = (Variable) variables.get(0);
        assertEquals("vb", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Array);
        Array rangeArray = (Array) bounds;
        // TODO check bounds; accept only literals
        //assertThat(rangeArray.getValues(), CoreMatchers.hasItems(items)
    }

    @Test
    public void parseOneActionMixedParametersInvalidOrder() throws Exception {
        String sb = String.join("\n", 
                "action aName(var bool{true,false} vb, param bool pb);"
                );

        Kmodel model = parserHelper.parse(sb);
        
        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'param' expecting 'var'");
    }
    
    @Test
    public void parseDuplicateParameterVarNames() throws Exception {
        String sb = String.join("\n", 
                "action aName(param bool p, var bool{true,false} p);"
                );

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';'");
    }
}
