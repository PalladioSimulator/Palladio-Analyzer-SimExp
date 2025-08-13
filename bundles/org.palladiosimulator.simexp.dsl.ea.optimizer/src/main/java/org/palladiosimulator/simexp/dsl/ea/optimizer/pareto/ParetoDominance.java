package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.Map;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

public class ParetoDominance<G extends Gene<?, G>> implements Comparator<Phenotype<G, Double>> {
    private final IAverageProvider<G> averageProvider;

    public ParetoDominance(IAverageProvider<G> averageProvider) {
        this.averageProvider = averageProvider;
    }

    /**
     * Return if a dominates b.
     * 
     * @return +1 if a dominates b, -1 if b dominates a, 0 if neither (or equal)
     */
    @Override
    public int compare(Phenotype<G, Double> a, Phenotype<G, Double> b) {
        Map<String, Double> averagesA = averageProvider.getAverages(a);
        Map<String, Double> averagesB = averageProvider.getAverages(b);
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
