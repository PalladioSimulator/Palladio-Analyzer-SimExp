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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelConstantParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseSingleBoolConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool condition = true;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("condition", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseSingleIntConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int one = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("one", DataType.INT,
                smodelCreator.createIntLiteral(1));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseSingleDoubleConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const double one = 1.0;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("one", DataType.DOUBLE,
                smodelCreator.createDoubleLiteral(1.0));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseSingleStringConstant() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const string word = "word";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant = smodelCreator.createConstant("word", DataType.STRING,
                smodelCreator.createStringLiteral("word"));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant);
    }

    @Test
    public void parseTwoConstants() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int count = 1;
                const string word = "word";
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        Constant expectedConstant1 = smodelCreator.createConstant("count", DataType.INT,
                smodelCreator.createIntLiteral(1));
        Constant expectedConstant2 = smodelCreator.createConstant("word", DataType.STRING,
                smodelCreator.createStringLiteral("word"));
        assertThat(model.getConstants()).containsExactlyInAnyOrder(expectedConstant1, expectedConstant2);
    }

    @Test
    public void parseConstantWithConstantValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int const1 = 1;
                const int const2 = const1;
                if (const2 == 0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoErrors(model);
        validationTestHelper.assertWarning(model, SmodelPackage.Literals.CONSTANT, null,
                "Constant 'const2' is probably redundant.");
        Constant exptectedConstant1 = smodelCreator.createConstant("const1", DataType.INT,
                smodelCreator.createIntLiteral(1));
        Constant exptectedConstant2 = SmodelFactory.eINSTANCE.createConstant();
        exptectedConstant2.setName("const2");
        exptectedConstant2.setDataType(DataType.INT);
        Expression expectedExpression2 = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression2.setFieldRef(exptectedConstant1);
        exptectedConstant2.setValue(expectedExpression2);
        assertThat(model.getConstants()).containsExactlyInAnyOrder(exptectedConstant1, exptectedConstant2);
    }

    @Test
    public void parseConstantWithConstantValueComputed() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1;
                const int anotherConstant = constant * 2;
                if (anotherConstant == 0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
    }

    @Test
    public void parseUnusedInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int one = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertWarning(model, SmodelPackage.Literals.SMODEL, null,
                "The Constant 'one' is never used.");
    }

    @Test
    public void parseBoolConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseIntConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseFloatConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const double noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseStringConstantWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const string noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseBoolConstantWithIntValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool number = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null,
                "Expected a value of type 'bool', got 'int' instead.");
    }

    @Test
    public void parseIntConstantWithBoolValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int number = true;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null,
                "Expected a value of type 'int', got 'bool' instead.");
    }

    @Test
    public void parseConstantWithOptimizableValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{0} variable;
                const int constant = variable;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithProbeValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int someProbe : id = "someId";
                const int constant = someProbe;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithVariableValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int variable = 1;
                const int constant = variable;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithExpressionContainingVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true} variable;
                const bool constant = variable && true;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null,
                "Cannot assign an expression containing a non-constant value to a constant.");
    }

    @Test
    public void parseConstantWithSelfReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const bool constant = false || constant;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null, "Cyclic reference detected.");
    }

    @Test
    public void parseConstantsWithCyclicReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int const1 = const2;
                const int const2 = const1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.CONSTANT, null, "Cyclic reference detected.",
                "Cyclic reference detected.");
    }
}
