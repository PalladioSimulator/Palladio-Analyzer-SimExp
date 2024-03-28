package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ArgumentKeyValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Runtime;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.util.SmodelSwitch;

public class SmodelValueSwitch extends SmodelSwitch<Object> {

    private final VariableValueProvider vvp;
    // private final ProbeValueProvider pvp;
    // private final RuntimeValueProvider rvp;
    private final Map<Constant, Object> resolvedConstants;

    public SmodelValueSwitch(
            VariableValueProvider vvp/* , ProbeValueProvider pvp, RuntimeValueProvider rvp */) {
        this.vvp = vvp;
        // this.pvp = pvp;
        // this.rvp = rvp;
        this.resolvedConstants = new HashMap<>();
    }

    @Override
    public Object doSwitch(EObject object) {
        if (object == null) {
            throw new RuntimeException("Couldn't determine the value of an object that is null.");
        }

        Object result = super.doSwitch(object);

        if (result == null) {
            throw new RuntimeException("Couldn't determine the value of objects of class '" + object.eClass()
                .getName() + "'.");
        }

        return result;
    }

    @Override
    public Object caseConstant(Constant constant) {
        Object value = resolvedConstants.get(constant);

        if (value == null) {
            value = doSwitch(constant.getValue());

            if (constant.getDataType() == DataType.INT) {
                value = ((Number) value).intValue();
            }

            resolvedConstants.put(constant, value);
        }

        return value;
    }

    @Override
    public Object caseOptimizable(Optimizable variable) {
        Object value = vvp.getValue(variable);

        if (value == null) {
            throw new RuntimeException(
                    "The VariableValueProvider couldn't provide a value for variable '" + variable.getName() + "'.");
        }

        return value;
    }

    @Override
    public Object caseProbe(Probe probe) {
        Object value = null;
        if (DataType.FLOAT == probe.getDataType()) {
            // value = pvp.getDoubleValue(probe);
        }
        if (DataType.BOOL == probe.getDataType()) {
            // value = pvp.getBooleanValue(probe);
        }

        if (value == null) {
            throw new RuntimeException(
                    "The ProbeValueProvider couldn't provide a value for probe '" + probe.getName() + "'.");
        }

        return value;
    }

    @Override
    public Object caseRuntime(Runtime runtime) {
        /*
         * Object value = rvp.getValue(runtime);
         * 
         * if (value == null) { throw new RuntimeException(
         * "The RuntimeValueProvider couldn't provide a value for runtime '" + runtime.getName() +
         * "'."); }
         * 
         * return value;
         */
        return null;
    }

    @Override
    public Object caseArgumentKeyValue(ArgumentKeyValue argument) {
        Object value = doSwitch(argument.getArgument());

        Parameter parameter = argument.getParamRef();
        if (parameter.getDataType() == DataType.INT) {
            value = ((Number) value).intValue();
        }

        return value;
    }

    @Override
    public Boolean caseBoolLiteral(BoolLiteral literal) {
        return literal.isTrue();
    }

    @Override
    public Integer caseIntLiteral(IntLiteral literal) {
        return literal.getValue();
    }

    @Override
    public Float caseFloatLiteral(FloatLiteral literal) {
        return literal.getValue();
    }

    @Override
    public String caseStringLiteral(StringLiteral literal) {
        return literal.getValue();
    }

    @Override
    public Object caseExpression(Expression expression) {
        Field field = expression.getFieldRef();
        if (field != null) {
            return doSwitch(field);
        }

        Literal literal = expression.getLiteral();
        if (literal != null) {
            return doSwitch(literal);
        }

        Operation operation = expression.getOp();

        Object leftValue = null;
        if (expression.getLeft() != null) {
            leftValue = doSwitch(expression.getLeft());
        }

        Object rightValue = null;
        if (expression.getRight() != null) {
            rightValue = doSwitch(expression.getRight());
        }

        switch (operation) {
        case UNDEFINED:
            return leftValue;

        case OR:
            return (Boolean) leftValue || (Boolean) rightValue;

        case AND:
            return (Boolean) leftValue && (Boolean) rightValue;

        case NOT:
            return !(Boolean) leftValue;

        case EQUAL:
            return leftValue.equals(rightValue);

        case UNEQUAL:
            return !leftValue.equals(rightValue);

        case SMALLER:
            return ((Number) leftValue).floatValue() < ((Number) rightValue).floatValue();

        case SMALLER_OR_EQUAL:
            return ((Number) leftValue).floatValue() <= ((Number) rightValue).floatValue();

        case GREATER_OR_EQUAL:
            return ((Number) leftValue).floatValue() >= ((Number) rightValue).floatValue();

        case GREATER:
            return ((Number) leftValue).floatValue() > ((Number) rightValue).floatValue();

        case PLUS:
            if (rightValue == null) {
                return +((Number) leftValue).floatValue();

            } else {
                return ((Number) leftValue).floatValue() + ((Number) rightValue).floatValue();
            }

        case MINUS:
            if (rightValue == null) {
                return -((Number) leftValue).floatValue();

            } else {
                return ((Number) leftValue).floatValue() - ((Number) rightValue).floatValue();
            }

        case MULTIPLY:
            return ((Number) leftValue).floatValue() * ((Number) rightValue).floatValue();

        case DIVIDE:
            return ((Number) leftValue).floatValue() / ((Number) rightValue).floatValue();

        case MODULO:
            return ((Number) leftValue).floatValue() % ((Number) rightValue).floatValue();

        default:
            throw new RuntimeException(
                    "Couldn't determine the value of an expression with operation '" + operation + "'.");
        }
    }
}
