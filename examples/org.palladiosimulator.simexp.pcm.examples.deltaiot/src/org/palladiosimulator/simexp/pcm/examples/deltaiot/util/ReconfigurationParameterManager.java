package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterCalculator.isPhysicalLink;

import java.util.function.Predicate;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;

import de.uka.ipd.sdq.stoex.VariableReference;

public class ReconfigurationParameterManager {
	
	private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
	
	public ReconfigurationParameterManager(DeltaIoTReconfigurationParamRepository reconfParamsRepo) {
		this.reconfParamsRepo = reconfParamsRepo;
	}
	
	public double findDistributionFactorOf(AssemblyContext mote, LinkingResource link) {
		var branches = DeltaIoTModelAccess.get().retrieveCommunicatingBranches(mote);
		
		ProbabilisticBranchTransition searchedBranch;
		if (branches.size() == 1) {
			searchedBranch = branches.get(0);
		} else if (branches.size() == 2) {
			searchedBranch = isPhysicalLink(branches.get(0), link) ? branches.get(0) : branches.get(1);
		} else {
			throw new RuntimeException("The number of links must be either one or two.");
		}
		
		var originalBranch = findBranchWith(searchedBranch.getEntityName());
		return originalBranch.getBranchProbability();
	}
	
	public ProbabilisticBranchTransition findBranchWith(String branchName) {
		return reconfParamsRepo.getDistributionFactors().stream()
				.flatMap(each -> each.getFactorValues().stream())
				.filter(factorValuesWithBranch(branchName))
				.map(DistributionFactorValue::getAppliedBranch)
				.findFirst()
				.orElseThrow(() -> new RuntimeException(String.format("There is no branch with name %s", branchName)));
	}
	
	public VariableReference findVariableReferenceWith(String referenceName) {
		return reconfParamsRepo.getTransmissionPower().stream()
				.flatMap(each -> each.getTransmissionPowerValues().stream())
				.filter(powerValuesWithVariable(referenceName))
				.map(TransmissionPowerValue::getVariableRef)
				.findFirst()
				.orElseThrow(() -> new RuntimeException(
						String.format("There is no variable reference with name %s", referenceName)));
	}

	private Predicate<TransmissionPowerValue> powerValuesWithVariable(String referenceName) {
		return value -> value.getVariableRef().getReferenceName().equals(referenceName);
	}

	private Predicate<DistributionFactorValue> factorValuesWithBranch(String branchName) {
		return value -> value.getAppliedBranch().getEntityName().equals(branchName);
	}
	
}