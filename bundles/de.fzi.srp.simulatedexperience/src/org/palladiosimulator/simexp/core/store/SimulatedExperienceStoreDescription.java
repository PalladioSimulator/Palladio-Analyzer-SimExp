package org.palladiosimulator.simexp.core.store;

public class SimulatedExperienceStoreDescription {

	public class SimulatedExperienceStoreDescBuilder {
		
		public SimulatedExperienceStoreDescBuilder withSimulationId(String id) {
			SimulatedExperienceStoreDescription.this.setSimulationId(id);
			return this;
		}
		
		public SimulatedExperienceStoreDescBuilder andSampleSpaceId(String id) {
			SimulatedExperienceStoreDescription.this.setSampleSpaceId(id);
			return this;
		}
		
		public SimulatedExperienceStoreDescBuilder andSampleHorizon(int length) {
			SimulatedExperienceStoreDescription.this.setSampleHorizon(length);
			return this;
		}
		
		public SimulatedExperienceStoreDescription build() {
			return SimulatedExperienceStoreDescription.this;
		}
		
	}
	
	private String simulationId;
	private String sampleSpaceId;
	private int sampleHorizon;
	
	private SimulatedExperienceStoreDescription() {
		
	}

	public static SimulatedExperienceStoreDescBuilder newBuilder() {
		return new SimulatedExperienceStoreDescription().new SimulatedExperienceStoreDescBuilder();
	}
	
	public int getSampleHorizon() {
		return sampleHorizon;
	}

	private void setSampleHorizon(int sampleHorizon) {
		this.sampleHorizon = sampleHorizon;
	}

	public String getSampleSpaceId() {
		return sampleSpaceId;
	}

	private void setSampleSpaceId(String sampleSpaceId) {
		this.sampleSpaceId = sampleSpaceId;
	}

	public String getSimulationId() {
		return simulationId;
	}

	public void setSimulationId(String simulationId) {
		this.simulationId = simulationId;
	}
	
}
