package org.palladiosimulator.simexp.core.strategy.mape;

import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface Monitor {
    public void monitor(State source, SharedKnowledge knowledge);
}
