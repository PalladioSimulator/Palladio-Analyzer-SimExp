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
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EcoreExpression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Runtime;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelRuntimeParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolRuntime() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime bool rbool: simple: a;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rbool = fields.get(0);
        Assert.assertEquals("rbool", rbool.getName());
        Assert.assertEquals(DataType.BOOL, rbool.getDataType());
        EcoreExpression a = ((Runtime) rbool).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseSingleIntRuntime() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseSingleFloatRuntime() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime float rfloat: simple: a;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rfloat = fields.get(0);
        Assert.assertEquals("rfloat", rfloat.getName());
        Assert.assertEquals(DataType.FLOAT, rfloat.getDataType());

        EcoreExpression a = ((Runtime) rfloat).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseSingleStringRuntime() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime string rstring: simple: a;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rstring = fields.get(0);
        Assert.assertEquals("rstring", rstring.getName());
        Assert.assertEquals(DataType.STRING, rstring.getDataType());
        EcoreExpression a = ((Runtime) rstring).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseTwoRuntimes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a;
                runtime string rstring: simple: b;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(2, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
        Field rstring = fields.get(1);
        Assert.assertTrue(rstring instanceof Runtime);
        Assert.assertEquals("rstring", rstring.getName());
        Assert.assertEquals(DataType.STRING, rstring.getDataType());
        EcoreExpression b = ((Runtime) rstring).getExp();
        Assert.assertEquals("b", b.getName());
        Assert.assertNull(b.getExp());
    }

    @Test
    public void parseRuntimeWithTwoReferences() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a.b;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNotNull(a.getExp());
        EcoreExpression b = a.getExp();
        Assert.assertEquals("b", b.getName());
        Assert.assertNull(b.getExp());
    }

    @Test
    public void parseRuntimeWithIndex() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a[1];
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertEquals(1, a.getIndex());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseRuntimeWithBoolPredicate() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a{something=true};
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertEquals("something", a.getParam());
        Assert.assertTrue(a.getValue() instanceof BoolLiteral);
        Assert.assertTrue(((BoolLiteral) a.getValue()).isTrue());
    }

    @Test
    public void parseRuntimeWithIntPredicate() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a{something=1};
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertEquals("something", a.getParam());
        Assert.assertTrue(a.getValue() instanceof IntLiteral);
        Assert.assertEquals(1, ((IntLiteral) a.getValue()).getValue());
    }

    @Test
    public void parseRuntimeWithFloatPredicate() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a{something=0.5};
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertEquals("something", a.getParam());
        Assert.assertTrue(a.getValue() instanceof FloatLiteral);
        Assert.assertEquals(0.5, ((FloatLiteral) a.getValue()).getValue(), 0);
    }

    @Test
    public void parseRuntimeWithStringPredicate() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a{something="something"};
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Field rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertEquals("something", a.getParam());
        Assert.assertTrue(a.getValue() instanceof StringLiteral);
        Assert.assertEquals("something", ((StringLiteral) a.getValue()).getValue());
    }

    @Test
    public void parseRuntimeWithComplexExpression() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                runtime int rint: simple: a.b[3].c{selected=true}.d;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Runtime> fields = model.getRuntimes();
        Assert.assertEquals(1, fields.size());
        Runtime rint = fields.get(0);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());
        EcoreExpression a = rint.getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNotNull(a.getExp());
        EcoreExpression b = a.getExp();
        Assert.assertEquals("b", b.getName());
        Assert.assertEquals(3, b.getIndex());
        Assert.assertNotNull(b.getExp());
        EcoreExpression c = b.getExp();
        Assert.assertEquals("c", c.getName());
        Assert.assertEquals("selected", c.getParam());
        Assert.assertTrue(c.getValue() instanceof BoolLiteral);
        Assert.assertTrue(((BoolLiteral) c.getValue()).isTrue());
        Assert.assertNotNull(c.getExp());
        EcoreExpression d = c.getExp();
        Assert.assertEquals("d", d.getName());
        Assert.assertNull(d.getExp());
    }
}