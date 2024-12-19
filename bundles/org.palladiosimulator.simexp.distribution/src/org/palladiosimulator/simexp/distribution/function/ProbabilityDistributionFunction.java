package org.palladiosimulator.simexp.distribution.function;

import tools.mdsd.probdist.api.random.ISeedable;

public interface ProbabilityDistributionFunction<T> extends ISeedable {
    public T drawSample();

}
