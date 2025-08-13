package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.ITranscoder;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

public class ParetoDominance<G extends Gene<?, G>> implements Comparator<Phenotype<G, Double>> {
    private final ITranscoder<G> normalizer;
    private final QualityAttributesAverageCalculator qualityAttributesAverageCalculator;

    public ParetoDominance(ITranscoder<G> normalizer, IQualityAttributeProvider qualityAttributeProvider) {
        this.normalizer = normalizer;
        this.qualityAttributesAverageCalculator = new QualityAttributesAverageCalculator(qualityAttributeProvider);
    }

    /**
     * Return if a dominates b.
     * 
     * @return +1 if a dominates b, -1 if b dominates a, 0 if neither (or equal)
     */
    @Override
    public int compare(Phenotype<G, Double> a, Phenotype<G, Double> b) {
        List<OptimizableValue<?>> optimizableValuesA = normalizer.toOptimizableValues(a.genotype());
        List<OptimizableValue<?>> optimizableValuesB = normalizer.toOptimizableValues(b.genotype());
        Map<String, Double> averagesA = qualityAttributesAverageCalculator.calculateAverages(optimizableValuesA);
        Map<String, Double> averagesB = qualityAttributesAverageCalculator.calculateAverages(optimizableValuesB);
        assert averagesA.keySet()
            .equals(averagesB.keySet());

        boolean aBetter = false;
        boolean bBetter = false;

        for (Map.Entry<String, Double> entryA : averagesA.entrySet()) {
            Comparator<Double> comparator = getComparator(entryA.getKey());
            double valueA = entryA.getValue();
            double valueB = averagesB.get(entryA.getKey());

            if (Double.isNaN(valueA) || Double.isNaN(valueB)) {
                // Treat NaN as incomparable
                return 0;
            }

            if (comparator.compare(valueA, valueB) < 0) {
                aBetter = true;
            } else {
                if (comparator.compare(valueB, valueA) < 0) {
                    bBetter = true;
                }
            }

            if (aBetter && bBetter) {
                // Incomparable
                return 0;
            }
        }

        if (aBetter && !bBetter) {
            return +1;
        }
        if (bBetter && !aBetter) {
            return -1;
        }
        // Equal on all objectives -> neither strictly dominates
        return 0;
    }

    private Comparator<Double> getComparator(String attribute) {
        // TODO:
        return Double::compare;
    }
}
