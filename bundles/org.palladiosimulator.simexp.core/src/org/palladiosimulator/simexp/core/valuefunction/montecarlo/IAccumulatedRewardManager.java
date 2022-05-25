package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

public interface IAccumulatedRewardManager {

    public void append(String state, AccumulatedReward accReward);

    public AccumulatedReward getAccumulatedReward(String state);
    
}