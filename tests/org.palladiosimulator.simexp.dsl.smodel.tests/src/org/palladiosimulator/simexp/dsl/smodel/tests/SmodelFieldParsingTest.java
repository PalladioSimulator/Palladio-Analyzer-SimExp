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
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelFieldParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;

    @Inject
    private ValidationTestHelper validationTestHelper;

    @Test
    public void parseAllDifferentFieldTypes() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true, false} condition;
                const int one = 1;
                probe double aliasName : id = "someId";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Optimizable expectedOptimizable = createOptimizable("condition", DataType.BOOL,
                createSetBoundsBool(createBoolLiteral(true), createBoolLiteral(false)));
        assertThat(model.getOptimizables()).containsExactlyInAnyOrder(expectedOptimizable);
        Constant expectedConstant = createConstant("one", DataType.INT, createIntLiteral(1));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
        Probe expectedProbe = createProbe("aliasName", DataType.DOUBLE, ProbeAdressingKind.ID, "someId");
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

    private Probe createProbe(String name, DataType type, ProbeAdressingKind kind, String id) {
        Probe probe = SmodelFactory.eINSTANCE.createProbe();
        probe.setName(name);
        probe.setDataType(type);
        probe.setKind(kind);
        probe.setIdentifier(id);
        return probe;
    }

    private SetBounds createSetBoundsBool(Literal... values) {
        SetBounds bounds = SmodelFactory.eINSTANCE.createSetBounds();
        for (Literal value : values) {
            bounds.getValues()
                .add(value);
        }
        return bounds;
    }

    private Optimizable createOptimizable(String name, DataType type, Bounds bounds) {
        Optimizable optimizable = SmodelFactory.eINSTANCE.createOptimizable();
        optimizable.setName(name);
        optimizable.setDataType(type);
        optimizable.setValues(bounds);
        return optimizable;
    }

    private IntLiteral createIntLiteral(int value) {
        IntLiteral literal = SmodelFactory.eINSTANCE.createIntLiteral();
        literal.setValue(value);
        return literal;
    }

    private BoolLiteral createBoolLiteral(boolean value) {
        BoolLiteral literal = SmodelFactory.eINSTANCE.createBoolLiteral();
        literal.setTrue(value);
        return literal;
    }

    private Constant createConstant(String name, DataType type, Literal literal) {
        Constant constant = SmodelFactory.eINSTANCE.createConstant();
        constant.setName(name);
        constant.setDataType(type);
        Expression expression = SmodelFactory.eINSTANCE.createExpression();
        expression.setLiteral(literal);
        constant.setValue(expression);
        return constant;
    }
}
