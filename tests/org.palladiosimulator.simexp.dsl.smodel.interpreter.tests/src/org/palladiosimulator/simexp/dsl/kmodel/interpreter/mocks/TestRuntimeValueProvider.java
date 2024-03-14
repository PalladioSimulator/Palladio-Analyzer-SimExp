package org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks;

import org.palladiosimulator.simexp.dsl.kmodel.interpreter.RuntimeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Runtime;

public class TestRuntimeValueProvider implements RuntimeValueProvider {
    @Override
    public Object getValue(Runtime runtime) {
        switch (runtime.getDataType()) {
        case BOOL:
            return true;

        case INT:
            return 1;

        case FLOAT:
            return 0.12345;

        case STRING:
            return "string";

        default:
            return null;
        }
    }
}
