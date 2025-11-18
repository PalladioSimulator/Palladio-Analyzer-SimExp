package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;

public class CachingAverageProvider implements IAverageProvider {
    private final IAverageProvider delegate;

    private final Map<IndividualResult, Optional<Map<String, Double>>> averageCache;

    public CachingAverageProvider(IAverageProvider delegate) {
        this.delegate = delegate;
        this.averageCache = new HashMap<>();
    }

    @Override
    public Optional<Map<String, Double>> getAverages(IndividualResult individualResult) {
        Optional<Map<String, Double>> averages = averageCache.get(individualResult);
        if (averages != null) {
            return averages;
        }

        averages = delegate.getAverages(individualResult);
        averageCache.put(individualResult, averages);
        return averages;
    }
}
