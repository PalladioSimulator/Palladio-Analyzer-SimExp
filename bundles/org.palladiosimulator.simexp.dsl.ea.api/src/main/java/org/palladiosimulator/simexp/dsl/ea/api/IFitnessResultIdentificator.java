package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IFitnessResultIdentificator {
    Optional<String> getIdentificator(List<OptimizableValue<?>> optimizableValues);

}
