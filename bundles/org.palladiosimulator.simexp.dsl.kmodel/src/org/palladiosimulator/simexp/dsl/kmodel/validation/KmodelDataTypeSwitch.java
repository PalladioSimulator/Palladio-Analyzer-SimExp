package org.palladiosimulator.simexp.dsl.kmodel.validation;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ArgumentKeyValue;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Bounds;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Operation;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.util.KmodelSwitch;

public class KmodelDataTypeSwitch extends KmodelSwitch<DataType> {
    
    @Override
    public DataType doSwitch(EObject object) {
        if (object == null) {
            throw new RuntimeException("Couldn't determine the datatype of an object that is null.");
        }
        
        DataType result = super.doSwitch(object);
        
        if (result == null) {
            throw new RuntimeException("Couldn't determine the datatype of objects of class '" + object.eClass().getName() + "'.");
        }
        
        return result;
    }
    
    @Override
    public DataType caseField(Field field) {
        return field.getDataType();
    }
    
    @Override
    public DataType caseBounds(Bounds bounds) {
        return doSwitch(bounds.eContainer());
    }
    
    @Override
    public DataType caseArgumentKeyValue(ArgumentKeyValue argument) {
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
    public DataType caseFloatLiteral(FloatLiteral literal) {
        return DataType.FLOAT;
    }
    
    @Override
    public DataType caseStringLiteral(StringLiteral literal) {
        return DataType.STRING;
    }
    
    @Override
    public DataType caseExpression(Expression expression) {
        Field field = expression.getFieldRef();
        if (field != null) {
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
        
        switch(operation) {
        case UNDEFINED:
            return leftType != null ? leftType : DataType.UNDEFINED;
            
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
            if (leftType == DataType.FLOAT || rightType == DataType.FLOAT) {
                return DataType.FLOAT;
            } else {
                return DataType.INT;
            }
            
        case DIVIDE:
            return DataType.FLOAT;  
        
        case MODULO:
            if (leftType == DataType.FLOAT || rightType == DataType.FLOAT) {
                return DataType.FLOAT;
            } else {
                return DataType.INT;
            }
            
        default:
            throw new RuntimeException("Couldn't determine the datatype of an expression with operation '" + operation + "'.");
        }
    }
}

