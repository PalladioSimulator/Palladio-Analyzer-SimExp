package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IEAConfig extends IEAParameter, IEATermination {
    IPrecisionProvider getPrecisionProvider();

    Optional<ISeedProvider> getSeedProvider();

    double penaltyForInvalids();
}
