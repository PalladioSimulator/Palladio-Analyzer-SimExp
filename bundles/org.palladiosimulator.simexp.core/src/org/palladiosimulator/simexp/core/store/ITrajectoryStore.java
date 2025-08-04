package org.palladiosimulator.simexp.core.store;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public interface ITrajectoryStore<A, R> extends ISimulatedExperienceStore {
    void store(Trajectory<A, R> trajectory);

    void store(Sample<A, R> sample);
}
