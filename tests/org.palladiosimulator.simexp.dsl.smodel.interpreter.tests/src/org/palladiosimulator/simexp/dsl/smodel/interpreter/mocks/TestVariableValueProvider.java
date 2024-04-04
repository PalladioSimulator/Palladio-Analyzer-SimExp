package org.palladiosimulator.simexp.dsl.smodel.interpreter.mocks;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;

public class TestVariableValueProvider implements VariableValueProvider {
    @Override
    public Object getValue(Optimizable variable) {
        Bounds bounds = variable.getValues();
        if (bounds instanceof SetBounds) {
            SetBounds array = (SetBounds) bounds;
            // default - return first entry of list
            return getValue(array.getValues()
                .get(0));
        }
        if (bounds instanceof RangeBounds) {
            RangeBounds range = (RangeBounds) bounds;
            // default - return start value
            return getValue(range.getStartValue());
        }
        return null;
    }

    private Object getValue(Expression literal) {
        if (literal instanceof BoolLiteral) {
            BoolLiteral boolLiteral = (BoolLiteral) literal;
            return boolLiteral.isTrue();
        } else if (literal instanceof IntLiteral) {
            IntLiteral intLiteral = (IntLiteral) literal;
            return intLiteral.getValue();
        } else if (literal instanceof DoubleLiteral) {
            DoubleLiteral floatLiteral = (DoubleLiteral) literal;
            return floatLiteral.getValue();
        } else if (literal instanceof StringLiteral) {
            StringLiteral stringLiteral = (StringLiteral) literal;
            return stringLiteral.getValue();
        }

        return null;
    }
}
