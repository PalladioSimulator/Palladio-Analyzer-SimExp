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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelFieldParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseAllDifferentFieldTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true, false} condition;
                const int one = 1;
                probe double aliasName : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("condition", DataType.BOOL, smodelCreator
            .createSetBounds(smodelCreator.createBoolLiteral(true), smodelCreator.createBoolLiteral(false)));
        assertThat(model.getOptimizables()).containsExactlyInAnyOrder(expectedOptimizable);
        Constant expectedConstant = smodelCreator.createConstant("one", DataType.INT,
                smodelCreator.createIntLiteral(1));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
        Probe expectedProbe = smodelCreator.createProbe("aliasName", DataType.DOUBLE, ProbeAdressingKind.ID, "someId");
        assertThat(model.getProbes()).containsExactlyInAnyOrder(expectedProbe);
    }

    @Test
    public void parseFieldWithInvalidName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int 123 = 123;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input '123' expecting RULE_ID");
    }

    @Test
    public void parseFieldWithTokenAsName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int const = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input 'const' expecting RULE_ID");
        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "no viable alternative at input '='");
        validationTestHelper.assertError(model, SmodelPackage.Literals.SMODEL, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input '<EOF>' expecting RULE_ID");
    }

    @Test
    public void parseSameFieldsWithSameName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int number = 1;
                const int number = 2;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null, 29, 6,
                "Duplicate Field 'number'");
        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null, 51, 6,
                "Duplicate Field 'number'");
    }

    @Test
    public void parseDifferentFieldsWithSameName() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{"word"} word;
                probe string word : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.OPTIMIZABLE, null, "Duplicate Field 'word'");
        validationTestHelper.assertError(model, SmodelPackage.Literals.PROBE, null, "Duplicate Field 'word'");
    }

    @Test
    public void parseLocalField() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                if(true){
                    optimizable int variable = {1};
                }
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.IF_STATEMENT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input 'optimizable' expecting '}'");
    }
}
