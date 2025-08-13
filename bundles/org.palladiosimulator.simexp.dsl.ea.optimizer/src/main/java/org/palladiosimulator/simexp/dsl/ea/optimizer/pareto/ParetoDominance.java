package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

public class ParetoDominance<G extends Gene<?, G>> implements Comparator<Phenotype<G, Double>> {

    @Override
    public int compare(Phenotype<G, Double> a, Phenotype<G, Double> b) {
        return a.fitness()
            .compareTo(b.fitness());
    }

}
