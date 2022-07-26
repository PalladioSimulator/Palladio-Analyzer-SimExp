package org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Array;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Bounds;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Range;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

public class TestVariableValueProvider implements VariableValueProvider {

    @Override
    public Object getValue(Variable variable) {
        Bounds bounds = variable.getValues();
        if (bounds instanceof Array) {
            Array array = (Array) bounds;
            // default - return first entry of list
            return array.getValues().get(0);
        }
        if (bounds instanceof Range) {
            Range range = (Range) bounds;
            // default - return start value
            return range.getStartValue();
        }
        return null;
    }

}
