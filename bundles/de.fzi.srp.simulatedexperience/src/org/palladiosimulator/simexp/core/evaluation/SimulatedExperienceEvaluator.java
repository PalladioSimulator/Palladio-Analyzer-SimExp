package org.palladiosimulator.simexp.core.evaluation;

import java.util.List;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public class SimulatedExperienceEvaluator {

	private final String simulationId;
	private final String sampleSpaceId;
	
	private SimulatedExperienceEvaluator(String simulationId, String sampleSpaceId) {
		this.simulationId = simulationId;
		this.sampleSpaceId = sampleSpaceId;
	}
	
	public static SimulatedExperienceEvaluator of(String simulationId, String sampleSpaceId) {
		return new SimulatedExperienceEvaluator(simulationId, sampleSpaceId);
	}
	
	public Double computeTotalReward() {
		double totalReward = 0;
		
		SampleModelIterator iterator = SampleModelIterator.get(simulationId, sampleSpaceId);
		while (iterator.hasNext()) {
			List<SimulatedExperience> traj = iterator.next();
			totalReward += accumulateReward(traj.stream());
		}
	
		return totalReward;
	}

	private double accumulateReward(Stream<SimulatedExperience> traj) {
		return traj.map(each -> Double.parseDouble(each.getReward())).reduce((r1, r2) -> r1 + r2).get();
	}
	
}
