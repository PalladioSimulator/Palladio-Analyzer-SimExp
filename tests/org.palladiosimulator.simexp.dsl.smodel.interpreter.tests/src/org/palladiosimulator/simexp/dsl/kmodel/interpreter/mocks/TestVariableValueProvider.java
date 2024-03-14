package org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks;

import org.palladiosimulator.simexp.dsl.kmodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Array;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Range;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;

public class TestVariableValueProvider implements VariableValueProvider {
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
            BoolLiteral boolLiteral = (BoolLiteral) literal;
            return boolLiteral.isTrue();
        } else if (literal instanceof IntLiteral) {
            IntLiteral intLiteral = (IntLiteral) literal;
            return intLiteral.getValue();
        } else if (literal instanceof FloatLiteral) {
            FloatLiteral floatLiteral = (FloatLiteral) literal;
            return floatLiteral.getValue();
        } else if (literal instanceof StringLiteral) {
            StringLiteral stringLiteral = (StringLiteral) literal;
            return stringLiteral.getValue();
        }

        return null;
    }
}
