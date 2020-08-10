package org.palladiosimulator.simexp.core.process;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;

import com.google.common.collect.Lists;

public class ExperienceSimulationConfiguration {

	public class ExperienceSimulationConfigBuilder {
		
		public ExperienceSimulationConfigBuilder withSimulationID(String simulationID) {
			ExperienceSimulationConfiguration.this.simulationID = simulationID;
			return this;
		}
		
		public ExperienceSimulationConfigBuilder withSampleSpaceID(String sampleSpaceID) {
			ExperienceSimulationConfiguration.this.sampleSpaceID = sampleSpaceID;
			return this;
		}
		
		public ExperienceSimulationConfigBuilder withNumberOfRuns(int numOfRuns) {
			ExperienceSimulationConfiguration.this.numberOfRuns = numOfRuns;
			return this;
		}
		
		public ExperienceSimulationConfigBuilder addSimulationRunner(List<ExperienceSimulationRunner> runner) {
			ExperienceSimulationConfiguration.this.runner.addAll(runner);
			return this;
		}
		
		public ExperienceSimulationConfigBuilder addSimulationRunner(ExperienceSimulationRunner runner) {
			ExperienceSimulationConfiguration.this.runner.add(runner);
			return this;
		}
		
		public ExperienceSimulationConfigBuilder sampleWith(MarkovSampling markovSampler) {
			ExperienceSimulationConfiguration.this.markovSampler = markovSampler;
			return this;
		}
		
		public ExperienceSimulationConfiguration build() {
			checkValidity();
		
			return ExperienceSimulationConfiguration.this;							   
		}

		private void checkValidity() {
			//TODO exception handling
			Objects.requireNonNull(runner, "");
			Objects.requireNonNull(markovSampler, "");
			if (isNegative(numberOfRuns) || isNegative(horizon)) {
				throw new RuntimeException("");
			}
			if (isEmptyString(simulationID) || isEmptyString(sampleSpaceID)) {
				throw new RuntimeException("");
			}
		}

		private boolean isNegative(int value) {
			return value < 0;
		}
		
		private boolean isEmptyString(String value) {
			return value == "";
		}
	}
	
	private int horizon = 0;
	private int numberOfRuns = 0;
	private String simulationID = "";
	private String sampleSpaceID = "";
	private List<ExperienceSimulationRunner> runner = Lists.newArrayList();
	private MarkovSampling markovSampler = null;
	
	private ExperienceSimulationConfiguration() {
		
	}
	
	public int getHorizon() {
		return horizon;
	}

	public int getNumberOfRuns() {
		return numberOfRuns;
	}

	public String getSimulationID() {
		return simulationID;
	}

	public List<ExperienceSimulationRunner> getSimulationRunner() {
		return runner;
	}

	public MarkovSampling getMarkovSampler() {
		return markovSampler;
	}

	public String getSampleSpaceID() {
		return sampleSpaceID;
	}
	
	public static ExperienceSimulationConfigBuilder newBuilder() {
		return new ExperienceSimulationConfiguration().new ExperienceSimulationConfigBuilder();
	}
	
}
