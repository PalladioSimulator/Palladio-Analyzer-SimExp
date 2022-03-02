package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.STATE_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.filterMotesWithWirelessLinks;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.requirePcmSelfAdaptiveSystemState;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.retrieveDeltaIoTNetworkReconfiguration;

import java.util.Iterator;
import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.MoteContextFilter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterCalculator;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

import com.google.common.math.DoubleMath;

public class DeltaIoTDefaultReconfigurationStrategy extends ReconfigurationStrategy<QVToReconfiguration> {

	private final ReconfigurationParameterCalculator paramCalculator;

	public DeltaIoTDefaultReconfigurationStrategy(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.paramCalculator = new ReconfigurationParameterCalculator(reconfParamsRepo);
	}

	@Override
	public String getId() {
		return "DefaultDeltaIoTStrategy";
	}

	@Override
	protected void monitor(State source, SharedKnowledge knowledge) {
		requirePcmSelfAdaptiveSystemState(source);

		PcmSelfAdaptiveSystemState state = PcmSelfAdaptiveSystemState.class.cast(source);

		knowledge.store(STATE_KEY, state);

		addMonitoredEnvironmentValues(state, knowledge);

		var tracker = SystemConfigurationTracker.get(getId());
		tracker.registerAndPrintNetworkConfig(knowledge);
		if (tracker.isLastRun()) {
			tracker.saveNetworkConfigs();
			tracker.resetTrackedValues();
		}
	}

	@Override
	protected boolean analyse(State source, SharedKnowledge knowledge) {
		MoteContextFilter moteFiler = new MoteContextFilter(knowledge);
		for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
			for (WirelessLink eachLink : eachMote.links) {
				if (isPowerOptimal(eachLink) == false) {
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
	protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
		DeltaIoTNetworkReconfiguration reconfiguration = retrieveDeltaIoTNetworkReconfiguration(options);
		reconfiguration.setDistributionFactorValuesToDefaults();
		
		boolean powerChanging = false;

		MoteContextFilter moteFiler = new MoteContextFilter(knowledge);
		for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
			for (WirelessLink eachLink : eachMote.links) {
				powerChanging = false;
				if (eachLink.SNR > 0 && eachLink.transmissionPower > 0) {
					decreaseTransmissionPower(eachMote.mote, eachLink, reconfiguration);
					powerChanging = true;
				} else if (eachLink.SNR < 0 && eachLink.transmissionPower < 15) {
					increaseTransmissionPower(eachMote.mote, eachLink, reconfiguration);
					powerChanging = true;
				}
			}

			if (eachMote.hasTwoLinks() && powerChanging == false) {
				if (eachMote.hasUnequalTransmissionPower()) {
					Iterator<WirelessLink> iterator = eachMote.links.iterator();
					WirelessLink left = iterator.next();
					double leftTransmissionPower = left.transmissionPower;
					double leftDistributionFactor = left.distributionFactor;
					WirelessLink right = iterator.next();
					double rightTransmissionPower = right.transmissionPower;
					double rightDistributionFactor = right.distributionFactor;

					if (isEqualToOne(leftDistributionFactor) && isEqualToOne(rightDistributionFactor)) {
						reconfiguration.setDistributionFactorsUniformally(eachMote.mote);
					}

					if (leftTransmissionPower > rightTransmissionPower && isSmallerThanOne(leftDistributionFactor)) {
						adjustDistributionFactor(right, eachMote, reconfiguration);
					} else if (isSmallerThanOne(rightDistributionFactor)) {
						adjustDistributionFactor(left, eachMote, reconfiguration);
					}

				}
			}
		}

		return reconfiguration;
	}

	@Override
	protected QVToReconfiguration emptyReconfiguration() {
		return QVToReconfiguration.empty();
	}

	private void addMonitoredEnvironmentValues(PcmSelfAdaptiveSystemState state, SharedKnowledge knowledge) {
		var motesToLinks = filterMotesWithWirelessLinks(state);
		for (AssemblyContext each : motesToLinks.keySet()) {
			var moteContext = new MoteContext(each, motesToLinks.get(each));
			knowledge.store(moteContext.getId(), moteContext);
		}
	}

	private boolean isPowerOptimal(WirelessLink link) {
		return (link.SNR > 0 && link.transmissionPower > 0)
				|| (link.SNR < 0 && link.transmissionPower < 15);
	}

	private void decreaseTransmissionPower(AssemblyContext mote, WirelessLink link,
			DeltaIoTNetworkReconfiguration reconfiguration) {
		var adjustedParams = paramCalculator.computeDecreasedTransmissionPower(mote, link);
		reconfiguration.adjustTransmissionPower(adjustedParams);
	}

	private void increaseTransmissionPower(AssemblyContext mote, WirelessLink link,
			DeltaIoTNetworkReconfiguration reconfiguration) {
		var adjustedParams = paramCalculator.computeIncreasedTransmissionPower(mote, link);
		reconfiguration.adjustTransmissionPower(adjustedParams);
	}

	private void adjustDistributionFactor(WirelessLink linkToDecrease, MoteContext mote,
			DeltaIoTNetworkReconfiguration reconfiguration) {
		var adjustedParams = paramCalculator.computeAdjustedDistributionFactors(linkToDecrease, mote);
		reconfiguration.adjustDistributionFactor(adjustedParams);
	}
	
	private boolean isSmallerThanOne(double distributionFactor) {
		return distributionFactor < 1.0 && isEqualToOne(distributionFactor) == false;
	}
	
	private boolean isEqualToOne(double distributionFactor) {
		var TOLERANCE = 0.0001;
		return DoubleMath.fuzzyEquals(distributionFactor, 1.0, TOLERANCE);
	}

}
