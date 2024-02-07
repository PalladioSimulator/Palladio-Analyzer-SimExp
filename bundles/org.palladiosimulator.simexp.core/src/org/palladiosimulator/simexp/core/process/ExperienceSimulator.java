package org.palladiosimulator.simexp.core.process;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;

public class ExperienceSimulator<S, A, R> {

    private static final Logger LOGGER = Logger.getLogger(ExperienceSimulator.class.getName());

    private final MarkovSampling<S, A, R, State<S>> markovSampler;
    private final List<ExperienceSimulationRunner<S, A>> simulationRunner;
    private final Optional<Initializable> beforeExecutionInitialization;
    private final SimulatedExperienceStore<S, A, R> simulatedExperienceStore;

    private int numberOfRuns;

    private ExperienceSimulator(ExperienceSimulationConfiguration<S, A, R> config,
            SimulatedExperienceStore<S, A, R> simulatedExperienceStore,
            SimulationRunnerHolder<S, A> simulationRunnerHolder) {
        this.numberOfRuns = config.getNumberOfRuns();
        this.markovSampler = config.getMarkovSampler();
        this.simulationRunner = config.getSimulationRunner();
        this.beforeExecutionInitialization = Optional.ofNullable(config.getBeforeExecutionInitialization());
        this.simulationRunner.forEach(simulationRunnerHolder::registerSimulationRunner);
        this.simulatedExperienceStore = simulatedExperienceStore;
    }

    public static <S, A, R> ExperienceSimulator<S, A, R> createSimulator(
            ExperienceSimulationConfiguration<S, A, R> config,
            SimulatedExperienceStore<S, A, R> simulatedExperienceStore,
            SimulationRunnerHolder<S, A> simulationRunnerHolder) {
        return new ExperienceSimulator<>(config, simulatedExperienceStore, simulationRunnerHolder);
    }

    public void run() {
        do {
            initExperienceSimulator();

            Trajectory<S, A, R, State<S>> traj = markovSampler.sampleTrajectory();
            for (Sample<S, A, R, State<S>> each : traj.getSamplePath()) {
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
