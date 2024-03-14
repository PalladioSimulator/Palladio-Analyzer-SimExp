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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelTestUtil;
import org.palladiosimulator.simexp.dsl.kmodel.util.ExpressionUtil;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelConstantParsingJavaTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    private ExpressionUtil expressionUtil = new ExpressionUtil();

    @Test
    public void parseSingleBoolConstant() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const bool condition = true;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const int one = 1;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const float one = 1.0;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const string word = "word";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const int count = 1;
                const string word = "word";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const int const1 = 1;
                const int const2 = const1;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
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
    public void parseConstantWithoutValue() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const int noValue;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting '='");
    }

    @Test
    public void parseConstantWithVariableValue() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                var int{0} variable;
                const int constant = variable;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithProbeValue() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe int someProbe : id "someId";
                const int constant = someProbe;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithRuntimeValue() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint : simple: a;
                const int constant = rint;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithExpressionContainingVariable() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                var bool{true} variable;
                const bool constant = variable && true;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithSelfReference() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = false || constant;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                // TODO:
                // "Couldn't resolve reference to Field 'constant'.");
                "Cyclic reference detected.");
    }

    @Test
    public void parseConstantsWithCyclicReference() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const int const1 = const2;
                const int const2 = const1;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 2,
                // TODO:
                // "Couldn't resolve reference to Field 'const2'.");
                "Cyclic reference detected.", "Cyclic reference detected.");
    }

    @Test
    public void parseConstantWithWrongValueType() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                const int number = true;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertValidationIssues(validationTestHelper, model, 1,
                "Expected a value of type 'int', got 'bool' instead.");
    }
}
