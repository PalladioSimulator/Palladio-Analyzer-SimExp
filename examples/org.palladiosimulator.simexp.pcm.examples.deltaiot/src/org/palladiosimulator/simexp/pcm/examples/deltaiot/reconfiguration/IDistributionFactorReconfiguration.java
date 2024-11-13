package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.Map;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;

public interface IDistributionFactorReconfiguration extends IDeltaIoToReconfiguration {
    void setDistributionFactorValuesToDefaults();

    void adjustDistributionFactor(Map<ProbabilisticBranchTransition, Double> factors);
}
