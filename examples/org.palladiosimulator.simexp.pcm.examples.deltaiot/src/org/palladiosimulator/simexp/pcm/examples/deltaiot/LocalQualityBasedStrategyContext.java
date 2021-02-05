package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.LocalQualityBasedReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;

import com.google.common.collect.Sets;

public class LocalQualityBasedStrategyContext implements ReconfigurationStrategyContext {

	private final Policy<Action<?>> reconfSelectionPolicy;
	private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;

	public LocalQualityBasedStrategyContext(PrismSimulatedMeasurementSpec packetLossSpec,
			PrismSimulatedMeasurementSpec energyConsumptionSpec,
			DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.reconfSelectionPolicy = LocalQualityBasedReconfigurationStrategy.newBuilder()
				.withReconfigurationParams(reconfParamsRepo)
				.andPacketLossSpec(packetLossSpec)
				.andEnergyConsumptionSpec(energyConsumptionSpec)
				.build();
		this.reconfParamsRepo = reconfParamsRepo;
	}

	@Override
	public Set<Reconfiguration<?>> getReconfigurationSpace() {
		Set<Reconfiguration<?>> reconfs = Sets.newHashSet();

		List<QVToReconfiguration> qvts = QVToReconfigurationManager.get().loadReconfigurations();
		for (QVToReconfiguration each : qvts) {
			if (DistributionFactorReconfiguration.isCorrectQvtReconfguration(each)) {
				reconfs.add(new DistributionFactorReconfiguration(each, reconfParamsRepo.getDistributionFactors()));
			} else if (TransmissionPowerReconfiguration.isCorrectQvtReconfguration(each)) {
				reconfs.add(new TransmissionPowerReconfiguration(each, reconfParamsRepo.getTransmissionPower()));
			}
		}

		if (reconfs.isEmpty()) {
			// TODO exception handling
			throw new RuntimeException("No DeltaIoT reconfigutations could be found or generated");
		}
		return reconfs;
	}

	@Override
	public boolean isSelectionPolicy() {
		return true;
	}

	@Override
	public ReconfigurationStrategy getStrategy() {
		return null;
	}

	@Override
	public Policy<Action<?>> getSelectionPolicy() {
		return reconfSelectionPolicy;
	}

}
