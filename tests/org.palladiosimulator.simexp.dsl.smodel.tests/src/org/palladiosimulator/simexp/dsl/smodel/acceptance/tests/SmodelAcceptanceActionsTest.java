package org.palladiosimulator.simexp.dsl.smodel.acceptance.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Array;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelAcceptanceActionsTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseOneActionNoParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName();
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        assertEquals(0, action.getParameterList()
            .getParameters()
            .size());
    }

    @Test
    public void parseMultipleActionNoParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName1();
                action aName2();
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(2, actions.size());
        Action action = actions.get(0);
        assertEquals("aName1", action.getName());
        assertEquals(0, action.getParameterList()
            .getParameters()
            .size());
        action = actions.get(1);
        assertEquals("aName2", action.getName());
        assertEquals(0, action.getParameterList()
            .getParameters()
            .size());
    }

    @Test
    public void parseOneActionBoolParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param bool pb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList()
            .getParameters();
        assertEquals(1, parameters.size());
        EList<Optimizable> variables = action.getParameterList()
            .getVariables();
        assertEquals(0, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("pb", param.getName());
        assertEquals(DataType.BOOL, param.getDataType());
    }

    @Test
    public void parseOneActionStringParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param string ps);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList()
            .getParameters();
        assertEquals(1, parameters.size());
        EList<Optimizable> variables = action.getParameterList()
            .getVariables();
        assertEquals(0, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("ps", param.getName());
        assertEquals(DataType.STRING, param.getDataType());
    }

    @Test
    public void parseOneActionIntParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param int pi);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList()
            .getParameters();
        assertEquals(1, parameters.size());
        EList<Optimizable> variables = action.getParameterList()
            .getVariables();
        assertEquals(0, variables.size());
        Parameter param = parameters.get(0);
        assertEquals("pi", param.getName());
        assertEquals(DataType.INT, param.getDataType());
    }

    @Test
    public void parseOneActionMultipleBoolParam() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param bool pb1, param bool pb2);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList()
            .getParameters();
        assertEquals(2, parameters.size());
        EList<Optimizable> variables = action.getParameterList()
            .getVariables();
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
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param bool pb, param bool pb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'pb'",
                "Duplicate Field 'pb'");
    }

    @Test
    public void parseDuplicateParameterNames2() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param bool p, param int p);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'p'",
                "Duplicate Field 'p'");
    }

    @Test
    public void parseOneActionBoolVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable bool{true,false} vb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList()
            .getParameters();
        assertEquals(0, parameters.size());
        EList<Optimizable> variables = action.getParameterList()
            .getVariables();
        assertEquals(1, variables.size());
        Optimizable variable = variables.get(0);
        assertEquals("vb", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Array);
        Array rangeArray = (Array) bounds;
        BoolLiteral boolRange1 = (BoolLiteral) rangeArray.getValues()
            .get(0);
        BoolLiteral boolRange2 = (BoolLiteral) rangeArray.getValues()
            .get(1);
        List<Boolean> actualBoolBounds = Arrays.asList(boolRange1.isTrue(), boolRange2.isTrue());
        MatcherAssert.assertThat(actualBoolBounds, CoreMatchers.hasItems(true, false));
    }

    @Test
    public void parseOneActionBoolVariableNoBounds() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable bool vb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input 'vb'");
    }

    @Test
    public void parseOneActionMultipleVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable bool{true,false} vb, optimizable int{1,2} vi);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList()
            .getParameters();
        assertEquals(0, parameters.size());
        EList<Optimizable> variables = action.getParameterList()
            .getVariables();
        assertEquals(2, variables.size());
        Optimizable variable1 = variables.get(0);
        assertEquals("vb", variable1.getName());
        assertEquals(DataType.BOOL, variable1.getDataType());
        Bounds bounds = variable1.getValues();
        assertTrue(bounds instanceof Array);
        Array rangeArray = (Array) bounds;
        BoolLiteral boolRange1 = (BoolLiteral) rangeArray.getValues()
            .get(0);
        BoolLiteral boolRange2 = (BoolLiteral) rangeArray.getValues()
            .get(1);
        List<Boolean> actualBoolBounds = Arrays.asList(boolRange1.isTrue(), boolRange2.isTrue());
        MatcherAssert.assertThat(actualBoolBounds, CoreMatchers.hasItems(true, false));
        Optimizable variable2 = variables.get(1);
        assertEquals("vi", variable2.getName());
        assertEquals(DataType.INT, variable2.getDataType());
        Bounds bounds2 = variable2.getValues();
        assertTrue(bounds2 instanceof Array);
        Array rangeArray2 = (Array) bounds2;
        IntLiteral intRange1 = (IntLiteral) rangeArray2.getValues()
            .get(0);
        IntLiteral intRange2 = (IntLiteral) rangeArray2.getValues()
            .get(1);
        List<Integer> actualIntBounds = Arrays.asList(intRange1.getValue(), intRange2.getValue());
        MatcherAssert.assertThat(actualIntBounds, CoreMatchers.hasItems(1, 2));
    }

    @Test
    public void parseOneActionMixedParameters() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param bool pb, optimizable bool{true,false} vb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Action> actions = model.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        assertEquals("aName", action.getName());
        EList<Parameter> parameters = action.getParameterList()
            .getParameters();
        assertEquals(1, parameters.size());
        EList<Optimizable> variables = action.getParameterList()
            .getVariables();
        assertEquals(1, variables.size());
        Field param = parameters.get(0);
        assertEquals("pb", param.getName());
        assertEquals(DataType.BOOL, param.getDataType());
        Optimizable variable = variables.get(0);
        assertEquals("vb", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Array);
        Array rangeArray = (Array) bounds;
        BoolLiteral boolRange1 = (BoolLiteral) rangeArray.getValues()
            .get(0);
        BoolLiteral boolRange2 = (BoolLiteral) rangeArray.getValues()
            .get(1);
        List<Boolean> actualBoolBounds = Arrays.asList(boolRange1.isTrue(), boolRange2.isTrue());
        MatcherAssert.assertThat(actualBoolBounds, CoreMatchers.hasItems(true, false));
    }

    @Test
    public void parseOneActionMixedParametersInvalidOrder() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(optimizable bool{true,false} vb, param bool pb);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input 'param' expecting 'optimizable'");
    }

    @Test
    public void parseDuplicateParameterVarNames() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                action aName(param bool p, optimizable bool{true,false} p);
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'p'",
                "Duplicate Field 'p'");
    }
}