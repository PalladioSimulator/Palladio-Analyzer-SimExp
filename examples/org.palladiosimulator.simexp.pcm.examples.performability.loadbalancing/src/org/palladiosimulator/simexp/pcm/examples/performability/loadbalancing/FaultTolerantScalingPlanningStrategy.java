package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConstants;
import org.palladiosimulator.simexp.pcm.examples.performability.PolicySelectionException;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class FaultTolerantScalingPlanningStrategy<C> extends AbstractLoadBalancingScalingPlanningStrategy<C> {

    private static final String SCALE_IN_QVTO_NAME = "scaleIn";
    private static final String SCALE_OUT_SOURCE_QVTO_NAME = "scaleOut";

    public FaultTolerantScalingPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration,
            NodeRecoveryStrategy<C, QVTOReconfigurator> recoveryStrategy, Threshold lowerThresholdResponseTime,
            Threshold upperThresholdResponseTime) {
        super(responseTimeSpec, strategyConfiguration, recoveryStrategy, lowerThresholdResponseTime,
                upperThresholdResponseTime);

    }

    @Override
    public QVToReconfiguration planReconfigurationSteps(State source, Set<QVToReconfiguration> options,
            SharedKnowledge knowledge) throws PolicySelectionException {
        SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue>> sasState = (SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue>>) source;
        Double responseTime = retrieveResponseTime(sasState);
        Map<ResourceContainer, CategoricalValue> serverNodeStates = retrieveServerNodeStates(
                sasState.getPerceivedEnvironmentalState());

        /** scaling only allowed if all nodes are available */
        if (allNodesAreAvailable(serverNodeStates)) {
            if (isExceeded(responseTime)) {
                return lookupReconfigure(SCALE_OUT_SOURCE_QVTO_NAME, options);
            } else if (isSubceeded(responseTime)) {
                return lookupReconfigure(SCALE_IN_QVTO_NAME, options);
            }
        } else {
            /** workaround until node recovery is also accessible as qvto script */
            recoveryStrategy.execute(sasState, knowledge);
        }
        return emptyReconfiguration();
    }

    private boolean allNodesAreAvailable(Map<ResourceContainer, CategoricalValue> serverNodeStates) {
        return serverNodeStates.values()
            .stream()
            .allMatch(v -> v.get()
                .equals(PerformabilityStrategyConstants.NODE_STATE_AVAILABLE));
    }

}
