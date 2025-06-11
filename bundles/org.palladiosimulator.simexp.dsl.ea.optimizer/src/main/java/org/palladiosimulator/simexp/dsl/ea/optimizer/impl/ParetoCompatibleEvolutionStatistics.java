package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.ISeq;

public class ParetoCompatibleEvolutionStatistics implements Consumer<EvolutionResult<BitGene, Double>> {

    private final static int ROUNDING_CONSTANT = 100000;
    private final static String CPATTERN = "| %22s %-51s|\n";

    private final EvolutionStatistics<Double, DoubleMomentStatistics> evolutionStatistics;
    private final MOEAFitnessFunction fitnessFunction;
    private final long overallPower;

    public ParetoCompatibleEvolutionStatistics(MOEAFitnessFunction fitnessFunction, long overallPower) {
        this.evolutionStatistics = EvolutionStatistics.ofNumber();
        this.fitnessFunction = fitnessFunction;
        this.overallPower = overallPower;
    }

    @Override
    public void accept(EvolutionResult<BitGene, Double> t) {
        List<Phenotype<BitGene, Double>> phenoList = new ArrayList<>();
        for (Phenotype<BitGene, Double> phenotype : t.population()) {
            Phenotype<BitGene, Double> of = Phenotype.of(phenotype.genotype(), 0);
            Phenotype<BitGene, Double> finishedPheno = of.withFitness(phenotype.fitness());
            phenoList.add(finishedPheno);
        }
        ISeq<Phenotype<BitGene, Double>> popSeq = ISeq.of(phenoList);

        EvolutionResult<BitGene, Double> modifiedResult = EvolutionResult.of(t.optimize(), popSeq, t.generation(),
                t.totalGenerations(), t.durations(), t.killCount(), t.invalidCount(), t.alterCount());
        evolutionStatistics.accept(modifiedResult);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|  Evaluated optimizables of total search space                             |\n"
                + "+---------------------------------------------------------------------------+\n");
        String result = formatResult(fitnessFunction.getNumberOfUniqueFitnessEvaluations(), overallPower);
        stringBuilder.append(format(CPATTERN, "Evaluated:", result));

        stringBuilder.append("+---------------------------------------------------------------------------+\n");
        return evolutionStatistics.toString() + "\n" + stringBuilder.toString();
    }

    private String formatResult(long uniqueFitnessEvaluations, long combinationsInOptimizableSpace) {
        double percentageVisited = (((double) uniqueFitnessEvaluations) / (double) combinationsInOptimizableSpace)
                * 100;
        percentageVisited = Math.floor(percentageVisited * ROUNDING_CONSTANT) / ROUNDING_CONSTANT;
        return String.format("%d of %d (%.0f%%)", uniqueFitnessEvaluations, combinationsInOptimizableSpace,
                percentageVisited);
    }
}
