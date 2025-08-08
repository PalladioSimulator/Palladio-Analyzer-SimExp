package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;

import io.jenetics.Gene;

public class ParetoEvolutionStatistics<G extends Gene<?, G>> {

    private final static int ROUNDING_CONSTANT = 100000;
    private final static String CPATTERN = "| %22s %-51s|\n";

    private final MOEAFitnessFunction<G> fitnessFunction;
    private final BigInteger overallPower;

    public ParetoEvolutionStatistics(MOEAFitnessFunction<G> fitnessFunction, BigInteger overallPower) {
        this.fitnessFunction = fitnessFunction;
        this.overallPower = overallPower;
    }

    @Override
    public String toString() {
        return reportStatistics();
    }

    public String reportStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|  Evaluated optimizables of total search space                             |\n"
                + "+---------------------------------------------------------------------------+\n");
        BigInteger fitnessEvaluationCount = BigInteger.valueOf(fitnessFunction.getNumberOfUniqueFitnessEvaluations());
        String result = formatResult(fitnessEvaluationCount, overallPower);
        stringBuilder.append(format(CPATTERN, "Evaluated:", result));

        stringBuilder.append("+---------------------------------------------------------------------------+\n");
        return stringBuilder.toString();
    }

    private String formatResult(BigInteger uniqueFitnessEvaluations, BigInteger combinationsInOptimizableSpace) {
        String percentageVisited = toRoundedString(uniqueFitnessEvaluations, combinationsInOptimizableSpace);
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        String combinations = df.format(combinationsInOptimizableSpace);
        return String.format("%d of %s (%s%%)", uniqueFitnessEvaluations, combinations, percentageVisited);
    }

    String toRoundedString(BigInteger uniqueFitnessEvaluations, BigInteger combinationsInOptimizableSpace) {
        BigDecimal actualEvaluations = new BigDecimal(uniqueFitnessEvaluations);
        BigDecimal totalCombinations = new BigDecimal(combinationsInOptimizableSpace);
        BigDecimal percentageVisited = actualEvaluations.divide(totalCombinations, MathContext.DECIMAL32)
            .multiply(BigDecimal.valueOf(100));
        BigDecimal rounded = percentageVisited.round(new MathContext(ROUNDING_CONSTANT));
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        String fraction = df.format(rounded);
        return fraction;
    }
}
