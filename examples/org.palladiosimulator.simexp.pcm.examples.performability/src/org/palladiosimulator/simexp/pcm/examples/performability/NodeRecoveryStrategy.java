package org.palladiosimulator.simexp.pcm.examples.performability;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;

public interface NodeRecoveryStrategy<C, A> {

    void execute(SelfAdaptiveSystemState<C, A, List<InputValue>> sasState, SharedKnowledge knowledge);

}
