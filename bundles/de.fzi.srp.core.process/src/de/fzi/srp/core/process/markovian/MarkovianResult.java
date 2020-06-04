package de.fzi.srp.core.process.markovian;

import java.util.Optional;

import de.fzi.srp.core.model.markovmodel.markoventity.Reward;
import de.fzi.srp.core.model.markovmodel.samplemodel.Trajectory;

public class MarkovianResult {
	
	public class MarkovianResultBuilder {
		
		public MarkovianResultBuilder(Trajectory trajToEvaluate) {
			MarkovianResult.this.trajToEvaluate = trajToEvaluate;
			MarkovianResult.this.accumulatedReward = Optional.empty();
		}
		
		public MarkovianResultBuilder withProbability(double probability) {
			MarkovianResult.this.probability = probability;
			return MarkovianResultBuilder.this;
		}
		
		public MarkovianResultBuilder andReward(Reward<?> accumulatedReward) {
			MarkovianResult.this.accumulatedReward = Optional.of(accumulatedReward);
			return MarkovianResultBuilder.this;
		}
		
		public MarkovianResult build() {
			return MarkovianResult.this;
		}
	}
	
	private Trajectory trajToEvaluate;
	private double probability;
	private Optional<Reward<?>> accumulatedReward;
	
	private MarkovianResult() {
		
	}
	
	public static MarkovianResultBuilder of(Trajectory trajToAnalyse) {
		return new MarkovianResult().new MarkovianResultBuilder(trajToAnalyse);
	}
	
	public double getProbability() {
		return probability;
	}

	public Trajectory getEvaluatedTrajectory() {
		return trajToEvaluate;
	}

	public Optional<Reward<?>> getAccumulatedReward() {
		return accumulatedReward;
	}
}
