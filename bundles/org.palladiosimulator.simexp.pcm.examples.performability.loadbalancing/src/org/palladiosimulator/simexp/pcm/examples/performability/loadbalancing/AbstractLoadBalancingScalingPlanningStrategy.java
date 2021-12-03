package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.AbstractReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PolicySelectionException;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public abstract class AbstractLoadBalancingScalingPlanningStrategy extends AbstractReconfigurationPlanningStrategy {
    
    private final Threshold lowerResponseTimeThreshold;
    private final Threshold upperResponseTimeThreshold;
    

    public AbstractLoadBalancingScalingPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration, NodeRecoveryStrategy recoveryStrategy
            , Threshold lowerThresholdResponseTime, Threshold upperThresholdResponseTime) {
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


    protected QVToReconfiguration lookupReconfigure(String qvtoScriptName, Set<QVToReconfiguration> options) throws PolicySelectionException {
        return (QVToReconfiguration) findReconfiguration(qvtoScriptName, options)
                .orElseThrow(() -> new PolicySelectionException(missingQvtoTransformationMessage(qvtoScriptName)));
    }

}
