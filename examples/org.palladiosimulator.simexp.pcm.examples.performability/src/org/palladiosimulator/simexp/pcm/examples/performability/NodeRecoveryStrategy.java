package org.palladiosimulator.simexp.pcm.examples.performability;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;

public interface NodeRecoveryStrategy<S, A> {

    void execute(SelfAdaptiveSystemState<S, A> sasState, SharedKnowledge knowledge);

}
