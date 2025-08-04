package org.palladiosimulator.simexp.core.store;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class SimulatedExperienceStore<A, R> implements ITrajectoryStore<A, R> {

    private final SimulatedExperienceStoreDescription description;
    private final ISimulatedExperienceAccessor accessor;

    public SimulatedExperienceStore(ISimulatedExperienceAccessor accessor,
            SimulatedExperienceStoreDescription description) {
        this.description = description;
        this.accessor = accessor;
    }

    @Override
    public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id) {
        try (SimulatedExperienceReadAccessor readAccessor = accessor.createSimulatedExperienceReadAccessor()) {
            Optional<SimulatedExperience> result = readAccessor.findSelfAdaptiveSystemState(id);
            return result;
        }
    }

    @Override
    public Optional<List<SimulatedExperience>> getTrajectoryAt(int index) {
        try (SimulatedExperienceReadAccessor readAccessor = accessor.createSimulatedExperienceReadAccessor()) {
            Optional<List<SimulatedExperience>> result = readAccessor.getTrajectoryAt(index);
            return result;
        }
    }

    @Override
    public void store(Trajectory<A, R> trajectory) {
        try (SimulatedExperienceWriteAccessor writeAccessor = accessor
            .createSimulatedExperienceWriteAccessor(description)) {
            writeAccessor.store(toSimulatedExperience(trajectory));
        }
    }

    private List<SimulatedExperience> toSimulatedExperience(Trajectory<A, R> trajectory) {
        return trajectory.getSamplePath()
            .stream()
            .map(each -> DefaultSimulatedExperience.of(each))
            .collect(Collectors.toList());
    }

    @Override
    public void store(Sample<A, R> sample) {
        SimulatedExperience simExp = DefaultSimulatedExperience.of(sample);
        if (isAlreadyStored(simExp)) {
            return;
        }

        try (SimulatedExperienceWriteAccessor writeAccessor = accessor
            .createSimulatedExperienceWriteAccessor(description)) {
            writeAccessor.store(simExp);
        }
    }

    private boolean isAlreadyStored(SimulatedExperience simExp) {
        return findSimulatedExperience(simExp.getId()).isPresent();
    }

    private Optional<SimulatedExperience> findSimulatedExperience(String id) {
        try (SimulatedExperienceReadAccessor readAccessor = accessor.createSimulatedExperienceReadAccessor()) {
            Optional<SimulatedExperience> result = readAccessor.findSimulatedExperience(id);
            return result;
        }
    }
}
