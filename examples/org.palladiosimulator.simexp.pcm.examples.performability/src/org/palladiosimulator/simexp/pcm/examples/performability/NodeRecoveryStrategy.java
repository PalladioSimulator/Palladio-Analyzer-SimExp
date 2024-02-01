package org.palladiosimulator.simexp.pcm.examples.performability;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;

public interface NodeRecoveryStrategy<S, A> {

    void execute(SelfAdaptiveSystemState<S, A, List<InputValue>> sasState, SharedKnowledge knowledge);

}
