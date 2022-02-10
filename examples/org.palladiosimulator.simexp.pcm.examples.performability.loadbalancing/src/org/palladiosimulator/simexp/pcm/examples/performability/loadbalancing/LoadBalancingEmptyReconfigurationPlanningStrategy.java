package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.AbstractReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PolicySelectionException;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class LoadBalancingEmptyReconfigurationPlanningStrategy extends AbstractReconfigurationPlanningStrategy {
    
    public LoadBalancingEmptyReconfigurationPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration, NodeRecoveryStrategy recoveryStrategy) {
        super(responseTimeSpec, strategyConfiguration, recoveryStrategy);
    }

    @Override
    public QVToReconfiguration planReconfigurationSteps(State source, Set<QVToReconfiguration> options,
            SharedKnowledge knowledge) throws PolicySelectionException {
        return emptyReconfiguration();
    }

}
