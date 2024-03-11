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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.EcoreExpression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Runtime;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelRuntimeParsingJavaTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolRuntime() throws Exception {
        String sb = String.join("\n", "runtime bool rbool: simple: a;");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rbool = fields.get(0);
        Assert.assertTrue(rbool instanceof Runtime);
        Assert.assertEquals("rbool", rbool.getName());
        Assert.assertEquals(DataType.BOOL, rbool.getDataType());

        EcoreExpression a = ((Runtime) rbool).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseSingleIntRuntime() throws Exception {
        String sb = String.join("\n", "runtime int rint: simple: a;");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());

        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseSingleFloatRuntime() throws Exception {
        String sb = String.join("\n", "runtime float rfloat: simple: a;");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rfloat = fields.get(0);
        Assert.assertTrue(rfloat instanceof Runtime);
        Assert.assertEquals("rfloat", rfloat.getName());
        Assert.assertEquals(DataType.FLOAT, rfloat.getDataType());

        EcoreExpression a = ((Runtime) rfloat).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseSingleStringRuntime() throws Exception {
        String sb = String.join("\n", "runtime string rstring: simple: a;");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rstring = fields.get(0);
        Assert.assertTrue(rstring instanceof Runtime);
        Assert.assertEquals("rstring", rstring.getName());
        Assert.assertEquals(DataType.STRING, rstring.getDataType());

        EcoreExpression a = ((Runtime) rstring).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseTwoRuntimes() throws Exception {
        String sb = String.join("\n", "runtime int rint: simple: a;", "runtime string rstring: simple: b;");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(2, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
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
        String sb = String.join("\n", "runtime int rint: simple: a.b;");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
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
        String sb = String.join("\n", "runtime int rint: simple: a[1];");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());

        EcoreExpression a = ((Runtime) rint).getExp();
        Assert.assertEquals("a", a.getName());
        Assert.assertEquals(1, a.getIndex());
        Assert.assertNull(a.getExp());
    }

    @Test
    public void parseRuntimeWithBoolPredicate() throws Exception {
        String sb = String.join("\n", "runtime int rint: simple: a{something=true};");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
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
        String sb = String.join("\n", "runtime int rint: simple: a{something=1};");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
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
        String sb = String.join("\n", "runtime int rint: simple: a{something=0.5};");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
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
        String sb = String.join("\n", "runtime int rint: simple: a{something=\"something\"};");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
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
        String sb = String.join("\n", "runtime int rint: simple: a.b[3].c{selected=true}.d;");

        Kmodel model = parserHelper.parse(sb);
        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);

        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());

        Field rint = fields.get(0);
        Assert.assertTrue(rint instanceof Runtime);
        Assert.assertEquals("rint", rint.getName());
        Assert.assertEquals(DataType.INT, rint.getDataType());

        EcoreExpression a = ((Runtime) rint).getExp();
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
