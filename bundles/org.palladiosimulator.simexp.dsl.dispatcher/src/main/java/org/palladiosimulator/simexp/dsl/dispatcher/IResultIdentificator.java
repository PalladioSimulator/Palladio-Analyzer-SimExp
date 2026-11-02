package org.palladiosimulator.simexp.dsl.dispatcher;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IResultIdentificator {
    Optional<String> getIdentificator(List<OptimizableValue<?>> optimizableValues);

}
