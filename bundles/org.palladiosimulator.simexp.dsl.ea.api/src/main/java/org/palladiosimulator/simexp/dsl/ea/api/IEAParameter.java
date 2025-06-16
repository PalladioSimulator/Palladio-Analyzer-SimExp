package org.palladiosimulator.simexp.dsl.ea.api;

public interface IEAParameter {
    int populationSize();

    double mutationRate();

    double crossoverRate();
}
