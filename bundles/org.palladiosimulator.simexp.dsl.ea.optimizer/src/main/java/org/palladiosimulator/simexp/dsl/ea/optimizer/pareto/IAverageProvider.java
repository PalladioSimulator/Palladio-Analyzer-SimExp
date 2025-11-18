package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;

public interface IAverageProvider {
    Optional<Map<String, Double>> getAverages(IndividualResult individualResult);
}
