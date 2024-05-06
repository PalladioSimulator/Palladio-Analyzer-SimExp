package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;

public abstract class DeltaIoTBaseReconfiguration extends SingleQVToReconfiguration {
    private final static double TOLERANCE = 0.001;

    public DeltaIoTBaseReconfiguration(SingleQVToReconfiguration reconfiguration) {
        super(reconfiguration);
    }

    public boolean isValid(Map<ProbabilisticBranchTransition, Double> factors) {
        if (factors.size() != 2) {
            return false;
        }

        List<ProbabilisticBranchTransition> transitions = Lists.newArrayList(factors.keySet());
        double branchProb1 = transitions.get(0)
            .getBranchProbability() + factors.get(transitions.get(0));
        double branchProb2 = transitions.get(1)
            .getBranchProbability() + factors.get(transitions.get(1));

        boolean sumsUpToOne = DoubleMath.fuzzyEquals((branchProb1 + branchProb2), 1.0, TOLERANCE);
        boolean areInRange = Boolean.logicalAnd(Boolean.logicalAnd(branchProb1 >= 0, branchProb1 <= 1),
                Boolean.logicalAnd(branchProb2 >= 0, branchProb2 <= 1));
        return Boolean.logicalAnd(sumsUpToOne, areInRange);
    }

    public boolean isNotValid(Map<ProbabilisticBranchTransition, Double> factors) {
        return isValid(factors) == false;
    }

}
