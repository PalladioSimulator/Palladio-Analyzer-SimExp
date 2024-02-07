package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.AbstractReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PolicySelectionException;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class FaultTolerantLoadBalancingPlanningStrategy<C, A> extends AbstractReconfigurationPlanningStrategy<C, A> {

    private static final String NODE_RECOVERY_QVTO_NAME = "nodeRecovery";

    public FaultTolerantLoadBalancingPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration, NodeRecoveryStrategy<C, A> recoveryStrategy) {
        super(responseTimeSpec, strategyConfiguration, recoveryStrategy);
    }

    @Override
    public QVToReconfiguration planReconfigurationSteps(State source, Set<QVToReconfiguration> options,
            SharedKnowledge knowledge) throws PolicySelectionException {
        SelfAdaptiveSystemState<C, A> sasState = (SelfAdaptiveSystemState<C, A>) source;
        /**
         * workarournd to implement node recovery behavior until we are able to realize this as QVTO
         * transformation
         * 
         */
        recoveryStrategy.execute(sasState, knowledge);

        return nodeRecovery(options);
    }

    private QVToReconfiguration nodeRecovery(Set<QVToReconfiguration> options) throws PolicySelectionException {
        return findReconfiguration(NODE_RECOVERY_QVTO_NAME, options)
            .orElseThrow(() -> new PolicySelectionException(missingQvtoTransformationMessage(NODE_RECOVERY_QVTO_NAME)));
    }

}
