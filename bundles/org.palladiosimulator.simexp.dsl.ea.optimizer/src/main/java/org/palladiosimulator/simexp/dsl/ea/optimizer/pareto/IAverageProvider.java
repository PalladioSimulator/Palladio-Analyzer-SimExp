package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Map;
import java.util.Optional;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

public interface IAverageProvider<G extends Gene<?, G>> {
    Optional<Map<String, Double>> getAverages(Phenotype<G, Double> phenotype);
}
