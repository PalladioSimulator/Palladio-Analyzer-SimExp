package org.palladiosimulator.simexp.core.reward;

import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;

public interface RewardEvaluator<R> {

    public Reward<R> evaluate(StateQuantity quantifiedState);
}
