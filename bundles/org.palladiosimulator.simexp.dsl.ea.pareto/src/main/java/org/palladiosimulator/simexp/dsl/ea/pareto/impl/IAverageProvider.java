package org.palladiosimulator.simexp.dsl.ea.pareto.impl;

import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;

public interface IAverageProvider {
    Optional<Map<String, Double>> getAverages(IndividualResult individualResult);
}
