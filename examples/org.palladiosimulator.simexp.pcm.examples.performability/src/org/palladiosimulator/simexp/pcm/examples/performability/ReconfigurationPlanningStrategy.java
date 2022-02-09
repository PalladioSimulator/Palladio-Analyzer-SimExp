package org.palladiosimulator.simexp.pcm.examples.performability;

import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public interface ReconfigurationPlanningStrategy {
    
    
    /**  workaround until all steps are supported as QVTO transformation will return an empty qvto transformation 
     * @param responseTimeSpec TODO
     * @throws PolicySelectionException */
    QVToReconfiguration planReconfigurationSteps(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) throws PolicySelectionException;

    
}
