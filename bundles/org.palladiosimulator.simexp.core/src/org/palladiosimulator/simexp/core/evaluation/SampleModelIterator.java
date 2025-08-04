package org.palladiosimulator.simexp.core.evaluation;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;

public class SampleModelIterator implements Iterator<List<SimulatedExperience>> {

    private final ISimulatedExperienceStore simulatedExperienceStore;

    private int iteration;

    private SampleModelIterator(ISimulatedExperienceStore simulatedExperienceStore) {
        this.simulatedExperienceStore = simulatedExperienceStore;
        this.iteration = 0;
    }

    public static SampleModelIterator get(ISimulatedExperienceStore simulatedExperienceStore) {
        return new SampleModelIterator(simulatedExperienceStore);
    }

    @Override
    public boolean hasNext() {
        Optional<List<SimulatedExperience>> trajectory = simulatedExperienceStore.getTrajectoryAt(iteration);
        return trajectory.isPresent();
    }

    @Override
    public List<SimulatedExperience> next() {
        if (hasNext() == false) {
            // TODO exception handling
            throw new RuntimeException("");
        }

        Optional<List<SimulatedExperience>> traj = simulatedExperienceStore.getTrajectoryAt(iteration);

        iteration++;

        return traj.get();
    }
}
