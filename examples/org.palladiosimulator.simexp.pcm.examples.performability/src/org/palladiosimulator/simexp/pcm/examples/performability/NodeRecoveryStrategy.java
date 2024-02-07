package org.palladiosimulator.simexp.pcm.examples.performability;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;

public interface NodeRecoveryStrategy<C, A> {

    void execute(SelfAdaptiveSystemState<C, A> sasState, SharedKnowledge knowledge);

}
