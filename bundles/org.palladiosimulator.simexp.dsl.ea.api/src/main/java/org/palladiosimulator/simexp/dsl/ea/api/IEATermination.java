package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Optional;

public interface IEATermination {
    Optional<Integer> maxGenerations();

    Optional<Integer> steadyFitness();
}
