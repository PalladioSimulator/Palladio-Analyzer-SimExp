package org.palladiosimulator.simexp.dsl.smodel.tests;

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
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelProbeParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseSingleProbeMonitorId() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool probeName : monitorId "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("probeName");
        exptectedProbe.setDataType(DataType.BOOL);
        exptectedProbe.setKind(ProbeAdressingKind.MONITORID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleProbeId() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool probeName : id "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("probeName");
        exptectedProbe.setDataType(DataType.BOOL);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleBoolProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition : id "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("condition");
        exptectedProbe.setDataType(DataType.BOOL);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleIntProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int count : id "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("count");
        exptectedProbe.setDataType(DataType.INT);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleFloatVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe float number : id "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("number");
        exptectedProbe.setDataType(DataType.FLOAT);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe string word : id "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe.setName("word");
        exptectedProbe.setDataType(DataType.STRING);
        exptectedProbe.setKind(ProbeAdressingKind.ID);
        exptectedProbe.setIdentifier("someId");
        assertThat(model.getProbes(), contains(samePropertyValuesAs(exptectedProbe)));
    }

    @Test
    public void parseTwoProbes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int count : id "someId";
                probe string word : id "someOtherId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertModelWithoutErrors(model);
        SmodelTestUtil.assertNoValidationIssues(validationTestHelper, model);
        Probe exptectedProbe1 = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe1.setName("count");
        exptectedProbe1.setDataType(DataType.INT);
        exptectedProbe1.setKind(ProbeAdressingKind.ID);
        exptectedProbe1.setIdentifier("someId");
        Probe exptectedProbe2 = SmodelFactory.eINSTANCE.createProbe();
        exptectedProbe2.setName("word");
        exptectedProbe2.setDataType(DataType.STRING);
        exptectedProbe2.setKind(ProbeAdressingKind.ID);
        exptectedProbe2.setIdentifier("someOtherId");
        assertThat(model.getProbes(),
                contains(samePropertyValuesAs(exptectedProbe1), samePropertyValuesAs(exptectedProbe2)));
    }

    @Test
    public void parseProbeWithoutKind() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input '\"someId\"' expecting ':'");
    }

    @Test
    public void parseProbeWithoutIdentified() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition;
                """;

        Smodel model = parserHelper.parse(sb);

        SmodelTestUtil.assertErrorMessages(model, 1, "mismatched input ';' expecting ':'");
    }
}