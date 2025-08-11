package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.api.ISmodelConstants;

public class DefaultSmodelConfig implements ISmodelConfig {
    @Override
    public double getEpsilon() {
        // TODO: get from SModel
        return ISmodelConstants.EPSILON;
    }
}