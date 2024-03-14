package org.palladiosimulator.simexp.dsl.kmodel.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KmodelFactory;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.kmodel.tests.util.KmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(KmodelInjectorProvider.class)
public class KmodelProbeParsingJavaTest {
    @Inject
    private ParseHelper<Kmodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleProbeMonitorId() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe bool probeName : monitorId "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("probeName");
        exptectedProbe.setDataType(DataType.BOOL);
        exptectedProbe.setKind(ProbeAdressingKind.MONITORID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleProbeId() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe bool probeName : id "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("probeName");
        exptectedProbe.setDataType(DataType.BOOL);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleBoolProbe() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition : id "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("condition");
        exptectedProbe.setDataType(DataType.BOOL);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleIntProbe() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe int count : id "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("count");
        exptectedProbe.setDataType(DataType.INT);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleFloatVariable() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe float number : id "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("number");
        exptectedProbe.setDataType(DataType.FLOAT);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe string word : id "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("word");
        exptectedProbe.setDataType(DataType.STRING);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseTwoProbes() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe int count : id "someId";
                probe string word : id "someOtherId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertModelWithoutErrors(model);
        KmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe1 = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe1.setName("count");
        exptectedProbe1.setDataType(DataType.INT);
        exptectedProbe1.setKind(ProbeAdressingKind.ID);
        exptectedProbe1.setIdentifier("someId");
        Probe exptectedProbe2 = KmodelFactory.eINSTANCE.createProbe();
        exptectedProbe2.setName("word");
        exptectedProbe2.setDataType(DataType.STRING);
        exptectedProbe2.setKind(ProbeAdressingKind.ID);
        exptectedProbe2.setIdentifier("someOtherId");
        assertThat(model.getProbes(),
                contains(samePropertyValuesAs(exptectedProbe1), samePropertyValuesAs(exptectedProbe2)));
    }

    @Test
    public void parseProbeWithoutKind() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition "someId";
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '\"someId\"' expecting ':'");
    }

    @Test
    public void parseProbeWithoutIdentified() throws Exception {
        String sb = KmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition;
                """;

        Kmodel model = parserHelper.parse(sb);

        KmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting ':'");
    }
}
