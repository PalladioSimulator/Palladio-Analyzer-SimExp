package org.palladiosimulator.simexp.pcm.modelled.config;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IOptimizedConfiguration {
    List<OptimizableValue<?>> getOptimizableValues();
}
