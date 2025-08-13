package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Optional;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IEAConfig extends IEAParameter, IEATermination {
    double getEpsilon();

    Optional<ISeedProvider> getSeedProvider();

    double penaltyForInvalids();
}
