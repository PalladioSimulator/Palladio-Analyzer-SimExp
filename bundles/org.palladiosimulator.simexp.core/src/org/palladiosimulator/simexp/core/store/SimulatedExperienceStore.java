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

    public void store(Trajectory<A, R> trajectory) {
        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        SimulatedExperienceWriteAccessor writeAccessor = accessor.createSimulatedExperienceWriteAccessor();
        try {
            writeAccessor.store(description, toSimulatedExperience(trajectory));
        } finally {
            writeAccessor.close();
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
        SimulatedExperienceWriteAccessor writeAccessor = accessor.createSimulatedExperienceWriteAccessor();
        try {
            writeAccessor.store(description, simExp);
        } finally {
            writeAccessor.close();
        }
    }

    private boolean isAlreadyStored(SimulatedExperience simExp) {
        return findSimulatedExperience(simExp.getId()).isPresent();
    }

    public Optional<SimulatedExperience> findSimulatedExperience(String id) {
        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        SimulatedExperienceReadAccessor readAccessor = accessor.createSimulatedExperienceReadAccessor();
        readAccessor.connect(description);
        try {
            Optional<SimulatedExperience> result = readAccessor.findSimulatedExperience(id);
            return result;
        } finally {
            readAccessor.close();
        }
    }

    public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id) {
        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        SimulatedExperienceReadAccessor readAccessor = accessor.createSimulatedExperienceReadAccessor();
        readAccessor.connect(description);
        try {
            Optional<SimulatedExperience> result = readAccessor.findSelfAdaptiveSystemState(id);
            return result;
        } finally {
            readAccessor.close();
        }
    }
}
