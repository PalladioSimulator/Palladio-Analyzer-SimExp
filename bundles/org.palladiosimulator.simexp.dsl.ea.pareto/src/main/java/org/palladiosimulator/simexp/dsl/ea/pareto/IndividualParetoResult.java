package org.palladiosimulator.simexp.dsl.ea.pareto;

import java.util.Map;
import java.util.TreeMap;

import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;

public class IndividualParetoResult {

    private final IndividualResult individualResult;
    private final Map<String, Double> averages;

    public IndividualParetoResult(IndividualResult individualResult, Map<String, Double> averages) {
        this.individualResult = individualResult;
        this.averages = new TreeMap<>(averages);
    }

    public IndividualResult getIndividualResult() {
        return individualResult;
    }

    public Map<String, Double> getAverages() {
        return averages;
    }

    public double buildScore() {
        double sum = averages.values()
            .stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        double score = sum / averages.size();
        return score;
    }

}
