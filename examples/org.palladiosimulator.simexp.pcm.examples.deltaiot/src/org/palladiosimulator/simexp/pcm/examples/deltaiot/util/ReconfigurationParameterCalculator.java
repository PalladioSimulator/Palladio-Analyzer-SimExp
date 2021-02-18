package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.DISTRIBUTION_FACTOR_INCREMENT;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.TRANSMISSION_POWER_INCREMENT;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

import com.google.common.collect.Maps;

import de.uka.ipd.sdq.stoex.VariableReference;

public class ReconfigurationParameterCalculator {

	private final ReconfigurationParameterManager paramManager;

	public ReconfigurationParameterCalculator(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.paramManager = new ReconfigurationParameterManager(reconfParamsRepo);
	}

	public Map<VariableReference, Integer> computeIncreasedTransmissionPower(AssemblyContext mote,
			WirelessLink link) {
		return computeAdjustedTransmissionPower(mote, link, TRANSMISSION_POWER_INCREMENT);
	}

	public Map<VariableReference, Integer> computeDecreasedTransmissionPower(AssemblyContext mote,
			WirelessLink link) {
		return computeAdjustedTransmissionPower(mote, link, TRANSMISSION_POWER_INCREMENT * (-1));
	}

	public Map<ProbabilisticBranchTransition, Double> computeAdjustedDistributionFactors(
			WirelessLink linkToDecrease, MoteContext context) {
		var modelAccess = DeltaIoTModelAccess.get();
		var branchesToAdapt = modelAccess.retrieveCommunicatingBranches(context.mote);

		ProbabilisticBranchTransition branchToIncrease;
		ProbabilisticBranchTransition branchToDecrease;
		if (modelAccess.isPhysicalLink(branchesToAdapt.get(0), linkToDecrease.pcmLink)) {
			branchToIncrease = paramManager.findBranchWith(branchesToAdapt.get(1).getEntityName());
			branchToDecrease = paramManager.findBranchWith(branchesToAdapt.get(0).getEntityName());
		} else {
			branchToIncrease = paramManager.findBranchWith(branchesToAdapt.get(0).getEntityName());
			branchToDecrease = paramManager.findBranchWith(branchesToAdapt.get(1).getEntityName());
		}

		Map<ProbabilisticBranchTransition, Double> factors = Maps.newHashMap();
		factors.put(branchToDecrease, DISTRIBUTION_FACTOR_INCREMENT * (-1));
		factors.put(branchToIncrease, DISTRIBUTION_FACTOR_INCREMENT);
		return factors;
	}

	private Map<VariableReference, Integer> computeAdjustedTransmissionPower(AssemblyContext mote, WirelessLink link,
			int adjustement) {
		Map<VariableReference, Integer> powerSettings = Maps.newHashMap();

		List<VariableUsage> varUsages = mote.getConfigParameterUsages__AssemblyContext();
		for (VariableUsage each : varUsages) {
			if (DeltaIoTModelAccess.get().isTransmissionPowerOfLink(each, link.pcmLink)) {
				var varRef = each.getNamedReference__VariableUsage().getReferenceName();
				powerSettings.put(paramManager.findVariableReferenceWith(varRef), adjustement);
			}
		}
		return powerSettings;
	}

}
