package org.palladiosimulator.simexp.dsl.smodel.interpreter;

public class DefaultSmodelConfig implements ISmodelConfig {
    @Override
    public double getEpsilon() {
        return 0.0001;
    }
}