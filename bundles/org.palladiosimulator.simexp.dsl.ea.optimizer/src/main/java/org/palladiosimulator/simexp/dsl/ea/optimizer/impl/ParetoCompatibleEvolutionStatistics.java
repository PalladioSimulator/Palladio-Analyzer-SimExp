package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.ext.moea.Vec;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.ISeq;

public class ParetoCompatibleEvolutionStatistics implements Consumer<EvolutionResult<BitGene, Vec<double[]>>> {

    private EvolutionStatistics<Double, DoubleMomentStatistics> evolutionStatistics = EvolutionStatistics.ofNumber();


    @Override
    public void accept(EvolutionResult<BitGene, Vec<double[]>> t) {
        List<Phenotype<BitGene, Double>> phenoList = new ArrayList();
        for (Phenotype<BitGene, Vec<double[]>> phenotype : t.population()) {
            Phenotype<BitGene, Double> of = Phenotype.of(phenotype.genotype(), 0);
            Phenotype<BitGene, Double> finishedPheno = of.withFitness(phenotype.fitness()
                .data()[0]);
            phenoList.add(finishedPheno);
        }
        ISeq<Phenotype<BitGene, Double>> popSeq = ISeq.of(phenoList);

        EvolutionResult<BitGene, Double> modifiedResult = EvolutionResult.of(t.optimize(), popSeq, t.generation(),
                t.totalGenerations(), t.durations(), t.killCount(), t.invalidCount(), t.alterCount());
        evolutionStatistics.accept(modifiedResult);
    }

    @Override
    public String toString() {
        return evolutionStatistics.toString();
    }

}
