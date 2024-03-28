package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Array;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Range;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;

public class DummyVariableValueProvider implements VariableValueProvider {

    @Override
    public Object getValue(Optimizable variable) {
        Bounds bounds = variable.getValues();
        if (bounds instanceof Array) {
            Array array = (Array) bounds;
            // default - return first entry of list
            return getValue(array.getValues()
                .get(0));
        }
        if (bounds instanceof Range) {
            Range range = (Range) bounds;
            // default - return start value
            return getValue(range.getStartValue());
        }
        return null;
    }

    private Object getValue(Literal literal) {
        if (literal instanceof BoolLiteral) {
            return ((BoolLiteral) literal).isTrue();
        }

        if (literal instanceof IntLiteral) {
            return ((IntLiteral) literal).getValue();
        }

        if (literal instanceof FloatLiteral) {
            return ((FloatLiteral) literal).getValue();
        }

        if (literal instanceof StringLiteral) {
            return ((StringLiteral) literal).getValue();
        }

        return null;
    }

}
