package org.palladiosimulator.simexp.core.store;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public interface ISimulatedExperienceStore<A, R> {
    ISimulatedExperienceAccessor getAccessor();

    void store(Trajectory<A, R> trajectory);

    void store(Sample<A, R> sample);
}
