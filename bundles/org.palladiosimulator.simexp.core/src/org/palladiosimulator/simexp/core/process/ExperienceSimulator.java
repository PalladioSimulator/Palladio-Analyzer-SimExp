package org.palladiosimulator.simexp.core.process;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;

public class ExperienceSimulator<C, A, R> {

    private static final Logger LOGGER = Logger.getLogger(ExperienceSimulator.class.getName());

    private final MarkovSampling<A, R> markovSampler;
    private final List<ExperienceSimulationRunner> simulationRunners;
    private final Optional<Initializable> beforeExecutionInitialization;
    private final SimulatedExperienceStore<A, R> simulatedExperienceStore;

    private int numberOfRuns;

    private ExperienceSimulator(ExperienceSimulationConfiguration<C, A, R> config,
            SimulatedExperienceStore<A, R> simulatedExperienceStore, SimulationRunnerHolder simulationRunnerHolder) {
        this.numberOfRuns = config.getNumberOfRuns();
        this.markovSampler = config.getMarkovSampler();
        this.simulationRunners = config.getSimulationRunners();
        this.beforeExecutionInitialization = Optional.ofNullable(config.getBeforeExecutionInitialization());
        simulationRunnerHolder.registerSimulationRunners(simulationRunners);
        this.simulatedExperienceStore = simulatedExperienceStore;
    }

    public static <S, A, R> ExperienceSimulator<S, A, R> createSimulator(
            ExperienceSimulationConfiguration<S, A, R> config, SimulatedExperienceStore<A, R> simulatedExperienceStore,
            SimulationRunnerHolder simulationRunnerHolder) {
        return new ExperienceSimulator<>(config, simulatedExperienceStore, simulationRunnerHolder);
    }

    public void run() {
        do {
            initExperienceSimulator();

            Trajectory<A, R> traj = markovSampler.sampleTrajectory();
            for (Sample<A, R> each : traj.getSamplePath()) {
                simulatedExperienceStore.store(each);
            }
            simulatedExperienceStore.store(traj);
        } while (stillRunsToExecute());
    }

    private void initExperienceSimulator() {
        beforeExecutionInitialization.ifPresent(Initializable::initialize);

        simulationRunners.stream()
            .filter(Initializable.class::isInstance)
            .map(Initializable.class::cast)
            .forEach(Initializable::initialize);
    }

    private boolean stillRunsToExecute() {
        return 0 != (--numberOfRuns);
    }

}
