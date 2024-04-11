package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.ActionCallExecutor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;

public class ActionCallExecutorTest {

    private final SmodelFactory smodelFactory = SmodelFactory.eINSTANCE;

    private ActionCallExecutor executor;

    @Mock
    private IFieldValueProvider fieldValueProvider;
    @Mock
    private ExpressionCalculator exprCalculator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        executor = new ActionCallExecutor(exprCalculator, fieldValueProvider);
    }

    @Test
    public void testExecuteActionNoArguments() {
        ActionCall actionCall = smodelFactory.createActionCall();
        Action action = smodelFactory.createAction();
        action.setName("a");
        actionCall.setActionRef(action);
        ActionArguments ActionArguments = smodelFactory.createActionArguments();
        action.setArguments(ActionArguments);

        ResolvedAction actualResolvedAction = executor.execute(actionCall);

        assertEquals(action, actualResolvedAction.getAction());
        Map<String, Object> actualArgs = actualResolvedAction.getArguments();
        assertEquals(0, actualArgs.size());
    }

    @Test
    public void testExecuteActionWithSingleParameterInt() {
        ActionCall actionCall = smodelFactory.createActionCall();
        Action action = smodelFactory.createAction();
        action.setName("a");
        ActionArguments ActionArguments = smodelFactory.createActionArguments();
        Parameter parameter = smodelFactory.createParameter();
        parameter.setName("p");
        parameter.setDataType(DataType.INT);
        ActionArguments.getParameters()
            .add(parameter);
        action.setArguments(ActionArguments);
        actionCall.setActionRef(action);
        ParameterValue argumentKeyValue = smodelFactory.createParameterValue();
        argumentKeyValue.setParamRef(parameter);
        Expression expression = smodelFactory.createExpression();
        IntLiteral literal = createIntLiteral(1);
        expression.setLiteral(literal);
        argumentKeyValue.setArgument(expression);
        actionCall.getArguments()
            .add(argumentKeyValue);
        when(exprCalculator.calculateInteger(expression)).thenReturn(literal.getValue());

        ResolvedAction actualResolvedAction = executor.execute(actionCall);

        assertEquals(action, actualResolvedAction.getAction());
        Map<String, Object> actualArgs = actualResolvedAction.getArguments();
        assertTrue(actualArgs.containsKey(parameter.getName()));
        Object actualValue = actualArgs.get(parameter.getName());
        assertEquals(1, actualValue);
    }

    @Test
    public void testExecuteActionWithSingleOptimizableInt() {
        ActionCall actionCall = smodelFactory.createActionCall();
        Action action = smodelFactory.createAction();
        action.setName("a");
        ActionArguments ActionArguments = smodelFactory.createActionArguments();
        Optimizable optimizable = smodelFactory.createOptimizable();
        optimizable.setName("o");
        optimizable.setDataType(DataType.INT);
        RangeBounds bounds = smodelFactory.createRangeBounds();
        IntLiteral startLiteral = createIntLiteral(0);
        bounds.setStartValue(startLiteral);
        IntLiteral endLiteral = createIntLiteral(1);
        bounds.setEndValue(endLiteral);
        optimizable.setValues(bounds);
        ActionArguments.getOptimizables()
            .add(optimizable);
        action.setArguments(ActionArguments);
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
        ActionCall actionCall = smodelFactory.createActionCall();
        Action action = smodelFactory.createAction();
        action.setName("a");
        ActionArguments ActionArguments = smodelFactory.createActionArguments();
        Parameter parameter = smodelFactory.createParameter();
        parameter.setName("p");
        parameter.setDataType(DataType.INT);
        ActionArguments.getParameters()
            .add(parameter);
        Optimizable optimizable = smodelFactory.createOptimizable();
        optimizable.setName("o");
        optimizable.setDataType(DataType.INT);
        RangeBounds bounds = smodelFactory.createRangeBounds();
        IntLiteral startLiteral = createIntLiteral(0);
        bounds.setStartValue(startLiteral);
        IntLiteral endLiteral = createIntLiteral(1);
        bounds.setEndValue(endLiteral);
        optimizable.setValues(bounds);
        ActionArguments.getOptimizables()
            .add(optimizable);
        action.setArguments(ActionArguments);
        actionCall.setActionRef(action);
        ParameterValue argumentKeyValue = smodelFactory.createParameterValue();
        argumentKeyValue.setParamRef(parameter);
        Expression expression = smodelFactory.createExpression();
        IntLiteral literal = createIntLiteral(1);
        expression.setLiteral(literal);
        argumentKeyValue.setArgument(expression);
        actionCall.getArguments()
            .add(argumentKeyValue);
        when(exprCalculator.calculateInteger(expression)).thenReturn(literal.getValue());
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

    private IntLiteral createIntLiteral(int value) {
        IntLiteral literal = smodelFactory.createIntLiteral();
        literal.setValue(value);
        return literal;
    }

}
