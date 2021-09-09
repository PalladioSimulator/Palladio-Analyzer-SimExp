package org.palladiosimulator.simexp.core.process;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;

public class ExperienceSimulator {
    
    private static final Logger LOGGER = Logger.getLogger(ExperienceSimulator.class.getName());

	private final MarkovSampling markovSampler;
	private final List<ExperienceSimulationRunner> simulationRunner;
	private final Optional<Initializable> beforeExecutionInitialization;

	private int numberOfRuns;

	private ExperienceSimulator(ExperienceSimulationConfiguration config) {
		this.numberOfRuns = config.getNumberOfRuns();
		this.markovSampler = config.getMarkovSampler();
		this.simulationRunner = config.getSimulationRunner();
		this.beforeExecutionInitialization = Optional.ofNullable(config.getBeforeExecutionInitialization());

		this.simulationRunner.forEach(SelfAdaptiveSystemState::registerSimulationRunner);

		SimulatedExperienceStoreDescription desc = SimulatedExperienceStoreDescription.newBuilder()
				.withSimulationId(config.getSimulationID())
				.andSampleSpaceId(config.getSampleSpaceID())
				.andSampleHorizon(markovSampler.getHorizon())
				.build();
		SimulatedExperienceStore.create(desc);
	}

	public static ExperienceSimulator createSimulator(ExperienceSimulationConfiguration config) {
		return new ExperienceSimulator(config);
	}

	public void run() {
		do {
			initExperienceSimulator();

			Trajectory traj = markovSampler.sampleTrajectory();
			for (Sample each : traj.getSamplePath()) {
				SimulatedExperienceStore.get().store(each);
			}
			SimulatedExperienceStore.get().store(traj);
		} while (stillRunsToExecute());
	}

	private void initExperienceSimulator() {
		beforeExecutionInitialization.ifPresent(Initializable::initialize);
		
		simulationRunner.stream().filter(Initializable.class::isInstance).map(Initializable.class::cast)
				.forEach(Initializable::initialize);
	}

	private boolean stillRunsToExecute() {
		return 0 != (--numberOfRuns);
	}

}
