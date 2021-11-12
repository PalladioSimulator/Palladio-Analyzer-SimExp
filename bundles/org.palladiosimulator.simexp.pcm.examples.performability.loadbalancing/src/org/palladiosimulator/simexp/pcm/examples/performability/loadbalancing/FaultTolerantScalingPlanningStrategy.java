package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Map;
import java.util.Set;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.AbstractReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConstants;
import org.palladiosimulator.simexp.pcm.examples.performability.PolicySelectionException;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class FaultTolerantScalingPlanningStrategy extends AbstractReconfigurationPlanningStrategy{
    
    private static final Threshold UPPER_THRESHOLD = Threshold.lessThanOrEqualTo(2.0);
    private static final Threshold LOWER_THRESHOLD = Threshold.greaterThanOrEqualTo(1.0);
    private static final String SCALE_IN_QVTO_NAME = "scaleIn";
    private static final String SCALE_OUT_SOURCE_QVTO_NAME = "scaleOut";

    public FaultTolerantScalingPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration, NodeRecoveryStrategy recoveryStrategy) {
        super(responseTimeSpec, strategyConfiguration, recoveryStrategy);
    }

    @Override
    public QVToReconfiguration planReconfigurationSteps(State source, Set<QVToReconfiguration> options,
            SharedKnowledge knowledge) throws PolicySelectionException {
        SelfAdaptiveSystemState<?> sasState = (SelfAdaptiveSystemState<?>) source;
        Double responseTime = retrieveResponseTime(sasState);
        Map<ResourceContainer, CategoricalValue> serverNodeStates = retrieveServerNodeStates(
                sasState.getPerceivedEnvironmentalState());

        /** workaround until node recovery is also accessible as qvto script */
        recoveryStrategy.execute(sasState, knowledge);

        
        /** scaling only allowed if all nodes are available */
        if (allNodesAreAvailable(serverNodeStates)) {
            if (isExceeded(responseTime)) {
                return outSource(options);
            } else if (isSubceeded(responseTime)) {
                return scaleIn(options);
            } else {
            }
        }
        return emptyReconfiguration();
    }
    
    private boolean allNodesAreAvailable(Map<ResourceContainer, CategoricalValue> serverNodeStates) {
        return serverNodeStates.values()
            .stream()
            .allMatch(v -> v.get()
            .equals(PerformabilityStrategyConstants.NODE_STATE_AVAILABLE));
    }

    private boolean isExceeded(Double responseTime) {
        return UPPER_THRESHOLD.isNotSatisfied(responseTime);
    }

    private boolean isSubceeded(Double responseTime) {
        return LOWER_THRESHOLD.isNotSatisfied(responseTime);
    }

    private QVToReconfiguration scaleIn(Set<QVToReconfiguration> options) throws PolicySelectionException {
        return findReconfiguration(SCALE_IN_QVTO_NAME, options)
                .orElseThrow(() -> new PolicySelectionException(missingQvtoTransformationMessage(SCALE_IN_QVTO_NAME)));
    }

    private QVToReconfiguration outSource(Set<QVToReconfiguration> options) throws PolicySelectionException {
        return (QVToReconfiguration) findReconfiguration(SCALE_OUT_SOURCE_QVTO_NAME, options)
                .orElseThrow(() -> new PolicySelectionException(missingQvtoTransformationMessage(SCALE_OUT_SOURCE_QVTO_NAME)));
    }

}
