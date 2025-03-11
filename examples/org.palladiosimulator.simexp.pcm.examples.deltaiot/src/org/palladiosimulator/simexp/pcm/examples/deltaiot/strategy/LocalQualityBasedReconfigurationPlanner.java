package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.ITransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.MoteContextFilter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterCalculator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import com.google.common.math.DoubleMath;

public class LocalQualityBasedReconfigurationPlanner implements QualityBasedReconfigurationPlanner {

    private final ReconfigurationParameterCalculator paramCalculator;
    private final IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver;

    public LocalQualityBasedReconfigurationPlanner(DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess,
            IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver) {
        this.paramCalculator = new ReconfigurationParameterCalculator(reconfParamsRepo, modelAccess);
        this.reconfCustomizerResolver = reconfCustomizerResolver;
    }

    @Override
    public QVToReconfiguration planEnergyConsumption(SharedKnowledge knowledge) {
        IDeltaIoToReconfiguration reconfiguration = reconfCustomizerResolver.resolveDeltaIoTReconfCustomizer(knowledge);
        decreaseTransmissionPowerLocally((ITransmissionPowerReconfiguration) reconfiguration, knowledge);
        decreaseDistributionLocally((IDistributionFactorReconfiguration) reconfiguration, knowledge);
        return reconfiguration;
    }

    @Override
    public QVToReconfiguration planPacketLoss(SharedKnowledge knowledge) {
        IDeltaIoToReconfiguration reconfiguration = reconfCustomizerResolver.resolveDeltaIoTReconfCustomizer(knowledge);
        increaseTransmissionPowerLocally((ITransmissionPowerReconfiguration) reconfiguration, knowledge);
        increaseDistributionLocally((IDistributionFactorReconfiguration) reconfiguration, knowledge);
        return reconfiguration;
    }

    private void increaseDistributionLocally(IDistributionFactorReconfiguration reconfiguration,
            SharedKnowledge knowledge) {
        var motesFilter = new MoteContextFilter(knowledge);
        for (MoteContext each : motesFilter.motesWithTwoLinks()) {
            var linkToDecrease = motesFilter.linkWithSmallestSNR(each);
            if (isGreaterThanZero(linkToDecrease.distributionFactor)) {
                adjustDistributionFactor(linkToDecrease, each, reconfiguration);
            }
        }
    }

    private void decreaseDistributionLocally(IDistributionFactorReconfiguration reconfiguration,
            SharedKnowledge knowledge) {
        var motesFilter = new MoteContextFilter(knowledge);
        for (MoteContext each : motesFilter.motesWithTwoLinks()) {
            if (each.hasUnequalTransmissionPower()) {
                var linkToDecrease = motesFilter.linkWithHighestTransmissionPower(each);
                if (isGreaterThanZero(linkToDecrease.distributionFactor)) {
                    adjustDistributionFactor(linkToDecrease, each, reconfiguration);
                }
            }
        }
    }

    private void increaseTransmissionPowerLocally(ITransmissionPowerReconfiguration reconfiguration,
            SharedKnowledge knowledge) {
        var motesWithLowSNRLinks = new MoteContextFilter(knowledge).motesWithSNRLowerThan(Threshold.lessThan(0));
        for (MoteContext each : motesWithLowSNRLinks.keySet()) {
            var lowSNRLink = motesWithLowSNRLinks.get(each);
            if (isGreaterThanZero(lowSNRLink.distributionFactor) && lowSNRLink.transmissionPower < 15) {
                increaseTransmissionPower(each.mote, lowSNRLink, reconfiguration);
            }
        }
    }

    private void decreaseTransmissionPowerLocally(ITransmissionPowerReconfiguration reconfiguration,
            SharedKnowledge knowledge) {
        var motesWithHighSNRLinks = new MoteContextFilter(knowledge)
            .motesWithSNRHigherThan(Threshold.greaterThanOrEqualTo(0));
        for (MoteContext each : motesWithHighSNRLinks.keySet()) {
            var highSNRLink = motesWithHighSNRLinks.get(each);
            if (isGreaterThanZero(highSNRLink.distributionFactor) && highSNRLink.transmissionPower > 0) {
                decreaseTransmissionPower(each.mote, highSNRLink, reconfiguration);
            }
        }
    }

    private void decreaseTransmissionPower(AssemblyContext mote, WirelessLink link,
            ITransmissionPowerReconfiguration reconfiguration) {
        var adjustedParams = paramCalculator.computeDecreasedTransmissionPower(mote, link);
        reconfiguration.adjustTransmissionPower(adjustedParams);
    }

    private void increaseTransmissionPower(AssemblyContext mote, WirelessLink link,
            ITransmissionPowerReconfiguration reconfiguration) {
        var adjustedParams = paramCalculator.computeIncreasedTransmissionPower(mote, link);
        reconfiguration.adjustTransmissionPower(adjustedParams);
    }

    private void adjustDistributionFactor(WirelessLink linkToDecrease, MoteContext mote,
            IDistributionFactorReconfiguration reconfiguration) {
        var adjustedParams = paramCalculator.computeAdjustedDistributionFactors(linkToDecrease, mote);
        reconfiguration.adjustDistributionFactor(adjustedParams);
    }

    private boolean isGreaterThanZero(double distributionFactor) {
        var TOLERANCE = 0.0001;
        return DoubleMath.fuzzyEquals(distributionFactor, 0.0, TOLERANCE) == false && distributionFactor > 0;
    }

}
