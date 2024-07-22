package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.STATE_KEY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.filterMotesWithWirelessLinks;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.requirePcmSelfAdaptiveSystemState;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.EmptyQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.ITransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.MoteContextFilter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.ReconfigurationParameterCalculator;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.math.DoubleMath;

public class DeltaIoTDefaultReconfigurationStrategy
        extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {

    private final static double UNIFORM_DIST_VALUE = 0.5;

    private final ReconfigurationParameterCalculator paramCalculator;
    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;
    private final SimulationParameters simulationParameters;
    private final SystemConfigurationTracker systemConfigurationTracker;
    private final IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver;

    public DeltaIoTDefaultReconfigurationStrategy(DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, SimulationParameters simulationParameters,
            SystemConfigurationTracker systemConfigurationTracker,
            IDeltaIoToReconfCustomizerResolver reconfCustomizerResolver) {
        this.paramCalculator = new ReconfigurationParameterCalculator(reconfParamsRepo, modelAccess);
        this.modelAccess = modelAccess;
        this.simulationParameters = simulationParameters;
        this.systemConfigurationTracker = systemConfigurationTracker;
        this.reconfCustomizerResolver = reconfCustomizerResolver;
    }

    @Override
    public String getId() {
        return "DefaultDeltaIoTStrategy";
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {
        systemConfigurationTracker.prepareNetworkConfig();

        requirePcmSelfAdaptiveSystemState(source);

        PcmSelfAdaptiveSystemState state = PcmSelfAdaptiveSystemState.class.cast(source);

        knowledge.store(STATE_KEY, state);

        addMonitoredEnvironmentValues(state, knowledge);

        systemConfigurationTracker.processNetworkConfig(knowledge);
    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        MoteContextFilter moteFiler = new MoteContextFilter(knowledge);
        for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
            for (WirelessLink eachLink : eachMote.links) {
                if (isPowerOptimal(eachLink) == false) {
                    return true;
                }
            }

            if (eachMote.hasTwoLinks()) {
                if (eachMote.hasUnequalTransmissionPower()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        IDeltaIoToReconfiguration reconfiguration = reconfCustomizerResolver
            .resolveDeltaIoTReconfCustomizer(options);
        if (reconfiguration instanceof IDistributionFactorReconfiguration) {
            IDistributionFactorReconfiguration distributionFactorReconfiguration = (IDistributionFactorReconfiguration) reconfiguration;
            distributionFactorReconfiguration.setDistributionFactorValuesToDefaults();
        }

        MoteContextFilter moteFiler = new MoteContextFilter(knowledge);
        for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
            for (WirelessLink eachLink : eachMote.links) {
                if (reconfiguration instanceof ITransmissionPowerReconfiguration) {
                    ITransmissionPowerReconfiguration transmissionPowerReconfiguration = (ITransmissionPowerReconfiguration) reconfiguration;
                    if (eachLink.SNR > 0 && eachLink.transmissionPower > 0) {
                        decreaseTransmissionPower(eachMote.mote, eachLink, transmissionPowerReconfiguration);
                    } else if (eachLink.SNR < 0 && eachLink.transmissionPower < 15) {
                        increaseTransmissionPower(eachMote.mote, eachLink, transmissionPowerReconfiguration);
                    }
                }
            }

            if (eachMote.hasTwoLinks()) {
                if (reconfiguration instanceof IDistributionFactorReconfiguration) {
                    IDistributionFactorReconfiguration distributionFactorReconfiguration = (IDistributionFactorReconfiguration) reconfiguration;

                    if (eachMote.hasUnequalTransmissionPower()) {
                        Iterator<WirelessLink> iterator = eachMote.links.iterator();
                        WirelessLink left = iterator.next();
                        double leftTransmissionPower = left.transmissionPower;
                        double leftDistributionFactor = left.distributionFactor;
                        WirelessLink right = iterator.next();
                        double rightTransmissionPower = right.transmissionPower;
                        double rightDistributionFactor = right.distributionFactor;

                        if (isEqualToOne(leftDistributionFactor) && isEqualToOne(rightDistributionFactor)) {
                            setDistributionFactorsUniformally(distributionFactorReconfiguration, eachMote.mote);
                        }

                        if (leftTransmissionPower > rightTransmissionPower
                                && isSmallerThanOne(leftDistributionFactor)) {
                            adjustDistributionFactor(right, eachMote, distributionFactorReconfiguration);
                        } else if (isSmallerThanOne(rightDistributionFactor)) {
                            adjustDistributionFactor(left, eachMote, distributionFactorReconfiguration);
                        }
                    }
                }
            }
        }

        return reconfiguration;
    }

    private void setDistributionFactorsUniformally(IDistributionFactorReconfiguration reconfiguration,
            AssemblyContext mote) {
        List<ProbabilisticBranchTransition> communicatingBranches = modelAccess.retrieveCommunicatingBranches(mote);
        // communicatingBranches.forEach(branch -> setDistributionFactorIfPresent(branch,
        // UNIFORM_DIST_VALUE));
        for (ProbabilisticBranchTransition branch : communicatingBranches) {
            // reconfiguration.setDistributionFactorIfPresent(branch, UNIFORM_DIST_VALUE);
            Map<ProbabilisticBranchTransition, Double> factors = Collections.singletonMap(branch, UNIFORM_DIST_VALUE);
            reconfiguration.adjustDistributionFactor(factors);
        }
    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return EmptyQVToReconfiguration.empty();
    }

    private void addMonitoredEnvironmentValues(PcmSelfAdaptiveSystemState state, SharedKnowledge knowledge) {
        var motesToLinks = filterMotesWithWirelessLinks(modelAccess, state);
        for (AssemblyContext each : motesToLinks.keySet()) {
            var moteContext = new MoteContext(modelAccess, each, motesToLinks.get(each));
            knowledge.store(moteContext.getId(), moteContext);
        }
    }

    private boolean isPowerOptimal(WirelessLink link) {
        return (link.SNR > 0 && link.transmissionPower > 0) || (link.SNR < 0 && link.transmissionPower < 15);
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

    private boolean isSmallerThanOne(double distributionFactor) {
        return distributionFactor < 1.0 && isEqualToOne(distributionFactor) == false;
    }

    private boolean isEqualToOne(double distributionFactor) {
        var TOLERANCE = 0.0001;
        return DoubleMath.fuzzyEquals(distributionFactor, 1.0, TOLERANCE);
    }

}
