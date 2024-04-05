package org.palladiosimulator.simexp.dsl.smodel.tests;

import static org.palladiosimulator.simexp.dsl.smodel.test.util.EcoreAssert.assertThat;

import javax.inject.Inject;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelPackage;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelInjectorProvider;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelTestUtil;

@RunWith(XtextRunner.class)
@InjectWith(SmodelInjectorProvider.class)
public class SmodelOptimizableParsingTest {
    @Inject
    private ParseHelper<Smodel> parserHelper;
    @Inject
    private ValidationTestHelper validationTestHelper;
    @Inject
    private SmodelCreator smodelCreator;

    @Test
    public void parseSingleBoolVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true} condition;
                if (condition) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createBoolLiteral(true));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("condition", DataType.BOOL, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseSingleIntVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{1} count;
                if (count==0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createIntLiteral(1));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("count", DataType.INT, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseSingleDoubleVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable double{1.0} number;
                if (number==0.0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createDoubleLiteral(1.0));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("number", DataType.DOUBLE, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseSingleStringVariable() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{"word"} word;
                if (word=="") {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createStringLiteral("word"));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("word", DataType.STRING, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseTwoVariables() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{1} count;
                optimizable string{"word"} word;
                if (count==0 && word=="") {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds1 = smodelCreator.createSetBounds(smodelCreator.createIntLiteral(1));
        Optimizable expectedOptimizable1 = smodelCreator.createOptimizable("count", DataType.INT, expectedBounds1);
        SetBounds expectedBounds2 = smodelCreator.createSetBounds(smodelCreator.createStringLiteral("word"));
        Optimizable expectedOptimizable2 = smodelCreator.createOptimizable("word", DataType.STRING, expectedBounds2);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable1, expectedOptimizable2);
    }

    @Test
    public void parseBoolVarSet() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool{true,false} vName;
                if (vName) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createBoolLiteral(true),
                smodelCreator.createBoolLiteral(false));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("vName", DataType.BOOL, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseIntVarSet() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int{1,3} vName;
                if (vName==0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createIntLiteral(1),
                smodelCreator.createIntLiteral(3));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("vName", DataType.INT, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseDoubleVarSet() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable double{1.0,3.0} vName;
                if (vName==0.0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createDoubleLiteral(1.0),
                smodelCreator.createDoubleLiteral(3.0));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("vName", DataType.DOUBLE, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseStringVarSet() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{"s1","s2"} vName;
                if (vName=="") {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        SetBounds expectedBounds = smodelCreator.createSetBounds(smodelCreator.createStringLiteral("s1"),
                smodelCreator.createStringLiteral("s2"));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("vName", DataType.STRING, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseIntVarRange() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable int[1,2,1] vName;
                if (vName==0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        RangeBounds expectedBounds = smodelCreator.createRangeBounds(smodelCreator.createIntLiteral(1),
                smodelCreator.createIntLiteral(2), smodelCreator.createIntLiteral(1));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("vName", DataType.INT, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseVariableWithValueRange() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable double[1.0, 2.0, 0.1] values;
                if (values==0.0) {}
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertNoIssues(model);
        RangeBounds expectedBounds = smodelCreator.createRangeBounds(smodelCreator.createDoubleLiteral(1.0),
                smodelCreator.createDoubleLiteral(2.0), smodelCreator.createDoubleLiteral(0.1));
        Optimizable expectedOptimizable = smodelCreator.createOptimizable("values", DataType.DOUBLE, expectedBounds);
        assertThat(model.getOptimizables()).containsExactly(expectedOptimizable);
    }

    @Test
    public void parseVariableWithWrongValue() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable string{true} list;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.SET_BOUNDS, null,
                "Expected a value of type 'string', got 'bool' instead.");
    }

    @Test
    public void parseNonNumberVariableWithRange() throws Exception {
        String sb = SmodelTestUtil.MODEL_NAME_LINE + """
                optimizable bool[true, false, true] range;
                """;

        Smodel model = parserHelper.parse(sb);

        validationTestHelper.assertError(model, SmodelPackage.Literals.OPTIMIZABLE, null,
                "Cannot assign a range to a variable of the type 'bool'.");
    }
}
