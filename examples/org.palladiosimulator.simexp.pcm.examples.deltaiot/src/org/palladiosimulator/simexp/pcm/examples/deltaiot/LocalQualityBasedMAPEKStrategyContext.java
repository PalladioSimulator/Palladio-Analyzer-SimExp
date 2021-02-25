package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTReconfigurationStrategy2;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.LocalQualityBasedReconfigurationPlanner;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;

public class LocalQualityBasedMAPEKStrategyContext implements ReconfigurationStrategyContext {

	private final DeltaIoTReconfigurationStrategy2 strategy;
	private final DefaultDeltaIoTStrategyContext decoratedContext;
	
	public LocalQualityBasedMAPEKStrategyContext(PrismSimulatedMeasurementSpec packetLossSpec,
			PrismSimulatedMeasurementSpec energyConsumptionSpec,
			DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.strategy = DeltaIoTReconfigurationStrategy2.newBuilder()
				.withID("LocalQualityBasedStrategy")
				.andPacketLossSpec(packetLossSpec)
				.andEnergyConsumptionSpec(energyConsumptionSpec)
				.andPlanner(new LocalQualityBasedReconfigurationPlanner(reconfParamsRepo))
				.build();
		this.decoratedContext = new DefaultDeltaIoTStrategyContext(reconfParamsRepo);
	}

	@Override
	public Set<Reconfiguration<?>> getReconfigurationSpace() {
		return decoratedContext.getReconfigurationSpace();
	}

	@Override
	public boolean isSelectionPolicy() {
		return false;
	}

	@Override
	public ReconfigurationStrategy getStrategy() {
		return strategy; 
	}

	@Override
	public Policy<Action<?>> getSelectionPolicy() {
		return null;
	}

}
