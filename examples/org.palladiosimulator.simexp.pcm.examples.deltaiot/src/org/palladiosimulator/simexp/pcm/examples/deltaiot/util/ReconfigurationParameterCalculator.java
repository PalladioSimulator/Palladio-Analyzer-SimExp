package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.DISTRIBUTION_FACTOR_INCREMENT;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.TRANSMISSION_POWER_INCREMENT;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext;

import com.google.common.collect.Maps;

import de.uka.ipd.sdq.stoex.VariableReference;

public class ReconfigurationParameterCalculator {

	private final ReconfigurationParameterManager paramManager;

	public ReconfigurationParameterCalculator(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.paramManager = new ReconfigurationParameterManager(reconfParamsRepo);
	}

	public Map<VariableReference, Integer> computeIncreasedTransmissionPower(AssemblyContext mote,
			LinkingResource link) {
		return computeAdjustedTransmissionPower(mote, link, TRANSMISSION_POWER_INCREMENT);
	}

	public Map<VariableReference, Integer> computeDecreasedTransmissionPower(AssemblyContext mote,
			LinkingResource link) {
		return computeAdjustedTransmissionPower(mote, link, TRANSMISSION_POWER_INCREMENT * (-1));
	}

	public Map<ProbabilisticBranchTransition, Double> computeAdjustedDistributionFactors(
			LinkingResource linkWithHighestQuantity, MoteContext context) {
		var branchesToAdapt = DeltaIoTModelAccess.get().retrieveCommunicatingBranches(context.mote);

		ProbabilisticBranchTransition branchToIncrease;
		ProbabilisticBranchTransition branchToDecrease;
		if (isPhysicalLink(branchesToAdapt.get(0), linkWithHighestQuantity)) {
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

	public static boolean isPhysicalLink(ProbabilisticBranchTransition probabilisticBranchTransition,
			LinkingResource physicalLink) {
		String usedLinkId = probabilisticBranchTransition.getEntityName()
				.substring(probabilisticBranchTransition.getEntityName().length() - 1);
		return physicalLink.getEntityName().endsWith(usedLinkId);
	}

	private Map<VariableReference, Integer> computeAdjustedTransmissionPower(AssemblyContext mote, LinkingResource link,
			int adjustement) {
		Map<VariableReference, Integer> powerSettings = Maps.newHashMap();

		List<VariableUsage> varUsages = mote.getConfigParameterUsages__AssemblyContext();
		for (VariableUsage each : varUsages) {
			if (DeltaIoTModelAccess.get().isTransmissionPowerOfLink(each, link)) {
				var varRef = each.getNamedReference__VariableUsage().getReferenceName();
				powerSettings.put(paramManager.findVariableReferenceWith(varRef), adjustement);
			}
		}
		return powerSettings;
	}

}
