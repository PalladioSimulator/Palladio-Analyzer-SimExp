package org.palladiosimulator.simexp.core.store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public class SimulatedExperienceStore<S, A, R> {

    private final SimulatedExperienceStoreDescription description;
    private final SimulatedExperienceAccessor simExperienceAccessor;

    private static SimulatedExperienceStore storeInstance = null;

    private SimulatedExperienceStore(SimulatedExperienceStoreDescription description) {
        this.description = description;
        // TODO exception handling
        this.simExperienceAccessor = ServiceRegistry.get()
            .findService(SimulatedExperienceAccessor.class)
            .orElseThrow(() -> new RuntimeException(""));
        ServiceRegistry.get()
            .findService(SimulatedExperienceCache.class)
            .ifPresent(cache -> simExperienceAccessor.setOptionalCache(cache));
    }

    public static <S, A, R> void create(SimulatedExperienceStoreDescription description) {
        storeInstance = new SimulatedExperienceStore<>(description);
    }

    public static <S, A, R> SimulatedExperienceStore<S, A, R> get() {
        // TODO Exception handling
        return Objects.requireNonNull(storeInstance, "");
    }

    public void store(Trajectory<S, A, R> trajectory) {
        simExperienceAccessor.connect(description);
        simExperienceAccessor.store(toSimulatedExperience(trajectory));
        simExperienceAccessor.close();
    }

    private List<SimulatedExperience> toSimulatedExperience(Trajectory<S, A, R> trajectory) {
        return trajectory.getSamplePath()
            .stream()
            .map(each -> DefaultSimulatedExperience.of(each))
            .collect(Collectors.toList());
    }

    public void store(Sample<S, A, R> sample) {
        SimulatedExperience simExp = DefaultSimulatedExperience.of(sample);
        if (isAlreadyStored(simExp)) {
            return;
        }

        simExperienceAccessor.connect(description);
        simExperienceAccessor.store(simExp);
        simExperienceAccessor.close();
    }

    private boolean isAlreadyStored(SimulatedExperience simExp) {
        return findSimulatedExperience(simExp.getId()).isPresent();
    }

    public Optional<SimulatedExperience> findSimulatedExperience(String id) {
        simExperienceAccessor.connect(description);
        Optional<SimulatedExperience> result = simExperienceAccessor.findSimulatedExperience(id);
        simExperienceAccessor.close();
        return result;
    }

    public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id) {
        simExperienceAccessor.connect(description);
        Optional<SimulatedExperience> result = simExperienceAccessor.findSelfAdaptiveSystemState(id);
        simExperienceAccessor.close();
        return result;
    }

}
