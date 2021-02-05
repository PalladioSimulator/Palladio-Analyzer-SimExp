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
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTDefaultReconfigurationStrategy;

import com.google.common.collect.Sets;

public class DefaultDeltaIoTStrategyContext implements ReconfigurationStrategyContext {

	private final DeltaIoTDefaultReconfigurationStrategy strategy;
	private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
	
	public DefaultDeltaIoTStrategyContext(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.strategy = new DeltaIoTDefaultReconfigurationStrategy(reconfParamsRepo);
		this.reconfParamsRepo = reconfParamsRepo;
	}
	
	@Override
	public Set<Reconfiguration<?>> getReconfigurationSpace() {
		List<QVToReconfiguration> qvts = QVToReconfigurationManager.get().loadReconfigurations();
		if (qvts.size() != 1) {
			throw new RuntimeException("No DeltaIoT network reconfigutation could be found.");
		}
		
		Set<Reconfiguration<?>> reconfs = Sets.newHashSet();
		if (DeltaIoTNetworkReconfiguration.isCorrectQvtReconfguration(qvts.get(0))) {
			reconfs.add(new DeltaIoTNetworkReconfiguration(qvts.get(0), reconfParamsRepo));
		}
		return reconfs;
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
