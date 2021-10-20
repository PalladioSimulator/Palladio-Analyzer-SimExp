package org.palladiosimulator.simexp.pcm.examples.performability;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

public interface NodeRecoveryStrategy {

    void execute(PcmSelfAdaptiveSystemState sasState, SharedKnowledge knowledge);
    
}
