package org.palladiosimulator.simexp.core.evaluation;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public class SampleModelIterator implements Iterator<List<SimulatedExperience>> {

    private final SimulatedExperienceAccessor accessor;

    private int iteration;

    private SampleModelIterator(SimulatedExperienceStoreDescription desc) {
        // TODO Exception Handling
        this.accessor = ServiceRegistry.get()
            .findService(SimulatedExperienceAccessor.class)
            .orElseThrow(() -> new RuntimeException(""));
        this.accessor.connect(desc);
        this.iteration = 0;
    }

    public static SampleModelIterator get(String simulationId, String sampleSpaceId) {
        SimulatedExperienceStoreDescription desc = SimulatedExperienceStoreDescription.newBuilder()
            .withSimulationId(simulationId)
            .andSampleSpaceId(sampleSpaceId)
            .build();
        return new SampleModelIterator(desc);
    }

    @Override
    public boolean hasNext() {
        return accessor.getTrajectoryAt(iteration)
            .isPresent();
    }

    @Override
    public List<SimulatedExperience> next() {
        if (hasNext() == false) {
            // TODO exception handling
            throw new RuntimeException("");
        }

        Optional<List<SimulatedExperience>> traj = accessor.getTrajectoryAt(iteration);

        iteration++;

        return traj.get();

    }

    public void terminate() {
        accessor.close();
    }

}
