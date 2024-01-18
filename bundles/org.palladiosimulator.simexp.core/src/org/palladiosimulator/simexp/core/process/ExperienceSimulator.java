package org.palladiosimulator.simexp.core.process;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;

public class ExperienceSimulator<S, A, R> {

    private static final Logger LOGGER = Logger.getLogger(ExperienceSimulator.class.getName());

    private final MarkovSampling<S, A, R> markovSampler;
    private final List<ExperienceSimulationRunner<S, A>> simulationRunner;
    private final Optional<Initializable> beforeExecutionInitialization;
    private final SimulatedExperienceStore<S, A, R> simulatedExperienceStore;

    private int numberOfRuns;

    private ExperienceSimulator(ExperienceSimulationConfiguration<S, A, R> config,
            DescriptionProvider descriptionProvider) {
        this.numberOfRuns = config.getNumberOfRuns();
        this.markovSampler = config.getMarkovSampler();
        this.simulationRunner = config.getSimulationRunner();
        this.beforeExecutionInitialization = Optional.ofNullable(config.getBeforeExecutionInitialization());

        this.simulationRunner.forEach(SelfAdaptiveSystemState::registerSimulationRunner);

        SimulatedExperienceStore.create(descriptionProvider);
        simulatedExperienceStore = SimulatedExperienceStore.get();
    }

    public static <S, A, R> ExperienceSimulator<S, A, R> createSimulator(
            ExperienceSimulationConfiguration<S, A, R> config, DescriptionProvider descriptionProvider) {
        return new ExperienceSimulator<>(config, descriptionProvider);
    }

    public void run() {
        do {
            initExperienceSimulator();

            Trajectory<S, A, R> traj = markovSampler.sampleTrajectory();
            for (Sample<S, A, R> each : traj.getSamplePath()) {
                simulatedExperienceStore.store(each);
            }
            simulatedExperienceStore.store(traj);
        } while (stillRunsToExecute());
    }

    private void initExperienceSimulator() {
        beforeExecutionInitialization.ifPresent(Initializable::initialize);

        simulationRunner.stream()
            .filter(Initializable.class::isInstance)
            .map(Initializable.class::cast)
            .forEach(Initializable::initialize);
    }

    private boolean stillRunsToExecute() {
        return 0 != (--numberOfRuns);
    }

}
