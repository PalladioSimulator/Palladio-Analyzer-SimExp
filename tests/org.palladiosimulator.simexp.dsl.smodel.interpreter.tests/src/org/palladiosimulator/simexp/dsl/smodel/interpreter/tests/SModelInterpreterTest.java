package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.RuntimeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mocks.TestProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mocks.TestRuntimeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mocks.TestVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class SModelInterpreterTest {
    public static final String MODEL_NAME_LINE = "modelName = \"name\";";

    private ParseHelper<Smodel> parserHelper;
    private SmodelInterpreter interpreter;
    private VariableValueProvider vvp;
    private ProbeValueProvider pvp;
    private RuntimeValueProvider rvp;

    @Before
    public void setUp() {
        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();
        parserHelper = injector.getInstance(Key.get(new TypeLiteral<ParseHelper<Smodel>>() {
        }));

        pvp = new TestProbeValueProvider();
        vvp = new TestVariableValueProvider();
        rvp = new TestRuntimeValueProvider();
    }

    @Test
    public void testSimpleModel() throws Exception {

        /**
         * sample model: if (true) {
         * 
         * }
         */

        SmodelFactory kmodelFactory = SmodelFactory.eINSTANCE;
        Smodel kmodel = kmodelFactory.createSmodel();
        Constant constant = kmodelFactory.createConstant();
        constant.setName("simpleCondition");
        constant.setDataType(DataType.BOOL);
        kmodel.getConstants()
            .add(constant);
        IfStatement rule = kmodelFactory.createIfStatement();
        Expression condExpr = kmodelFactory.createExpression();
        BoolLiteral boolLiteral = kmodelFactory.createBoolLiteral();
        condExpr.setLiteral(boolLiteral);
        rule.setCondition(condExpr);
        interpreter = new SmodelInterpreter(kmodel, vvp, pvp, rvp);

        boolean actual = interpreter.analyze();

        assertFalse(actual);
    }

    @Test
    public void testAnalyzeWithTrueCondition() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (true) {
                    adapt(parameter=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);

        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);
        Assert.assertTrue(interpreter.analyze());
    }

    @Test
    public void testAnalyzeWithSecondTrueCondition() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (false) {
                    adapt(parameter=1);
                }
                if (true) {
                    adapt(parameter=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        Assert.assertTrue(interpreter.analyze());
    }

    @Test
    public void testAnalyzeWithIfElseStatement() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (false) {} else {
                    adapt(parameter=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        Assert.assertTrue(interpreter.analyze());
    }

    @Test
    public void testAnalyzeWithInnerTrueCondition() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (false) {
                    if (true) {
                        adapt(parameter=1);
                    }
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        Assert.assertFalse(interpreter.analyze());
    }

    @Test
    public void testAnalyzeWithNoTrueCondition() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (false) {
                    adapt(parameter=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        Assert.assertFalse(interpreter.analyze());
    }

    @Test
    public void testAnalyzeWithoutCondition() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        Assert.assertFalse(interpreter.analyze());
    }

    @Test
    public void testPlanWithOneActionCall() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (true) {
                    adapt(parameter=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 1);
    }

    @Test
    public void testPlanWithTwoActionCalls() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                action adapt2(param int parameter);
                if (true) {
                    adapt(parameter=1);
                    adapt2(parameter=2);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(2, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 1);

        Action adapt2 = model.getActions()
            .get(1);
        ResolvedAction resolvedAdapt2 = resolvedActions.get(1);
        assertResolvedAction(adapt2, resolvedAdapt2, 2);
    }

    @Test
    public void testPlanWithTwoActionCallsOfSameAction() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (true) {
                    adapt(parameter=1);
                    adapt(parameter=2);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(2, resolvedActions.size());
        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 1);
        ResolvedAction resolvedAdapt2 = resolvedActions.get(1);
        assertResolvedAction(adapt, resolvedAdapt2, 2);
    }

    @Test
    public void testPlanWithNestedActionCalls() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                action adapt2(param int parameter);
                if (true) {
                    adapt(parameter=2);
                    if (true) {
                        adapt2(parameter=1);
                    }
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(2, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 2);

        Action adapt2 = model.getActions()
            .get(1);
        ResolvedAction resolvedAdapt2 = resolvedActions.get(1);
        assertResolvedAction(adapt2, resolvedAdapt2, 1);
    }

    @Test
    public void testPlanWithIfElseStatement() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (false) {} else {
                    adapt(parameter=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 1);
    }

    @Test
    public void testPlanWithFalseOuterCondition() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                action adapt2(param int parameter);
                if (false) {
                    adapt(parameter=1);
                    if (true) {
                        adapt2(parameter=1);
                    }
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(0, resolvedActions.size());
    }

    @Test
    public void testPlanWithFalseInnerCondition() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                action adapt2(param int parameter);
                if (true) {
                    adapt(parameter=1);
                    if (false) {
                        adapt2(parameter=1);
                    }
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 1);
    }

    @Test
    public void testPlanWithoutActionCalls() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(0, resolvedActions.size());
    }

    @Test
    public void testPlanWithActionWithoutParameters() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt();
                if (true) {
                    adapt();
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt);
    }

    @Test
    public void testPlanWithActionWithSingleParameter() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter);
                if (true) {
                    adapt(parameter=1);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 1);
    }

    @Test
    public void testPlanWithActionWithMultipleParameters() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter, param int parameter2, param int parameter3);
                if (true) {
                    adapt(parameter=1, parameter2=2, parameter3=3);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 1, 2, 3);
    }

    @Test
    public void testPlanWithActionWithSingleVariable() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(var int{5, 4, 3, 2, 1} variable);
                if (true) {
                    adapt();
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 5);
    }

    @Test
    public void testPlanWithActionWithMultipleVariables() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(var int{5, 4, 3, 2, 1} variable, var float{0.1, 0.2, 0.3} variable2);
                if (true) {
                    adapt();
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 5, 0.1f);
    }

    @Test
    public void testPlanWithActionWithParameterAndVariable() throws Exception {
        String sb = MODEL_NAME_LINE + """
                action adapt(param int parameter, var float{0.1, 0.2, 0.3} variable);
                if (true) {
                    adapt(parameter=3);
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(1, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 3, 0.1f);
    }

    @Test
    public void testComplexAnalyzeAndPlan() throws Exception {
        String sb = MODEL_NAME_LINE + """
                const int constant = 1;
                const int anotherConstant = constant * 2;
                optimizable float{1.5, 2.5, 3.5} adaptationFactor;
                probe float someProbe : abc123;
                runtime string someRuntime: simple: a[0].b;

                action adapt(param int parameter, param float factor);
                action adapt2(param float parameter, optimizable float[1,5,1] someRange);
                action adapt3();

                if (someProbe > 0 || someProbe <= -10 && someRuntime == \"string\") {
                    adapt(parameter=anotherConstant, factor=0.5);
                    if (someProbe > 0 && someProbe < 10) {
                        adapt2(parameter=adaptationFactor);
                    }
                }
                if (someProbe == 100) {
                    adapt3();
                }
                """;

        Smodel model = parserHelper.parse(sb);
        interpreter = new SmodelInterpreter(model, vvp, pvp, rvp);

        List<ResolvedAction> resolvedActions = interpreter.plan();
        Assert.assertEquals(2, resolvedActions.size());

        Action adapt = model.getActions()
            .get(0);
        ResolvedAction resolvedAdapt = resolvedActions.get(0);
        assertResolvedAction(adapt, resolvedAdapt, 2, 0.5f);

        Action adapt2 = model.getActions()
            .get(1);
        ResolvedAction resolvedAdapt2 = resolvedActions.get(1);
        assertResolvedAction(adapt2, resolvedAdapt2, 1.5f, 1);
    }

    private void assertResolvedAction(Action action, ResolvedAction resolvedAction, Object... arguments) {
        // Check, if the expected & actual action of the resolved action match.
        Assert.assertEquals(action, resolvedAction.getAction());

        List<Parameter> parameters = action.getParameterList()
            .getParameters();
        List<Optimizable> variables = action.getParameterList()
            .getVariables();
        Map<String, Object> resolvedArguments = resolvedAction.getArguments();

        // Check, if the expected & actual amount of resolved arguments match.
        Assert.assertEquals(parameters.size() + variables.size(), resolvedArguments.size());

        // Check if the expected & actual names & values for parameter arguments match.
        for (int i = 0; i < parameters.size(); i++) {
            Field parameter = parameters.get(i);
            Assert.assertTrue(resolvedArguments.containsKey(parameter.getName()));

            Object value = resolvedArguments.get(parameter.getName());
            Assert.assertEquals(arguments[i], value);
        }

        // Check if the expected & actual names & values for variable arguments match.
        for (int i = 0; i < variables.size(); i++) {
            Field variable = variables.get(i);
            Assert.assertTrue(resolvedArguments.containsKey(variable.getName()));

            Object value = resolvedArguments.get(variable.getName());
            Assert.assertEquals(arguments[i + parameters.size()], value);
        }
    }
}