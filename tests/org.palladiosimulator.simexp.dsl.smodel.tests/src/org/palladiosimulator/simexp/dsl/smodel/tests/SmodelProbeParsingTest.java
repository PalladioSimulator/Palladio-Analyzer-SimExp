package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.palladiosimulator.simexp.dsl.smodel.test.util.EcoreAssert.assertThat;

import javax.inject.Inject;

import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelProbeParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseSingleProbeMonitorId() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool probeName : monitorId = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Probe exptectedProbe = smodelCreator.createProbe("probeName", DataType.BOOL, ProbeAdressingKind.MONITORID,
                "someId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(exptectedProbe);
    }

    @Test
    public void parseSingleProbeId() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool probeName : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Probe exptectedProbe = smodelCreator.createProbe("probeName", DataType.BOOL, ProbeAdressingKind.ID, "someId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(exptectedProbe);
    }

    @Test
    public void parseSingleBoolProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Probe exptectedProbe = smodelCreator.createProbe("condition", DataType.BOOL, ProbeAdressingKind.ID, "someId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(exptectedProbe);
    }

    @Test
    public void parseSingleIntProbe() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int count : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Probe exptectedProbe = smodelCreator.createProbe("count", DataType.INT, ProbeAdressingKind.ID, "someId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(exptectedProbe);
    }

    @Test
    public void parseSingleDoubleVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe double number : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Probe exptectedProbe = smodelCreator.createProbe("number", DataType.DOUBLE, ProbeAdressingKind.ID, "someId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(exptectedProbe);
    }

    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe string word : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Probe exptectedProbe = smodelCreator.createProbe("word", DataType.STRING, ProbeAdressingKind.ID, "someId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(exptectedProbe);
    }

    @Test
    public void parseTwoProbes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int count : id = "someId";
                probe string word : id = "someOtherId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Probe exptectedProbe1 = smodelCreator.createProbe("count", DataType.INT, ProbeAdressingKind.ID, "someId");
        Probe exptectedProbe2 = smodelCreator.createProbe("word", DataType.STRING, ProbeAdressingKind.ID,
                "someOtherId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(exptectedProbe1, exptectedProbe2);
    }

    @Test
    public void parseUnusedInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int one : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertWarning(model, SmodelPackage.Literals.SMODEL, null,
                "The Probe 'one' is never used.");
    }

    @Test
    public void parseProbeWithoutKind() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.PROBE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input '\"someId\"' expecting ':'");
    }

    @Test
    public void parseProbeWithoutIdentified() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool condition;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.PROBE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting ':'");
    }

    @Test
    public void parseBoolProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe bool pName: id = "ab11" = true;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.PROBE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseIntProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int pName: id = "ab11" = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.PROBE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseDoubleProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe double pName: id = "ab11" = 1.0;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.PROBE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input '=' expecting ';'");
    }

    @Test
    public void parseStringProbeAssignment() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe string pName: id = "ab11" = "s";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.PROBE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input '=' expecting ';'");
    }
}
