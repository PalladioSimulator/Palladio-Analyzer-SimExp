package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

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

import com.google.common.collect.Sets;

public class DistributionFactorReconfiguration extends DeltaIoTBaseReconfiguration
        implements IDistributionFactorReconfiguration {

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
        this(reconfiguration, Sets.newLinkedHashSet(distFactors));
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

    @Override
    public void adjustDistributionFactor(Map<ProbabilisticBranchTransition, Double> factors) {
        for (Map.Entry<ProbabilisticBranchTransition, Double> entry : factors.entrySet()) {
            ProbabilisticBranchTransition each = entry.getKey();
            Double value = entry.getValue();
            Optional<DistributionFactorValue> distFactorValue = findDistFactorValueWith(each);
            distFactorValue.ifPresent(v -> v.setValue(value));
        }
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
