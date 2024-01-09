package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PolicySelectionException;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public class LoadBalancingScalingPlanningStrategy<S> extends AbstractLoadBalancingScalingPlanningStrategy<S> {

    private static final String SCALE_IN_QVTO_NAME = "scaleIn";
    private static final String SCALE_OUT_SOURCE_QVTO_NAME = "scaleOut";

    public LoadBalancingScalingPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration,
            NodeRecoveryStrategy<S, QVTOReconfigurator> recoveryStrategy, Threshold lowerThresholdResponseTime,
            Threshold upperThresholdResponseTime) {
        super(responseTimeSpec, strategyConfiguration, recoveryStrategy, lowerThresholdResponseTime,
                upperThresholdResponseTime);
    }

    @Override
    public QVToReconfiguration planReconfigurationSteps(State<S> source, Set<QVToReconfiguration> options,
            SharedKnowledge knowledge) throws PolicySelectionException {
        SelfAdaptiveSystemState<S, QVTOReconfigurator> sasState = (SelfAdaptiveSystemState<S, QVTOReconfigurator>) source;

        Double responseTime = retrieveResponseTime(sasState);
        if (isExceeded(responseTime)) {
            return lookupReconfigure(SCALE_OUT_SOURCE_QVTO_NAME, options);
        } else if (isSubceeded(responseTime)) {
            return lookupReconfigure(SCALE_IN_QVTO_NAME, options);
        } else {
            return emptyReconfiguration();
        }
    }

}
