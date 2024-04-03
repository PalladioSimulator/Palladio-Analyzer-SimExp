package org.palladiosimulator.simexp.dsl.smodel.util;

import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.VariableAssignment;
import org.palladiosimulator.simexp.dsl.smodel.smodel.util.SmodelSwitch;

public class SmodelDataTypeSwitch extends SmodelSwitch<DataType> {
    @Override
    public DataType caseField(Field field) {
        return field.getDataType();
    }

    @Override
    public DataType caseVariableAssignment(VariableAssignment variableAssignment) {
        return doSwitch(variableAssignment.getValue());
    }

    @Override
    public DataType caseParameter(Parameter parameter) {
        return parameter.getDataType();
    }

    @Override
    public DataType caseBounds(Bounds bounds) {
        return doSwitch(bounds.eContainer());
    }

    @Override
    public DataType caseParameterValue(ParameterValue argument) {
        return doSwitch(argument.getArgument());
    }

    @Override
    public DataType caseBoolLiteral(BoolLiteral literal) {
        return DataType.BOOL;
    }

    @Override
    public DataType caseIntLiteral(IntLiteral literal) {
        return DataType.INT;
    }

    @Override
    public DataType caseDoubleLiteral(DoubleLiteral literal) {
        return DataType.DOUBLE;
    }

    @Override
    public DataType caseStringLiteral(StringLiteral literal) {
        return DataType.STRING;
    }

    @Override
    public DataType caseExpression(Expression expression) {
        Field field = expression.getFieldRef();
        if (field != null) {
            if (field.eContainer() == null) {
                return null;
            }
            return doSwitch(field);
        }

        Literal literal = expression.getLiteral();
        if (literal != null) {
            return doSwitch(literal);
        }

        Operation operation = expression.getOp();

        DataType leftType = null;
        if (expression.getLeft() != null) {
            leftType = doSwitch(expression.getLeft());
        }

        DataType rightType = null;
        if (expression.getRight() != null) {
            rightType = doSwitch(expression.getRight());
        }

        switch (operation) {
        case UNDEFINED: {
            if (leftType != null) {
                return leftType;
            }
            return null;
        }

        case OR:
        case AND:
        case NOT:
        case EQUAL:
        case UNEQUAL:
        case SMALLER:
        case SMALLER_OR_EQUAL:
        case GREATER_OR_EQUAL:
        case GREATER:
            return DataType.BOOL;

        case PLUS:
        case MINUS:
        case MULTIPLY:
            if (leftType == DataType.DOUBLE || rightType == DataType.DOUBLE) {
                return DataType.DOUBLE;
            } else {
                return DataType.INT;
            }

        case DIVIDE:
            return DataType.DOUBLE;

        case MODULO:
            if (leftType == DataType.DOUBLE || rightType == DataType.DOUBLE) {
                return DataType.DOUBLE;
            } else {
                return DataType.INT;
            }

        default:
            throw new RuntimeException(
                    "Couldn't determine the datatype of an expression with operation '" + operation + "'.");
        }
    }
}
