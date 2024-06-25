package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.Map;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public interface IDistributionFactorReconfiguration extends QVToReconfiguration {
    void setDistributionFactorValuesToDefaults();

    void adjustDistributionFactor(Map<ProbabilisticBranchTransition, Double> factors);
}
