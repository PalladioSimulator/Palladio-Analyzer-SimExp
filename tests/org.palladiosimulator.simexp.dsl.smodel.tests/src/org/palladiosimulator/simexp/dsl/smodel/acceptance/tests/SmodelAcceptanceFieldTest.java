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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Array;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Range;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelAcceptanceFieldTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseBoolProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool pName: id "ab11";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Probe> fields = model.getProbes();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Probe probe = (Probe) field;
        assertEquals("pName", probe.getName());
        assertEquals(DataType.BOOL, probe.getDataType());
        assertEquals("ab11", probe.getIdentifier());
    }

    @Test
    public void parseIntProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int pName: id "ab11";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Probe> fields = model.getProbes();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Probe probe = (Probe) field;
        assertEquals("pName", probe.getName());
        assertEquals(DataType.INT, probe.getDataType());
        assertEquals("ab11", probe.getIdentifier());
    }

    @Test
    public void parseFloatProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe float pName: id "ab11";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Probe> fields = model.getProbes();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Probe probe = (Probe) field;
        assertEquals("pName", probe.getName());
        assertEquals(DataType.FLOAT, probe.getDataType());
        assertEquals("ab11", probe.getIdentifier());
    }

    @Test
    public void parseStringProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe string pName: id "ab11";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Probe> fields = model.getProbes();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Probe probe = (Probe) field;
        assertEquals("pName", probe.getName());
        assertEquals(DataType.STRING, probe.getDataType());
        assertEquals("ab11", probe.getIdentifier());
    }

    @Test
    public void parseBoolProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool pName: id "ab11" = true;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseIntProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int pName: id "ab11" = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseFloatProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe float pName: id "ab11" = 1.0;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseStringProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe string pName: id "ab11" = "s";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
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
    public void parseMultipleVariableTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool cBool = true;
                probe bool pBool: id "ab11";
                optimizable bool{true,false} vBool;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> constants = model.getConstants();
        assertEquals(1, constants.size());
        Field constant = constants.get(0);
        assertEquals("cBool", constant.getName());
        assertEquals(DataType.BOOL, constant.getDataType());
        EList<Probe> probes = model.getProbes();
        Probe probe = probes.get(0);
        assertEquals("pBool", probe.getName());
        assertEquals(DataType.BOOL, probe.getDataType());
        assertEquals("ab11", probe.getIdentifier());
        EList<Optimizable> variables = model.getOptimizables();
        Optimizable variable = variables.get(0);
        assertEquals("vBool", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
    }

    @Test
    public void parseDuplicateConstantNames() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool name = true;
                const int name = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateProbeNames() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool name: id "ax10";
                probe int name: id "ax11";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateVariableNames() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true,false} name;
                optimizable int{1,2] name;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ']' expecting '}'");
    }

    @Test
    public void parseDuplicateConstantEnvVariableName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool name = true;
                envvar bool name : staticId "statId" dynamicId "dynId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateProbeAddressing() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool name1: id "ax10";
                probe bool name2: id "ax10";
                if (name1 && name2) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, model.eClass(), null, "probe 'name2' duplicate addressing.");
    }

    @Test
    public void parseDuplicateEnvVariableAddressing() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                envvar bool name1 : staticId "statId" dynamicId "dynId";
                envvar bool name2 : staticId "statId" dynamicId "dynId";
                if (name1 && name2) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, model.eClass(), null, "envvar 'name2' duplicate addressing.");
    }
}
