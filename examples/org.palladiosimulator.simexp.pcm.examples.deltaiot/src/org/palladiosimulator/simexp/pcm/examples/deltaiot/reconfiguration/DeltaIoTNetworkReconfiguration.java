package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;

import de.uka.ipd.sdq.stoex.VariableReference;

public class DeltaIoTNetworkReconfiguration extends DeltaIoTBaseReconfiguration
        implements IDeltaIoToReconfiguration, IDistributionFactorReconfiguration, ITransmissionPowerReconfiguration {

    private final static String QVT_FILE_SUFFIX = "DeltaIoTNetwork";

    private final DeltaIoTReconfigurationParamRepository paramRepo;

    public DeltaIoTNetworkReconfiguration(SingleQVToReconfiguration reconfiguration,
            DeltaIoTReconfigurationParamRepository paramRepo) {
        super(reconfiguration);
        this.paramRepo = paramRepo;
    }

    public static boolean isCorrectQvtReconfguration(QVToReconfiguration qvt) {
        return qvt.getReconfigurationName()
            .endsWith(QVT_FILE_SUFFIX);
    }

    @Override
    public void setDistributionFactorValuesToDefaults() {
        for (DistributionFactor each : paramRepo.getDistributionFactors()) {
            each.getFactorValues()
                .forEach(value -> value.setValue(DistributionFactorReconfiguration.DEFAULT_VALUE));
        }
    }

    @Override
    public void adjustDistributionFactor(Map<ProbabilisticBranchTransition, Double> factors) {
        if (isNotValid(factors)) {
            throw new RuntimeException("The disrtribution factors are note valid.");
        }

        for (Map.Entry<ProbabilisticBranchTransition, Double> entry : factors.entrySet()) {
            ProbabilisticBranchTransition branch = entry.getKey();
            Double value = entry.getValue();
            setDistributionFactorIfPresent(branch, value);
        }
    }

    @Override
    public void adjustTransmissionPower(Map<VariableReference, Integer> powerSetting) {
        for (Map.Entry<VariableReference, Integer> entry : powerSetting.entrySet()) {
            VariableReference each = entry.getKey();
            Integer adjustment = entry.getValue();
            var tp = findTransmissionPowerValueWith(each);
            if (tp.isEmpty()) {
                throw new RuntimeException(
                        String.format("Power value for %s could not be found.", each.getReferenceName()));
            }

            TransmissionPowerValue transmissionPowerValue = tp.get();
            adjust(transmissionPowerValue, adjustment);
        }
    }

    private void adjust(TransmissionPowerValue value, int adjustement) {
        int newPowerVal = value.getPowerSetting() + adjustement;
        value.setPowerSetting(newPowerVal);
    }

    private Optional<DistributionFactorValue> findDistFactorValueWith(ProbabilisticBranchTransition branch) {
        return paramRepo.getDistributionFactors()
            .stream()
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

    private void setDistributionFactorIfPresent(ProbabilisticBranchTransition branch, double value) {
        var factor = findDistFactorValueWith(branch);
        if (factor.isPresent()) {
            factor.get()
                .setValue(value);
        } else {
            throw new RuntimeException(
                    String.format("Distribution factor for branch %s could not be found.", branch.getEntityName()));
        }
    }

    private Optional<TransmissionPowerValue> findTransmissionPowerValueWith(VariableReference varRef) {
        return paramRepo.getTransmissionPower()
            .stream()
            .flatMap(each -> each.getTransmissionPowerValues()
                .stream())
            .filter(transmissionPowerValuesWith(varRef))
            .findFirst();
    }

    private Predicate<TransmissionPowerValue> transmissionPowerValuesWith(VariableReference varRef) {
        return v -> v.getVariableRef()
            .getReferenceName()
            .equals(varRef.getReferenceName());
    }

}
