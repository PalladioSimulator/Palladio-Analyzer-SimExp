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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelVariableParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseSingleBool() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var bool condition = true;
                if (condition) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Variable expectedVariable = smodelCreator.createVariable("condition", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        assertThat(model.getVariables()).containsExactlyInAnyOrder(expectedVariable);
    }

    @Test
    public void parseSingleInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int one = 1;
                if (one == 0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Variable expectedVariable = smodelCreator.createVariable("one", DataType.INT,
                smodelCreator.createIntLiteral(1));
        assertThat(model.getVariables()).containsExactlyInAnyOrder(expectedVariable);
    }

    @Test
    public void parseSingleDouble() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var double one = 1.0;
                if (one == 0.0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Variable expectedVariable = smodelCreator.createVariable("one", DataType.DOUBLE,
                smodelCreator.createDoubleLiteral(1.0));
        assertThat(model.getVariables()).containsExactlyInAnyOrder(expectedVariable);
    }

    @Test
    public void parseSingleString() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var string word = "word";
                if (word == "") {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Variable expectedVariable = smodelCreator.createVariable("word", DataType.STRING,
                smodelCreator.createStringLiteral("word"));
        assertThat(model.getVariables()).containsExactlyInAnyOrder(expectedVariable);
    }

    @Test
    public void parseTwo() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int count = 1;
                var string word = "word";
                if (word == "" || count == 0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Variable expectedVariable1 = smodelCreator.createVariable("count", DataType.INT,
                smodelCreator.createIntLiteral(1));
        Variable expectedVariable2 = smodelCreator.createVariable("word", DataType.STRING,
                smodelCreator.createStringLiteral("word"));
        assertThat(model.getVariables()).containsExactlyInAnyOrder(expectedVariable1, expectedVariable2);
    }

    @Test
    public void parseWithConstantValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1;
                var int variable = constant;
                if (variable == 0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        Constant exptectedConstant = smodelCreator.createConstant("constant", DataType.INT,
                smodelCreator.createIntLiteral(1));
        Variable expectedVariable = smodelCreator.createVariable("variable", DataType.INT, null);
        Expression expectedLiteralExpression = SmodelFactory.eINSTANCE.createExpression();
        expectedLiteralExpression.setFieldRef(exptectedConstant);
        expectedVariable.setValue(expectedLiteralExpression);
        assertThat(model.getVariables()).containsExactlyInAnyOrder(expectedVariable);
    }

    @Test
    public void parseWithConstantValueComputed() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                const int constant = 1;
                var int variable = constant * 2;
                if (variable == 0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
    }

    @Test
    public void parseUnusedInt() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int one = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertWarning(model, SmodelPackage.Literals.SMODEL, null,
                "The Variable 'one' is never used.");
    }

    @Test
    public void parseBoolWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var bool noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseIntWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseDoubleWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var double noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseStringWithoutValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var string noValue;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, Diagnostic.SYNTAX_DIAGNOSTIC,
                "mismatched input ';' expecting '='");
    }

    @Test
    public void parseBoolWithIntValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var bool number = 1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null,
                "Expected a value of type 'bool', got 'int' instead.");
    }

    @Test
    public void parseIntWithBoolValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int number = true;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null,
                "Expected a value of type 'int', got 'bool' instead.");
    }

    @Test
    public void parseWithOptimizableValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{0} opti;
                var int variable = opti;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null,
                "Cannot assign an expression containing a non-constant value to an variable.");
    }

    @Test
    public void parseWithProbeValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                probe int someProbe : id = "someId";
                var int variable = someProbe;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null,
                "Cannot assign an expression containing a non-constant value to an variable.");
    }

    @Test
    public void parseWithExpressionContainingOptimizable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true} opti;
                var bool variable = opti && true;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null,
                "Cannot assign an expression containing a non-constant value to an variable.");
    }

    @Test
    public void parseWithSelfReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var bool variable = false || variable;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null,
                "Cannot assign an expression containing a non-constant value to an variable.");
    }

    @Test
    public void parseWithCyclicReference() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                var int const1 = const2;
                var int const2 = const1;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null, 36, 6,
                "Cannot assign an expression containing a non-constant value to an variable.");
        validationTestHelper.assertError(model, SmodelPackage.Literals.VARIABLE, null, 61, 6,
                "Cannot assign an expression containing a non-constant value to an variable.");
    }
}
