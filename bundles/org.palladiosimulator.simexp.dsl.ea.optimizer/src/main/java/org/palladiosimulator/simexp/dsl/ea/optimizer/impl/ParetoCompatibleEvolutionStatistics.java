package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;

import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.ISeq;

public class ParetoCompatibleEvolutionStatistics implements Consumer<EvolutionResult<BitGene, Double>> {

    private static final int ROUNDING_CONSTANT = 100000;
    private EvolutionStatistics<Double, DoubleMomentStatistics> evolutionStatistics = EvolutionStatistics.ofNumber();
    private MOEAFitnessFunction fitnessFunction;
    private Genotype<BitGene> genotype;

    public ParetoCompatibleEvolutionStatistics(MOEAFitnessFunction fitnessFunction, Genotype<BitGene> genotype) {
        this.fitnessFunction = fitnessFunction;
        this.genotype = genotype;
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
        double percentageVisited = (((double) fitnessFunction.getNumberOfUniqueFitnessEvaluations())
                / (double) getNumberOfCombinationsInOptimizableSpace()) * 100;
        percentageVisited = Math.floor(percentageVisited * ROUNDING_CONSTANT) / ROUNDING_CONSTANT;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|  Evaluated optimizables of total search space                             |\n"
                + "+---------------------------------------------------------------------------+\n");
        stringBuilder.append("| Evaluated:    ");
        stringBuilder.append(fitnessFunction.getNumberOfUniqueFitnessEvaluations());
        stringBuilder.append("   of    ");
        stringBuilder.append(getNumberOfCombinationsInOptimizableSpace());
        stringBuilder.append("    that's   ");
        stringBuilder.append(percentageVisited + " %");
        stringBuilder.append("                         |\n");
        stringBuilder.append("+---------------------------------------------------------------------------+\n");
        return evolutionStatistics.toString() + "\n" + stringBuilder.toString();
    }

    private long getNumberOfCombinationsInOptimizableSpace() {
        long numOfCombinations = 1;
        for (int i = 0; i < genotype.length(); i++) {
            Chromosome<BitGene> currentChromosome = genotype.get(i);
            numOfCombinations = numOfCombinations * ((SmodelBitChromosome) currentChromosome).getNumOfValues();
        }
        return numOfCombinations;
    }

}
