package org.palladiosimulator.simexp.dsl.smodel.interpreter;

public class DefaultSmodelConfig implements ISmodelConfig {
    @Override
    public double getEpsilon() {
        // TODO: get from SModel see EAOptimizerSimulationExecutor
        return 0.0001;
    }
}