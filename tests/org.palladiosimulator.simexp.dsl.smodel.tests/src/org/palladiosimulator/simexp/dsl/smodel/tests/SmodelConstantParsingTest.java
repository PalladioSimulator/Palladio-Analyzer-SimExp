package org.palladiosimulator.simexp.dsl.smodel.tests;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;
import org.palladiosimulator.simexp.dsl.smodel.util.ExpressionUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelConstantParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    private ExpressionUtil expressionUtil = new ExpressionUtil();

    @Test
    public void parseSingleBoolConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool condition = true;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        Assert.assertEquals(1, fields.size());
        Constant constant = fields.get(0);
        Assert.assertEquals("condition", constant.getName());
        Assert.assertEquals(DataType.BOOL, constant.getDataType());
        Literal value = expressionUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        Assert.assertTrue(value instanceof BoolLiteral);
        Assert.assertEquals(true, ((BoolLiteral) value).isTrue());
    }

    @Test
    public void parseSingleIntConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int one = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        Assert.assertEquals(1, fields.size());
        Constant constant = fields.get(0);
        Assert.assertEquals("one", constant.getName());
        Assert.assertEquals(DataType.INT, constant.getDataType());
        Literal value = expressionUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        Assert.assertTrue(value instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) value).getValue());
    }

    @Test
    public void parseSingleFloatConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const float one = 1.0;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        Assert.assertEquals(1, fields.size());
        Constant constant = fields.get(0);
        Assert.assertEquals("one", constant.getName());
        Assert.assertEquals(DataType.FLOAT, constant.getDataType());
        Literal value = expressionUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        Assert.assertTrue(value instanceof FloatLiteral);
        Assert.assertEquals(1, ((FloatLiteral) value).getValue(), 0.0f);
    }

    @Test
    public void parseSingleStringConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const string word = "word";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        Assert.assertEquals(1, fields.size());
        Constant constant = fields.get(0);
        Assert.assertEquals("word", constant.getName());
        Assert.assertEquals(DataType.STRING, constant.getDataType());
        Literal value = expressionUtil.getNextExpressionWithContent(constant.getValue())
            .getLiteral();
        Assert.assertTrue(value instanceof StringLiteral);
        Assert.assertEquals("word", ((StringLiteral) value).getValue());
    }

    @Test
    public void parseTwoConstants() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int count = 1;
                const string word = "word";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        Assert.assertEquals(2, fields.size());
        Constant firstConstant = fields.get(0);
        Assert.assertEquals("count", firstConstant.getName());
        Assert.assertEquals(DataType.INT, firstConstant.getDataType());
        Literal firstValue = expressionUtil.getNextExpressionWithContent(firstConstant.getValue())
            .getLiteral();
        Assert.assertTrue(firstValue instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) firstValue).getValue());
        Constant secondConstant = fields.get(1);
        Assert.assertEquals("word", secondConstant.getName());
        Assert.assertEquals(DataType.STRING, secondConstant.getDataType());
        Literal secondValue = expressionUtil.getNextExpressionWithContent(secondConstant.getValue())
            .getLiteral();
        Assert.assertTrue(secondValue instanceof StringLiteral);
        Assert.assertEquals("word", ((StringLiteral) secondValue).getValue());
    }

    @Test
    public void parseConstantWithConstantValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int const1 = 1;
                const int const2 = const1;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Constant> fields = model.getConstants();
        Assert.assertEquals(2, fields.size());
        Constant firstConstant = fields.get(0);
        Assert.assertEquals("const1", firstConstant.getName());
        Assert.assertEquals(DataType.INT, firstConstant.getDataType());
        Literal firstValue = expressionUtil.getNextExpressionWithContent(firstConstant.getValue())
            .getLiteral();
        Assert.assertTrue(firstValue instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) firstValue).getValue());
        Constant secondConstant = fields.get(1);
        Assert.assertEquals("const2", secondConstant.getName());
        Assert.assertEquals(DataType.INT, secondConstant.getDataType());
        Field fieldReference = expressionUtil.getNextExpressionWithContent(secondConstant.getValue())
            .getFieldRef();
        Literal secondValue = expressionUtil.getNextExpressionWithContent(((Constant) fieldReference).getValue())
            .getLiteral();
        Assert.assertEquals(firstConstant, fieldReference);
        Assert.assertEquals(((IntLiteral) firstValue).getValue(), ((IntLiteral) secondValue).getValue());
    }

    @Test
    public void parseBoolConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseIntConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseFloatConstantWithoutValue() throws Exception {
        String sb = String.join("\n", SmodelTestUtil.MODEL_NAME_LINE, "const float noValue;");

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseStringConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const string noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseBoolConstantWithIntValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool number = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'bool', got 'int' instead.");
    }

    @Test
    public void parseIntConstantWithBoolValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int number = true;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'int', got 'bool' instead.");
    }

    @Test
    public void parseConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseConstantWithVariableValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{0} variable;
                const int constant = variable;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithProbeValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int someProbe : id "someId";
                const int constant = someProbe;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithRuntimeValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint : simple: a;
                const int constant = rint;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithExpressionContainingVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true} variable;
                const bool constant = variable && true;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithSelfReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = false || constant;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1, "Cyclic reference detected.");
    }

    @Test
    public void parseConstantsWithCyclicReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int const1 = const2;
                const int const2 = const1;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2, "Cyclic reference detected.",
                "Cyclic reference detected.");
    }

    @Test
    public void parseConstantWithWrongValueType() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int number = true;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'int', got 'bool' instead.");
    }
}
