package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import static java.lang.Math.max;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DistributionFactorReconfiguration extends DeltaIoTBaseReconfiguration
        implements IDeltaIoToReconfiguration, IDistributionFactorReconfiguration {

    public final static double DEFAULT_VALUE = 0.0;
    private final static String QVT_FILE_SUFFIX = "DistributionFactor";

    private final Set<DistributionFactor> distFactors;

    public DistributionFactorReconfiguration(SingleQVToReconfiguration reconfiguration,
            Set<DistributionFactor> distFactors) {
        super(reconfiguration);

        this.distFactors = distFactors;
    }

    public DistributionFactorReconfiguration(SingleQVToReconfiguration reconfiguration,
            List<DistributionFactor> distFactors) {
        this(reconfiguration, Sets.newHashSet(distFactors));
    }

    @Override
    public void setDistributionFactorValuesToDefaults() {
        for (DistributionFactor each : distFactors) {
            each.getFactorValues()
                .forEach(value -> value.setValue(DEFAULT_VALUE));
        }
    }

    public static boolean isCorrectQvtReconfguration(QVToReconfiguration qvt) {
        String stringRepresentation = qvt.getReconfigurationName();
        return stringRepresentation.endsWith(QVT_FILE_SUFFIX);
    }

    public void adjustDistributionFactors(Map<ProbabilisticBranchTransition, Double> factorAdjustements) {
        adjustDistributionFactor(factorAdjustements);
    }

    @Override
    public void adjustDistributionFactor(Map<ProbabilisticBranchTransition, Double> factors) {
        for (Map.Entry<ProbabilisticBranchTransition, Double> entry : factors.entrySet()) {
            ProbabilisticBranchTransition each = entry.getKey();
            Double value = entry.getValue();
            Optional<DistributionFactorValue> distFactorValue = findDistFactorValueWith(each);
            distFactorValue.ifPresent(v -> v.setValue(value));
        }
    }

    public boolean canBeIncreased(Map<ProbabilisticBranchTransition, Double> factors) {
        if (factors.size() != 2) {
            return false;
        }

        List<ProbabilisticBranchTransition> transitions = Lists.newArrayList(factors.keySet());
        double branchProb1 = transitions.get(0)
            .getBranchProbability();
        double branchProb2 = transitions.get(1)
            .getBranchProbability();
        return (branchProb1 == branchProb2) == false;
    }

    public boolean canBeDecreased(Map<ProbabilisticBranchTransition, Double> factors) {
        if (factors.size() != 2) {
            return false;
        }

        List<ProbabilisticBranchTransition> transitions = Lists.newArrayList(factors.keySet());
        double branchProb1 = transitions.get(0)
            .getBranchProbability() + factors.get(transitions.get(0));
        double branchProb2 = transitions.get(1)
            .getBranchProbability() + factors.get(transitions.get(1));
        return max(branchProb1, branchProb2) <= 1.0;
    }

    private Optional<DistributionFactorValue> findDistFactorValueWith(ProbabilisticBranchTransition branch) {
        return distFactors.stream()
            .flatMap(each -> each.getFactorValues()
                .stream())
            .filter(factorValueAppliedTo(branch))
            .findFirst();
    }

    private Predicate<DistributionFactorValue> factorValueAppliedTo(ProbabilisticBranchTransition givenBranch) {
        return value -> value.getAppliedBranch()
            .getId()
            .equals(givenBranch.getId());
    }

}
