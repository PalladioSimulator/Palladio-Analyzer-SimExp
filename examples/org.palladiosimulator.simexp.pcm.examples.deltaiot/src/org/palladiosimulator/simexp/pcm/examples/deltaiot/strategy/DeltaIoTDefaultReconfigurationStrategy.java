package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.OPTIONS_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.STATE_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.filterMotesWithWirelessLinks;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.requirePcmSelfAdaptiveSystemState;

import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.LinkingResourceQuantity;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.MoteContextFilter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterCalculator;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterManager;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

public class DeltaIoTDefaultReconfigurationStrategy extends ReconfigurationStrategy {

	private final ReconfigurationParameterManager paramManager;
	private final ReconfigurationParameterCalculator paramCalculator;

	public DeltaIoTDefaultReconfigurationStrategy(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.paramManager = new ReconfigurationParameterManager(reconfParamsRepo);
		this.paramCalculator = new ReconfigurationParameterCalculator(reconfParamsRepo);
	}

	@Override
	public String getId() {
		return "DefaultDeltaIoTStrategy";
	}

	@Override
	protected SharedKnowledge monitor(State source, Set<Reconfiguration<?>> options) {
		requirePcmSelfAdaptiveSystemState(source);

		PcmSelfAdaptiveSystemState state = PcmSelfAdaptiveSystemState.class.cast(source);

		SharedKnowledge knowledge = new SharedKnowledge();
		knowledge.store(STATE_KEY, state);
		knowledge.store(OPTIONS_KEY, options);

		addMonitoredEnvironmentValues(state, knowledge);

		return knowledge;
	}

	@Override
	protected boolean analyse(SharedKnowledge knowledge) {
		MoteContextFilter moteFiler = new MoteContextFilter(knowledge);
		for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
			for (LinkingResource eachLink : eachMote.linkDetails.keySet()) {
				if (isPowerOptimal(eachMote.linkDetails.get(eachLink)) == false) {
					return true;
				}
			}

			if (eachMote.hasTwoLinks()) {
				if (eachMote.hasEqualTransmissionPower()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected Reconfiguration<?> plan(SharedKnowledge knowledge) {
		DeltaIoTNetworkReconfiguration reconfiguration = knowledge.getValue(OPTIONS_KEY)
				.map(DeltaIoTNetworkReconfiguration.class::cast).orElseThrow();
		boolean powerChanging = false;

		MoteContextFilter moteFiler = new MoteContextFilter(knowledge);
		for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
			for (LinkingResource eachLink : eachMote.linkDetails.keySet()) {
				LinkingResourceQuantity quantity = eachMote.linkDetails.get(eachLink);
				powerChanging = false;
				if (quantity.SNR > 0 && quantity.transmissionPower > 0) {
					decreaseTransmissionPower(eachMote.mote, eachLink, reconfiguration);
					powerChanging = true;
				} else if (quantity.SNR < 0 && quantity.transmissionPower < 15) {
					increaseTransmissionPower(eachMote.mote, eachLink, reconfiguration);
					powerChanging = true;
				}
			}

			if (eachMote.hasTwoLinks() && powerChanging == false) {
				if (eachMote.hasUnequalTransmissionPower()) {
					var iterator = eachMote.linkDetails.keySet().iterator();
					var left = iterator.next();
					var leftTransmissionPower = eachMote.linkDetails.get(left).transmissionPower;
					var leftDistributionFactor = paramManager.findDistributionFactorOf(eachMote.mote, left);
					var right = iterator.next();
					var rightTransmissionPower = eachMote.linkDetails.get(right).transmissionPower;
					var rightDistributionFactor = paramManager.findDistributionFactorOf(eachMote.mote, right);

					if (leftDistributionFactor == 1.0 && rightDistributionFactor == 1.0) {
						reconfiguration.setDistributionFactorsUniformally(eachMote.mote);
					}

					if (leftTransmissionPower > rightTransmissionPower && leftDistributionFactor < 100) {
						adjustDistributionFactor(right, eachMote, reconfiguration);
					} else if (rightDistributionFactor < 100) {
						adjustDistributionFactor(left, eachMote, reconfiguration);
					}

				}
			}
		}

		return reconfiguration;
	}

	@Override
	protected Reconfiguration<?> emptyReconfiguration() {
		return QVToReconfiguration.empty();
	}

	private void addMonitoredEnvironmentValues(PcmSelfAdaptiveSystemState state, SharedKnowledge knowledge) {
		var motesToLinks = filterMotesWithWirelessLinks(state);
		for (AssemblyContext each : motesToLinks.keySet()) {
			var moteContext = new MoteContext(each, motesToLinks.get(each));
			knowledge.store(moteContext.getId(), moteContext);
		}
	}

	private boolean isPowerOptimal(LinkingResourceQuantity linkQuantity) {
		return (linkQuantity.SNR > 0 && linkQuantity.transmissionPower > 0)
				|| (linkQuantity.SNR < 0 && linkQuantity.transmissionPower < 15);
	}

	private void decreaseTransmissionPower(AssemblyContext mote, LinkingResource link,
			DeltaIoTNetworkReconfiguration reconfiguration) {
		var adjustedParams = paramCalculator.computeDecreasedTransmissionPower(mote, link);
		reconfiguration.adjustTransmissionPower(adjustedParams);
	}

	private void increaseTransmissionPower(AssemblyContext mote, LinkingResource link,
			DeltaIoTNetworkReconfiguration reconfiguration) {
		var adjustedParams = paramCalculator.computeIncreasedTransmissionPower(mote, link);
		reconfiguration.adjustTransmissionPower(adjustedParams);
	}
	
	private void adjustDistributionFactor(LinkingResource linkToDecrease, MoteContext mote,
			DeltaIoTNetworkReconfiguration reconfiguration) {
		var adjustedParams = paramCalculator.computeAdjustedDistributionFactors(linkToDecrease, mote);
		reconfiguration.adjustDistributionFactor(adjustedParams);
	}

}
