package org.palladiosimulator.simexp.core.store;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

public class SimulatedExperienceStore<A, R> {

    private final DescriptionProvider descriptionProvider;
    private final SimulatedExperienceAccessor accessor;

    public SimulatedExperienceStore(SimulatedExperienceAccessor accessor, DescriptionProvider descriptionProvider) {
        this.descriptionProvider = descriptionProvider;
        this.accessor = accessor;
    }

    public SimulatedExperienceAccessor getAccessor() {
        return accessor;
    }

    public void store(Trajectory<A, R> trajectory) {
        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        try (SimulatedExperienceWriteAccessor writeAccessor = getAccessor()
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

    public void store(Sample<A, R> sample) {
        SimulatedExperience simExp = DefaultSimulatedExperience.of(sample);
        if (isAlreadyStored(simExp)) {
            return;
        }

        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        try (SimulatedExperienceWriteAccessor writeAccessor = getAccessor()
            .createSimulatedExperienceWriteAccessor(description)) {
            writeAccessor.store(simExp);
        }
    }

    private boolean isAlreadyStored(SimulatedExperience simExp) {
        return findSimulatedExperience(simExp.getId()).isPresent();
    }

    private Optional<SimulatedExperience> findSimulatedExperience(String id) {
        try (SimulatedExperienceReadAccessor readAccessor = getAccessor().createSimulatedExperienceReadAccessor()) {
            Optional<SimulatedExperience> result = readAccessor.findSimulatedExperience(id);
            return result;
        }
    }
}
