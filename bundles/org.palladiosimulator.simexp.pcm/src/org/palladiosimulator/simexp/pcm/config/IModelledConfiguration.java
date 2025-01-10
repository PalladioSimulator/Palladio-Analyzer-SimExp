package org.palladiosimulator.simexp.pcm.config;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;

public interface IModelledConfiguration {
    URI getSmodelURI();

    ModelledOptimizationType getOptimizationType();
}
