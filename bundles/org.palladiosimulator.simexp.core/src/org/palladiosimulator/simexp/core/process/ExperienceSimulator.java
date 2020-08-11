package org.palladiosimulator.simexp.core.process;

import java.util.List;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;

public class ExperienceSimulator {
	
	//private final String simulationID;
	private final MarkovSampling markovSampler;
	private final List<ExperienceSimulationRunner> simulationRunner;
	
	private int numberOfRuns;
	
	private ExperienceSimulator(ExperienceSimulationConfiguration config) {
		//this.simulationID = config.getSimulationID();
		this.numberOfRuns = config.getNumberOfRuns();
		this.markovSampler = config.getMarkovSampler();
		this.simulationRunner = config.getSimulationRunner();
		
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
	
	public void produceAndStore() {		
		do {
			this.simulationRunner.forEach(ExperienceSimulationRunner::initSimulationRun);
			
			Trajectory tray = markovSampler.sampleTrajectory();
			for (Sample each : tray.getSamplePath()) {
				SimulatedExperienceStore.get().store(each);
			}
			SimulatedExperienceStore.get().store(tray); 
		} while (stillRunsToExecute());
	}

	private boolean stillRunsToExecute() {
		return 0 != (--numberOfRuns);
	}

}
