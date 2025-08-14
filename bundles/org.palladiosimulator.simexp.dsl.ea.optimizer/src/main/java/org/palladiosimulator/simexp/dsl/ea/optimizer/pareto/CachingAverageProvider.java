package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.HashMap;
import java.util.Map;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

public class CachingAverageProvider<G extends Gene<?, G>> implements IAverageProvider<G> {
    private final IAverageProvider<G> delegate;

    private final Map<Phenotype<G, Double>, Map<String, Double>> averageCache;

    public CachingAverageProvider(IAverageProvider<G> delegate) {
        this.delegate = delegate;
        this.averageCache = new HashMap<>();
    }

    @Override
    public Map<String, Double> getAverages(Phenotype<G, Double> phenotype) {
        Map<String, Double> averages = averageCache.get(phenotype);
        if (averages != null) {
            return averages;
        }

        averages = delegate.getAverages(phenotype);
        averageCache.put(phenotype, averages);
        return averages;
    }
}
