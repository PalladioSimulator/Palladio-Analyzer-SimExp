package org.palladiosimulator.simexp.core.evaluation;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceReadAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;

public class SampleModelIterator implements Iterator<List<SimulatedExperience>> {

    private final SimulatedExperienceReadAccessor readAccessor;

    private int iteration;

    private SampleModelIterator(SimulatedExperienceAccessor accessor, SimulatedExperienceStoreDescription desc) {
        readAccessor = accessor.createSimulatedExperienceReadAccessor();
        readAccessor.connect(desc);
        this.iteration = 0;
    }

    public static SampleModelIterator get(SimulatedExperienceAccessor accessor, String simulationId,
            String sampleSpaceId) {
        SimulatedExperienceStoreDescription desc = SimulatedExperienceStoreDescription.newBuilder()
            .withSimulationId(simulationId)
            .andSampleSpaceId(sampleSpaceId)
            .build();
        return new SampleModelIterator(accessor, desc);
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
