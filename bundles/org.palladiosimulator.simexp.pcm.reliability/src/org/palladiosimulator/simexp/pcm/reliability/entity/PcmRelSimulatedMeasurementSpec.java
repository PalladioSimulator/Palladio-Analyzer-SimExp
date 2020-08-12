package org.palladiosimulator.simexp.pcm.reliability.entity;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public class PcmRelSimulatedMeasurementSpec extends SimulatedMeasurementSpecification {

	private final UsageScenario usageScenario;

	public PcmRelSimulatedMeasurementSpec(UsageScenario usageScenario) {
		super(usageScenario.getId(), usageScenario.getEntityName());

		this.usageScenario = usageScenario;
	}

	public UsageScenario getUsageScenario() {
		return usageScenario;
	}

}
