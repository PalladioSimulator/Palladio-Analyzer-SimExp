package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IEAConfig extends IEAParameter, IEATermination {
    double getEpsilon();

    Function<String, Comparator<Double>> getComparatorFactory();

    Optional<ISeedProvider> getSeedProvider();

    double penaltyForInvalids();
}
