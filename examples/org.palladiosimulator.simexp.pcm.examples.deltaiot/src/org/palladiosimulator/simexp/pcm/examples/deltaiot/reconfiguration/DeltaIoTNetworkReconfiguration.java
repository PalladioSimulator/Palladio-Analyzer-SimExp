package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import de.uka.ipd.sdq.stoex.VariableReference;

public class DeltaIoTNetworkReconfiguration extends DeltaIoTBaseReconfiguration {

    private final static double UNIFORM_DIST_VALUE = 0.5;

    private final DeltaIoTReconfigurationParamRepository paramRepo;
    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;

    public DeltaIoTNetworkReconfiguration(QVToReconfiguration reconfiguration,
            DeltaIoTReconfigurationParamRepository paramRepo,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        super(reconfiguration.getTransformation());

        this.paramRepo = paramRepo;
        this.modelAccess = modelAccess;
    }

    public static boolean isCorrectQvtReconfguration(QVToReconfiguration qvt) {
        return qvt.getStringRepresentation()
            .endsWith("DeltaIoTNetwork");
    }

    public void setDistributionFactorValuesToDefaults() {
        for (DistributionFactor each : paramRepo.getDistributionFactors()) {
            each.getFactorValues()
                .forEach(value -> value.setValue(DistributionFactorReconfiguration.DEFAULT_VALUE));
        }
    }

    public void setDistributionFactorsUniformally(AssemblyContext mote) {
        modelAccess.retrieveCommunicatingBranches(mote)
            .forEach(branch -> setDistributionFactorIfPresent(branch, UNIFORM_DIST_VALUE));
    }

    public void adjustDistributionFactor(DeltaIoTBaseReconfiguration deltaIoTBaseReconfiguration,
            Map<ProbabilisticBranchTransition, Double> factors) {
        if (deltaIoTBaseReconfiguration.isNotValid(factors)) {
            throw new RuntimeException("The disrtribution factors are note valid.");
        }

        factors.keySet()
            .forEach(branch -> setDistributionFactorIfPresent(branch, factors.get(branch)));
    }

    public void adjustTransmissionPower(Map<VariableReference, Integer> powerSetting) {
        for (VariableReference each : powerSetting.keySet()) {
            var tp = findTransmissionPowerValueWith(each);
            if (tp.isEmpty()) {
                throw new RuntimeException(
                        String.format("Power value for %s could not be found.", each.getReferenceName()));
            }

            int newTpValue = tp.get()
                .getPowerSetting() + powerSetting.get(each);
            tp.get()
                .setPowerSetting(newTpValue);
        }
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
