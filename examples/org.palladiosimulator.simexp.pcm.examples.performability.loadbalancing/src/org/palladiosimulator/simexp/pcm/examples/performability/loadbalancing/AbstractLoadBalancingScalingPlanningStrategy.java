package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.AbstractReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PolicySelectionException;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public abstract class AbstractLoadBalancingScalingPlanningStrategy<C>
        extends AbstractReconfigurationPlanningStrategy<C, QVTOReconfigurator> {

    private final Threshold lowerResponseTimeThreshold;
    private final Threshold upperResponseTimeThreshold;

    public AbstractLoadBalancingScalingPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration,
            NodeRecoveryStrategy<C, QVTOReconfigurator> recoveryStrategy, Threshold lowerThresholdResponseTime,
            Threshold upperThresholdResponseTime) {
        super(responseTimeSpec, strategyConfiguration, recoveryStrategy);
        this.lowerResponseTimeThreshold = lowerThresholdResponseTime;
        this.upperResponseTimeThreshold = upperThresholdResponseTime;
    }

    protected boolean isSubceeded(Double responseTime) {
        return lowerResponseTimeThreshold.isNotSatisfied(responseTime);
    }

    protected boolean isExceeded(Double responseTime) {
        return upperResponseTimeThreshold.isNotSatisfied(responseTime);
    }

    protected QVToReconfiguration lookupReconfigure(String qvtoScriptName, Set<QVToReconfiguration> options)
            throws PolicySelectionException {
        return findReconfiguration(qvtoScriptName, options)
            .orElseThrow(() -> new PolicySelectionException(missingQvtoTransformationMessage(qvtoScriptName)));
    }

}
