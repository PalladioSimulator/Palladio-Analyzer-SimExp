package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import static java.lang.Math.max;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;

public class DistributionFactorReconfiguration extends QVToReconfiguration {

	private final static String QVT_FILE_SUFFIX = "DistributionFactor";
	private final static double DEFAULT_VALUE = 0.0;
	private final static double TOLERANCE = 0.001;

	private final Set<DistributionFactor> distFactors;

	public DistributionFactorReconfiguration(QVToReconfiguration reconfiguration, Set<DistributionFactor> distFactors) {
		super(reconfiguration.getTransformation());

		this.distFactors = distFactors;
	}

	public void setDistributionFactorValuesToDefaults() {
		for (DistributionFactor each : distFactors) {
			each.getFactorValues().forEach(value -> value.setValue(DEFAULT_VALUE));
		}
	}

	public DistributionFactorReconfiguration(QVToReconfiguration reconfiguration,
			List<DistributionFactor> distFactors) {
		this(reconfiguration, Sets.newHashSet(distFactors));
	}

	public static boolean isCorrectQvtReconfguration(QVToReconfiguration qvt) {
		return qvt.getStringRepresentation().endsWith(QVT_FILE_SUFFIX);
	}

	public void adjustDistributionFactors(Map<ProbabilisticBranchTransition, Double> factorAdjustements) {
		for (ProbabilisticBranchTransition each : factorAdjustements.keySet()) {
			findDistFactorValueWith(each).ifPresent(v -> v.setValue(factorAdjustements.get(each)));
		}
	}

	public boolean canBeIncreased(Map<ProbabilisticBranchTransition, Double> factors) {
		if (factors.size() != 2) {
			return false;
		}

		List<ProbabilisticBranchTransition> transitions = Lists.newArrayList(factors.keySet());
		double branchProb1 = transitions.get(0).getBranchProbability();
		double branchProb2 = transitions.get(1).getBranchProbability();
		return (branchProb1 == branchProb2) == false;
	}

	public boolean canBeDecreased(Map<ProbabilisticBranchTransition, Double> factors) {
		if (factors.size() != 2) {
			return false;
		}

		List<ProbabilisticBranchTransition> transitions = Lists.newArrayList(factors.keySet());
		double branchProb1 = transitions.get(0).getBranchProbability() + factors.get(transitions.get(0));
		double branchProb2 = transitions.get(1).getBranchProbability() + factors.get(transitions.get(1));
		return max(branchProb1, branchProb2) <= 1.0;
	}

	public boolean isValid(Map<ProbabilisticBranchTransition, Double> factors) {
		if (factors.size() != 2) {
			return false;
		}

		List<ProbabilisticBranchTransition> transitions = Lists.newArrayList(factors.keySet());
		double branchProb1 = transitions.get(0).getBranchProbability() + factors.get(transitions.get(0));
		double branchProb2 = transitions.get(1).getBranchProbability() + factors.get(transitions.get(1));

		boolean sumsUpToOne = DoubleMath.fuzzyEquals((branchProb1 + branchProb2), 1.0, TOLERANCE);
		boolean areInRange = Boolean.logicalAnd(Boolean.logicalAnd(branchProb1 >= 0, branchProb1 <= 1),
				Boolean.logicalAnd(branchProb2 >= 0, branchProb2 <= 1));
		return Boolean.logicalAnd(sumsUpToOne, areInRange);
	}

	private Optional<DistributionFactorValue> findDistFactorValueWith(ProbabilisticBranchTransition branch) {
		return distFactors.stream().flatMap(each -> each.getFactorValues().stream())
				.filter(factorValueAppliedTo(branch)).findFirst();
	}

	private Predicate<DistributionFactorValue> factorValueAppliedTo(ProbabilisticBranchTransition givenBranch) {
		return value -> value.getAppliedBranch().getId().equals(givenBranch.getId());
	}

}
