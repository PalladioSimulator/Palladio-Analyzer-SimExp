package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.jenetics.BitChromosome;
import io.jenetics.Chromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;

public class OptimizableChromosome {

    public Map<Class, Chromosome> mapClass2Chromo;

    public List<Pair> chromosomes;

    private static List<Codec> declaredChromoSubTypes = new ArrayList();

    public OptimizableChromosome(Map<Class, Chromosome> mapClass2Chromo, List<Pair> chromosomes) {
        this.mapClass2Chromo = mapClass2Chromo;
        this.chromosomes = chromosomes;
    }

    public static OptimizableChromosome nextChromosome() {
        Map<Class, Chromosome> mapClass2Chromo = new HashMap();
        List<Pair> localChromosomes = new ArrayList();
        for (Codec c : declaredChromoSubTypes) {

            localChromosomes.add(new Pair(c.decoder(), c.encoding()
                .newInstance()));

            new OptimizableChromosome(mapClass2Chromo, localChromosomes);
//          if (c == BitChromosome.class) {
//              localChromosomes.add(new Pair<>(BitChromosome.class, BitChromosome.of(20, 0.5)));
//              mapClass2Chromo.put(BitChromosome.class, BitChromosome.of(20, 0.5));
//          }
//          
        }

        // TODO implement
        return new OptimizableChromosome(mapClass2Chromo, localChromosomes);
    }

    public static Supplier<OptimizableChromosome> getNextChromosomeSupplier(List<Codec> classes) {
        declaredChromoSubTypes.addAll(classes);

        return () -> nextChromosome();
    }

    public static double eval(final OptimizableChromosome c) {
        double value = 0;

        for (Pair geno : c.chromosomes) {
            Chromosome chromosome = ((Genotype) geno.second()).chromosome();

            if (chromosome instanceof BitChromosome) {
                if (((Boolean) ((Function) geno.first()).apply(geno.second()))) {
                    value += 50;
                }
            } else if (chromosome instanceof DoubleChromosome doubleChromo) {
                value += (Double) ((Function) geno.first()).apply(geno.second());
            } else {
                throw new RuntimeException("Unknown chromosome type specified: " + chromosome.getClass());
            }

        }

        // TODO implement
        return value;
    }

}