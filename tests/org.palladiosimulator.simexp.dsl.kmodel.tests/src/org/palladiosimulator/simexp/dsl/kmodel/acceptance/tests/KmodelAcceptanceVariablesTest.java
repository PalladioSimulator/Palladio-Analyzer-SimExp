package org.palladiosimulator.simexp.dsl.kmodel.acceptance.tests;

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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Array;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Bounds;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Range;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelAcceptanceVariablesTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolConstant() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const bool condition = true;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> constants = model.getConstants();
        assertEquals(1, constants.size());
        Constant field = constants.get(0);
        Constant constant = field;
        assertEquals("condition", constant.getName());
        assertEquals(DataType.BOOL, constant.getDataType());
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        assertTrue(value instanceof BoolLiteral);
        assertEquals(true, ((BoolLiteral) value).isTrue());
    }

    @Test
    public void parseSingleIntConstant() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const int one = 1;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        assertEquals(1, fields.size());
        Constant constant = fields.get(0);
        assertEquals("one", constant.getName());
        assertEquals(DataType.INT, constant.getDataType());
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        assertTrue(value instanceof IntLiteral);
        assertEquals(1, ((IntLiteral) value).getValue());
    }

    @Test
    public void parseSingleFloatConstant() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const float one = 1.0;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Constant constant = (Constant) field;
        assertEquals("one", constant.getName());
        assertEquals(DataType.FLOAT, constant.getDataType());
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        assertTrue(value instanceof FloatLiteral);
        assertEquals(1, ((FloatLiteral) value).getValue(), 0.0f);
    }

    @Test
    public void parseSingleStringConstant() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const string word = \"word\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Constant constant = (Constant) field;
        assertEquals("word", constant.getName());
        assertEquals(DataType.STRING, constant.getDataType());
        Literal value = KmodelTestUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        assertTrue(value instanceof StringLiteral);
        assertEquals("word", ((StringLiteral) value).getValue());
    }

    @Test
    public void parseTwoConstants() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const int count = 1;",
                "const string word = \"word\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        assertEquals(2, fields.size());
        Field firstField = fields.get(0);
        Constant firstConstant = (Constant) firstField;
        assertEquals("count", firstConstant.getName());
        assertEquals(DataType.INT, firstConstant.getDataType());
        Literal firstValue = KmodelTestUtil.getNextExpressionWithContent(firstConstant.getValue())
            .getLiteral();
        assertTrue(firstValue instanceof IntLiteral);
        assertEquals(1, ((IntLiteral) firstValue).getValue());
        Field secondField = fields.get(1);
        Constant secondConstant = (Constant) secondField;
        assertEquals("word", secondConstant.getName());
        assertEquals(DataType.STRING, secondConstant.getDataType());
        Literal secondValue = KmodelTestUtil.getNextExpressionWithContent(secondConstant.getValue())
            .getLiteral();
        assertTrue(secondValue instanceof StringLiteral);
        assertEquals("word", ((StringLiteral) secondValue).getValue());
    }

    @Test
    public void parseBoolConstantWithoutValue() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const bool noValue;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseIntConstantWithoutValue() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const int noValue;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseFloatConstantWithoutValue() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const float noValue;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseStringConstantWithoutValue() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const string noValue;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseConstantWithVariableValue() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "var int variable;",
                "const int constant = variable;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "no viable alternative at input 'variable'");
    }

    @Test
    public void parseBoolConstantWithIntValue() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const bool number = 1;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'bool', got 'int' instead.");
    }

    @Test
    public void parseIntConstantWithBoolValue() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const int number = true;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'int', got 'bool' instead.");
    }

    @Test
    public void parseBoolProbe() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe bool pName: id \"ab11\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe int pName: id \"ab11\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe float pName: id \"ab11\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe string pName: id \"ab11\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe bool pName: id \"ab11\" = true;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseIntProbeAssignment() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe int pName: id \"ab11\" = 1;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseFloatProbeAssignment() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe float pName: id \"ab11\" = 1.0;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseStringProbeAssignment() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe string pName: id \"ab11\" = \"s\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseBoolVarArray() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "var bool{true,false} vName;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Variable> fields = model.getVariables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Variable variable = (Variable) field;
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "var int{1,3} vName;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Variable> fields = model.getVariables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Variable variable = (Variable) field;
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "var float{1.0,3.0} vName;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Variable> fields = model.getVariables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Variable variable = (Variable) field;
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "var string{\"s1\",\"s2\"} vName;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Variable> fields = model.getVariables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Variable variable = (Variable) field;
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "var int[1,2,1] vName;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Variable> fields = model.getVariables();
        assertEquals(1, fields.size());
        Field field = fields.get(0);
        Variable variable = (Variable) field;
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
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const bool cBool = true;",
                "probe bool pBool: id \"ab11\";", "var bool{true,false} vBool;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        EList<Variable> variables = model.getVariables();
        Variable variable = variables.get(0);
        assertEquals("vBool", variable.getName());
        assertEquals(DataType.BOOL, variable.getDataType());
    }

    @Test
    public void parseDuplicateConstantNames() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const bool name = true;", "const int name = 1;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateProbeNames() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "probe bool name: id \"ax10\";",
                "probe int name: id \"ax11\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateVariableNames() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "var bool{true,false} name;",
                "var int{1,2] name;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ']' expecting '}'");
    }

    @Test
    public void parseDuplicateConstantEnvVariableName() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE, "const bool name = true;",
                "envvar bool name : staticId \"statId\" dynamicId \"dynId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Duplicate Field 'name'",
                "Duplicate Field 'name'");
    }

    @Test
    public void parseDuplicateEnvVariableAddressing() throws Exception {
        String sb = String.join("\n", KmodelTestUtil.MODEL_NAME_LINE,
                "envvar bool name1 : staticId \"statId\" dynamicId \"dynId\";",
                "envvar bool name2 : staticId \"statId\" dynamicId \"dynId\";", "if (name1 && name2) {}");

        Kmodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, model.eClass(), null, "envvar 'name2' duplicate addressing.");
    }
}
