package org.palladiosimulator.simexp.core.store;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

class SampleModelIterator implements Iterator<List<SimulatedExperience>> {

    private final ISimulatedExperienceStore simulatedExperienceStore;

    private int iteration;

    public SampleModelIterator(ISimulatedExperienceStore simulatedExperienceStore) {
        this.simulatedExperienceStore = simulatedExperienceStore;
        this.iteration = 0;
    }

    @Override
    public boolean hasNext() {
        Optional<List<SimulatedExperience>> trajectory = simulatedExperienceStore.getTrajectoryAt(iteration);
        return trajectory.isPresent();
    }

    @Override
    public List<SimulatedExperience> next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }

        Optional<List<SimulatedExperience>> traj = simulatedExperienceStore.getTrajectoryAt(iteration);

        iteration++;

        return traj.get();
    }
}
