package org.palladiosimulator.simexp.dsl.smodel.tests;

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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Array;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Range;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelOptimizableParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true} condition;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> variables = model.getOptimizables();
        Assert.assertEquals(1, variables.size());
        Field variable = variables.get(0);
        Assert.assertEquals("condition", variable.getName());
        Assert.assertEquals(DataType.BOOL, variable.getDataType());
    }

    @Test
    public void parseSingleIntVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{1} count;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> variables = model.getOptimizables();
        Assert.assertEquals(1, variables.size());
        Field variable = variables.get(0);
        Assert.assertEquals("count", variable.getName());
        Assert.assertEquals(DataType.INT, variable.getDataType());
    }

    @Test
    public void parseSingleFloatVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable float{1.0} number;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> variables = model.getOptimizables();
        Assert.assertEquals(1, variables.size());
        Field variable = variables.get(0);
        Assert.assertEquals("number", variable.getName());
        Assert.assertEquals(DataType.FLOAT, variable.getDataType());
    }

    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{"word"} word;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> variables = model.getOptimizables();
        Assert.assertEquals(1, variables.size());
        Field variable = variables.get(0);
        Assert.assertEquals("word", variable.getName());
        Assert.assertEquals(DataType.STRING, variable.getDataType());
    }

    @Test
    public void parseTwoVariables() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{1} count;
                optimizable string{"word"} word;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> variables = model.getOptimizables();
        Assert.assertEquals(2, variables.size());
        Field firstVariable = variables.get(0);
        Assert.assertEquals("count", firstVariable.getName());
        Assert.assertEquals(DataType.INT, firstVariable.getDataType());
        Field secondVariable = variables.get(1);
        Assert.assertEquals("word", secondVariable.getName());
        Assert.assertEquals(DataType.STRING, secondVariable.getDataType());
    }

    @Test
    public void parseBoolVarArray() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true,false} vName;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> fields = model.getOptimizables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Optimizable variable = (Optimizable) field;
        assertEquals("vName", variable.getName());
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
    public void parseIntVarArray() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{1,3} vName;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> fields = model.getOptimizables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Optimizable variable = (Optimizable) field;
        assertEquals("vName", variable.getName());
        assertEquals(DataType.INT, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Array);
        Array boundsArray = (Array) bounds;
        IntLiteral intRange1 = (IntLiteral) boundsArray.getValues()
            .get(0);
        IntLiteral intRange2 = (IntLiteral) boundsArray.getValues()
            .get(1);
        List<Integer> actualIntBounds = Arrays.asList(intRange1.getValue(), intRange2.getValue());
        MatcherAssert.assertThat(actualIntBounds, CoreMatchers.hasItems(1, 3));
    }

    @Test
    public void parseFloatVarArray() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable float{1.0,3.0} vName;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> fields = model.getOptimizables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Optimizable variable = (Optimizable) field;
        assertEquals("vName", variable.getName());
        assertEquals(DataType.FLOAT, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Array);
        Array boundsArray = (Array) bounds;
        FloatLiteral floatRange1 = (FloatLiteral) boundsArray.getValues()
            .get(0);
        FloatLiteral floatRange2 = (FloatLiteral) boundsArray.getValues()
            .get(1);
        List<Double> actualFloatBounds = Arrays.asList(Double.valueOf(floatRange1.getValue()),
                Double.valueOf(floatRange2.getValue()));
        MatcherAssert.assertThat(actualFloatBounds, CoreMatchers.hasItems(1.0, 3.0));
    }

    @Test
    public void parseStringVarArray() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{"s1","s2"} vName;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> fields = model.getOptimizables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Optimizable variable = (Optimizable) field;
        assertEquals("vName", variable.getName());
        assertEquals(DataType.STRING, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Array);
        Array boundsArray = (Array) bounds;
        StringLiteral stringRange1 = (StringLiteral) boundsArray.getValues()
            .get(0);
        StringLiteral stringRange2 = (StringLiteral) boundsArray.getValues()
            .get(1);
        List<String> actualStringBounds = Arrays.asList(stringRange1.getValue(), stringRange2.getValue());
        MatcherAssert.assertThat(actualStringBounds, CoreMatchers.hasItems("s1", "s2"));
    }

    @Test
    public void parseIntVarRange() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int[1,2,1] vName;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> fields = model.getOptimizables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Optimizable variable = (Optimizable) field;
        assertEquals("vName", variable.getName());
        assertEquals(DataType.INT, variable.getDataType());
        Bounds bounds = variable.getValues();
        assertTrue(bounds instanceof Range);
        Range boundsRange = (Range) bounds;
        assertEquals(1, ((IntLiteral) boundsRange.getStartValue()).getValue());
        assertEquals(2, ((IntLiteral) boundsRange.getEndValue()).getValue());
        assertEquals(1, ((IntLiteral) boundsRange.getStepSize()).getValue());
    }

    @Test
    public void parseVariableWithValueRange() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable float[1.0, 2.0, 0.1] values;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Optimizable> variables = model.getOptimizables();
        Assert.assertEquals(1, variables.size());
        Field field = variables.get(0);
        Optimizable variable = (Optimizable) field;
        Bounds bounds = variable.getValues();
        Assert.assertTrue(bounds instanceof Range);
        Range valueRange = (Range) bounds;
        float startValue = ((FloatLiteral) valueRange.getStartValue()).getValue();
        Assert.assertEquals(1, startValue, 0.0f);
        float endValue = ((FloatLiteral) valueRange.getEndValue()).getValue();
        Assert.assertEquals(2, endValue, 0.0f);
        float stepSize = ((FloatLiteral) valueRange.getStepSize()).getValue();
        Assert.assertEquals(0.1f, stepSize, 0.0f);
    }

    @Test
    public void parseUnusedInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{0} one;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertWarning(model, SmodelPackage.Literals.SMODEL, null,
                "The Optimizable 'one' is never used.");
    }

    @Test
    public void parseVariableWithWrongValueTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{true, 1, 2.5} list;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 3,
                "Expected a value of type 'string', got 'bool' instead.",
                "Expected a value of type 'string', got 'int' instead.",
                "Expected a value of type 'string', got 'float' instead.");
    }

    @Test
    public void parseNonNumberVariableWithRange() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool[true, false, true] range;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign a range to a variable of the type 'bool'.");
    }
}
