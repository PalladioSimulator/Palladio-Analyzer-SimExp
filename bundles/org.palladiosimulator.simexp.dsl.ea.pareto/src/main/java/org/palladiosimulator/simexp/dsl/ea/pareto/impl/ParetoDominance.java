package org.palladiosimulator.simexp.dsl.ea.pareto.impl;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

public class ParetoDominance implements Comparator<IndividualResult> {
    private final double epsilon;
    private final IAverageProvider averageProvider;
    private final Function<String, Comparator<Double>> comparatorFactory;

    public ParetoDominance(IPrecisionProvider precisionProvider, IAverageProvider averageProvider,
            Function<String, Comparator<Double>> comparatorFactory) {
        this.epsilon = precisionProvider.getPrecision();
        this.averageProvider = averageProvider;
        this.comparatorFactory = comparatorFactory;
    }

    /**
     * Return if a dominates b.
     * 
     * @return +1 if a dominates b, -1 if b dominates a, 0 if neither (or equal)
     */
    @Override
    public int compare(IndividualResult a, IndividualResult b) {
        Optional<Map<String, Double>> averagesA = averageProvider.getAverages(a);
        Optional<Map<String, Double>> averagesB = averageProvider.getAverages(b);

        if (averagesA.isEmpty()) {
            if (averagesB.isEmpty()) {
                return 0;
            }
            return -1;
        }
        if (averagesB.isEmpty()) {
            return +1;
        }

        return doCompare(averagesA.get(), averagesB.get());
    }

    private int doCompare(Map<String, Double> averagesA, Map<String, Double> averagesB) {
        assert averagesA.keySet()
            .equals(averagesB.keySet());

        boolean aBetter = false;
        boolean bBetter = false;

        for (Map.Entry<String, Double> entryA : averagesA.entrySet()) {
            Comparator<Double> comparator = comparatorFactory.apply(entryA.getKey());
            double valueA = averagesB.get(entryA.getKey());
            double valueB = entryA.getValue();

            if (Double.isNaN(valueA) || Double.isNaN(valueB)) {
                // Treat NaN as incomparable
                return 0;
            }

            if (compareWithPrecision(valueA, valueB, comparator) < 0) {
                aBetter = true;
            } else {
                if (compareWithPrecision(valueB, valueA, comparator) < 0) {
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

    private int compareWithPrecision(Double a, Double b, Comparator<Double> comparator) {
        if (almostEqualAbs(a, b, epsilon)) {
            return 0;
        }
        return comparator.compare(a, b);
    }

    private boolean almostEqualAbs(double a, double b, double eps) {
        if (Double.isNaN(a) || Double.isNaN(b)) {
            return false;
        }
        if (Double.isInfinite(a) || Double.isInfinite(b)) {
            return a == b;
        }
        return Math.abs(a - b) <= eps;
    }
}
