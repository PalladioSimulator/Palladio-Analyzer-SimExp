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
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelProbeParsingJavaTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleBoolProbe() throws Exception {
        String sb = String.join("\n", "probe bool condition : \"someId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        Probe probe = (Probe) field;
        Assert.assertEquals("condition", probe.getName());
        Assert.assertEquals(DataType.BOOL, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }

    @Test
    public void parseSingleIntProbe() throws Exception {
        String sb = String.join("\n", "probe int count : \"someId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        Probe probe = (Probe) field;
        Assert.assertEquals("count", probe.getName());
        Assert.assertEquals(DataType.INT, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }

    @Test
    public void parseSingleFloatVariable() throws Exception {
        String sb = String.join("\n", "probe float number : \"someId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        Probe probe = (Probe) field;
        Assert.assertEquals("number", probe.getName());
        Assert.assertEquals(DataType.FLOAT, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }

    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = String.join("\n", "probe string word : \"someId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Field> fields = model.getFields();
        Assert.assertEquals(1, fields.size());
        Field field = fields.get(0);
        Assert.assertTrue(field instanceof Probe);
        Probe probe = (Probe) field;
        Assert.assertEquals("word", probe.getName());
        Assert.assertEquals(DataType.STRING, probe.getDataType());
        Assert.assertEquals("someId", probe.getId());
    }

    @Test
    public void parseTwoProbes() throws Exception {
        String sb = String.join("\n", "probe int count : \"someId\";", "probe string word : \"someOtherId\";");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        EList<Field> fields = model.getFields();
        Assert.assertEquals(2, fields.size());
        Field firstField = fields.get(0);
        Assert.assertTrue(firstField instanceof Probe);
        Probe firstProbe = (Probe) firstField;
        Assert.assertEquals("count", firstProbe.getName());
        Assert.assertEquals(DataType.INT, firstProbe.getDataType());
        Assert.assertEquals("someId", firstProbe.getId());
        Field secondField = fields.get(1);
        Assert.assertTrue(secondField instanceof Probe);
        Probe secondProbe = (Probe) secondField;
        Assert.assertEquals("word", secondProbe.getName());
        Assert.assertEquals(DataType.STRING, secondProbe.getDataType());
        Assert.assertEquals("someOtherId", secondProbe.getId());
    }

    @Test
    public void parseProbeWithoutId() throws Exception {
        String sb = String.join("\n", "probe bool condition;");

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting ':'");
    }
}
