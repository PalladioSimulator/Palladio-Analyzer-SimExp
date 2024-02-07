package org.palladiosimulator.simexp.core.store;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public class SimulatedExperienceStore<S, A, R> {

    private final DescriptionProvider descriptionProvider;
    private final SimulatedExperienceAccessor simExperienceAccessor;

    public SimulatedExperienceStore(DescriptionProvider descriptionProvider) {
        this.descriptionProvider = descriptionProvider;
        // TODO exception handling
        this.simExperienceAccessor = ServiceRegistry.get()
            .findService(SimulatedExperienceAccessor.class)
            .orElseThrow(() -> new RuntimeException(""));
        ServiceRegistry.get()
            .findService(SimulatedExperienceCache.class)
            .ifPresent(cache -> simExperienceAccessor.setOptionalCache(cache));
    }

    public void store(Trajectory<S, A, R, S> trajectory) {
        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        simExperienceAccessor.connect(description);
        simExperienceAccessor.store(toSimulatedExperience(trajectory));
        simExperienceAccessor.close();
    }

    private List<SimulatedExperience> toSimulatedExperience(Trajectory<S, A, R, S> trajectory) {
        return trajectory.getSamplePath()
            .stream()
            .map(each -> DefaultSimulatedExperience.of(each))
            .collect(Collectors.toList());
    }

    public void store(Sample<S, A, R, S> sample) {
        SimulatedExperience simExp = DefaultSimulatedExperience.of(sample);
        if (isAlreadyStored(simExp)) {
            return;
        }

        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        simExperienceAccessor.connect(description);
        simExperienceAccessor.store(simExp);
        simExperienceAccessor.close();
    }

    private boolean isAlreadyStored(SimulatedExperience simExp) {
        return findSimulatedExperience(simExp.getId()).isPresent();
    }

    public Optional<SimulatedExperience> findSimulatedExperience(String id) {
        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        simExperienceAccessor.connect(description);
        Optional<SimulatedExperience> result = simExperienceAccessor.findSimulatedExperience(id);
        simExperienceAccessor.close();
        return result;
    }

    public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id) {
        SimulatedExperienceStoreDescription description = descriptionProvider.getDescription();
        simExperienceAccessor.connect(description);
        Optional<SimulatedExperience> result = simExperienceAccessor.findSelfAdaptiveSystemState(id);
        simExperienceAccessor.close();
        return result;
    }

}
