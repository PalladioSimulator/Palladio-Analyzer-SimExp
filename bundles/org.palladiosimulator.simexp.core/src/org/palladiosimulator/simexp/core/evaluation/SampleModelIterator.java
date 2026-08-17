package org.palladiosimulator.simexp.core.evaluation;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;

public class SampleModelIterator implements Iterator<List<SimulatedExperience>> {

    private final SimulatedExperienceReadAccessor readAccessor;

    private int iteration;

    private SampleModelIterator(ISimulatedExperienceAccessor accessor) {
        readAccessor = accessor.createSimulatedExperienceReadAccessor();
        this.iteration = 0;
    }

    public static SampleModelIterator get(ISimulatedExperienceAccessor accessor) {
        return new SampleModelIterator(accessor);
    }

    @Override
    public boolean hasNext() {
        return readAccessor.existTrajectoryAt(iteration);
    }

    @Override
    public List<SimulatedExperience> next() {
        if (hasNext() == false) {
            // TODO exception handling
            throw new RuntimeException("");
        }

        Optional<List<SimulatedExperience>> traj = readAccessor.getTrajectoryAt(iteration);

        iteration++;

        return traj.get();

    }

    public void terminate() {
        readAccessor.close();
    }

}
