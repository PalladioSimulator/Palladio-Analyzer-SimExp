package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.ISeq;

public class ParetoEvolutionStatistics<G extends Gene<?, G>> implements Consumer<EvolutionResult<G, Double>> {

    private final static int ROUNDING_CONSTANT = 100000;
    private final static String CPATTERN = "| %22s %-51s|\n";

    private final EvolutionStatistics<Double, DoubleMomentStatistics> evolutionStatistics;
    private final MOEAFitnessFunction<G> fitnessFunction;
    private final BigInteger overallPower;

    public ParetoEvolutionStatistics(MOEAFitnessFunction<G> fitnessFunction, BigInteger overallPower) {
        this.evolutionStatistics = EvolutionStatistics.ofNumber();
        this.fitnessFunction = fitnessFunction;
        this.overallPower = overallPower;
    }

    @Override
    public void accept(EvolutionResult<G, Double> t) {
        List<Phenotype<G, Double>> phenoList = new ArrayList<>();
        for (Phenotype<G, Double> phenotype : t.population()) {
            Phenotype<G, Double> of = Phenotype.of(phenotype.genotype(), 0);
            Phenotype<G, Double> finishedPheno = of.withFitness(phenotype.fitness());
            phenoList.add(finishedPheno);
        }
        ISeq<Phenotype<G, Double>> popSeq = ISeq.of(phenoList);

        EvolutionResult<G, Double> modifiedResult = EvolutionResult.of(t.optimize(), popSeq, t.generation(),
                t.totalGenerations(), t.durations(), t.killCount(), t.invalidCount(), t.alterCount());
        evolutionStatistics.accept(modifiedResult);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|  Evaluated optimizables of total search space                             |\n"
                + "+---------------------------------------------------------------------------+\n");
        BigInteger fitnessEvaluationCount = BigInteger.valueOf(fitnessFunction.getNumberOfUniqueFitnessEvaluations());
        String result = formatResult(fitnessEvaluationCount, overallPower);
        stringBuilder.append(format(CPATTERN, "Evaluated:", result));

        stringBuilder.append("+---------------------------------------------------------------------------+\n");
        return evolutionStatistics.toString() + "\n" + stringBuilder.toString();
    }

    private String formatResult(BigInteger uniqueFitnessEvaluations, BigInteger combinationsInOptimizableSpace) {
        String percentageVisited = toRoundedString(uniqueFitnessEvaluations, combinationsInOptimizableSpace);
        return String.format("%d of %s (%s%%)", uniqueFitnessEvaluations, combinationsInOptimizableSpace,
                percentageVisited);
    }

    String toRoundedString(BigInteger uniqueFitnessEvaluations, BigInteger combinationsInOptimizableSpace) {
        BigDecimal actualEvaluations = new BigDecimal(uniqueFitnessEvaluations);
        BigDecimal totalCombinations = new BigDecimal(combinationsInOptimizableSpace);
        BigDecimal percentageVisited = actualEvaluations.divide(totalCombinations)
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
