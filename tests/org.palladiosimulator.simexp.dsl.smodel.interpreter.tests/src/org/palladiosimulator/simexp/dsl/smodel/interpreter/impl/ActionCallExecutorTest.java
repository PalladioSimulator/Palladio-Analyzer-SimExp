package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class ActionCallExecutorTest {

    private ActionCallExecutor executor;

    @Mock
    private IFieldValueProvider fieldValueProvider;
    @Mock
    private IExpressionCalculator exprCalculator;

    private SmodelCreator smodelCreator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        executor = new ActionCallExecutor(exprCalculator, fieldValueProvider);
    }

    @Test
    public void testExecuteActionNoArguments() {
        Action action = smodelCreator.createAction("a");
        ActionCall actionCall = smodelCreator.createActionCall(action);

        ResolvedAction actualResolvedAction = executor.execute(actionCall);

        assertEquals(action, actualResolvedAction.getAction());
        Map<String, Object> actualArgs = actualResolvedAction.getArguments();
        assertEquals(0, actualArgs.size());
    }

    @Test
    public void testExecuteActionWithSingleParameterInt() {
        Action action = smodelCreator.createAction("a");
        ActionCall actionCall = smodelCreator.createActionCall(action);
        ActionArguments actionArguments = action.getArguments();
        Parameter parameter = smodelCreator.createParameter("p", DataType.INT);
        actionArguments.getParameters()
            .add(parameter);
        action.setArguments(actionArguments);
        actionCall.setActionRef(action);
        ParameterValue argumentKeyValue = smodelCreator.createParameterValue(parameter,
                smodelCreator.createIntLiteral(1));
        actionCall.getArguments()
            .add(argumentKeyValue);
        when(exprCalculator.calculateInteger(argumentKeyValue.getArgument())).thenReturn(1);

        ResolvedAction actualResolvedAction = executor.execute(actionCall);

        assertEquals(action, actualResolvedAction.getAction());
        Map<String, Object> actualArgs = actualResolvedAction.getArguments();
        assertTrue(actualArgs.containsKey(parameter.getName()));
        Object actualValue = actualArgs.get(parameter.getName());
        assertEquals(1, actualValue);
    }

    @Test
    public void testExecuteActionWithSingleParameterString() {
        Action action = smodelCreator.createAction("a");
        ActionCall actionCall = smodelCreator.createActionCall(action);
        ActionArguments actionArguments = action.getArguments();
        Parameter parameter = smodelCreator.createParameter("p", DataType.STRING);
        actionArguments.getParameters()
            .add(parameter);
        action.setArguments(actionArguments);
        actionCall.setActionRef(action);
        ParameterValue argumentKeyValue = smodelCreator.createParameterValue(parameter,
                smodelCreator.createStringLiteral("s"));
        actionCall.getArguments()
            .add(argumentKeyValue);
        when(exprCalculator.calculateString(argumentKeyValue.getArgument())).thenReturn("s");

        ResolvedAction actualResolvedAction = executor.execute(actionCall);

        assertEquals(action, actualResolvedAction.getAction());
        Map<String, Object> actualArgs = actualResolvedAction.getArguments();
        assertTrue(actualArgs.containsKey(parameter.getName()));
        Object actualValue = actualArgs.get(parameter.getName());
        assertEquals("s", actualValue);
    }

    @Test
    public void testExecuteActionWithSingleOptimizableInt() {
        Action action = smodelCreator.createAction("a");
        ActionCall actionCall = smodelCreator.createActionCall(action);
        ActionArguments actionArguments = action.getArguments();
        RangeBounds bounds = smodelCreator.createRangeBounds(smodelCreator.createIntLiteral(0),
                smodelCreator.createIntLiteral(1), smodelCreator.createIntLiteral(1));
        Optimizable optimizable = smodelCreator.createOptimizable("o", DataType.INT, bounds);
        actionArguments.getOptimizables()
            .add(optimizable);
        action.setArguments(actionArguments);
        actionCall.setActionRef(action);
        when(fieldValueProvider.getIntegerValue(optimizable)).thenReturn(1);

        ResolvedAction actualResolvedAction = executor.execute(actionCall);

        assertEquals(action, actualResolvedAction.getAction());
        Map<String, Object> actualArgs = actualResolvedAction.getArguments();
        assertTrue(actualArgs.containsKey(optimizable.getName()));
        Object actualValue = actualArgs.get(optimizable.getName());
        assertEquals(1, actualValue);
    }

    @Test
    public void testExecuteActionWithParamAndOptimizableInt() {
        Action action = smodelCreator.createAction("a");
        ActionCall actionCall = smodelCreator.createActionCall(action);
        ActionArguments actionArguments = action.getArguments();
        Parameter parameter = smodelCreator.createParameter("p", DataType.INT);
        actionArguments.getParameters()
            .add(parameter);
        RangeBounds bounds = smodelCreator.createRangeBounds(smodelCreator.createIntLiteral(0),
                smodelCreator.createIntLiteral(1), smodelCreator.createIntLiteral(1));
        Optimizable optimizable = smodelCreator.createOptimizable("o", DataType.INT, bounds);
        actionArguments.getOptimizables()
            .add(optimizable);
        action.setArguments(actionArguments);
        actionCall.setActionRef(action);
        ParameterValue argumentKeyValue = smodelCreator.createParameterValue(parameter,
                smodelCreator.createIntLiteral(1));

        actionCall.getArguments()
            .add(argumentKeyValue);
        when(exprCalculator.calculateInteger(argumentKeyValue.getArgument())).thenReturn(1);
        when(fieldValueProvider.getIntegerValue(optimizable)).thenReturn(2);

        ResolvedAction actualResolvedAction = executor.execute(actionCall);

        assertEquals(action, actualResolvedAction.getAction());
        Map<String, Object> actualArgs = actualResolvedAction.getArguments();
        assertTrue(actualArgs.containsKey(parameter.getName()));
        Object actualValue1 = actualArgs.get(parameter.getName());
        assertEquals(1, actualValue1);
        assertTrue(actualArgs.containsKey(optimizable.getName()));
        Object actualValue2 = actualArgs.get(optimizable.getName());
        assertEquals(2, actualValue2);
    }
}
