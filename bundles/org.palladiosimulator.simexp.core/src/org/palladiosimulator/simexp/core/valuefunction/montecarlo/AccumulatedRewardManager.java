package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import java.util.HashMap;
import java.util.Map;


public class AccumulatedRewardManager implements IAccumulatedRewardManager {
    
    private final Map<String, AccumulatedReward> accRewards = new HashMap<String, AccumulatedReward>();
    
    
    @Override
    public void append(String state, AccumulatedReward accReward) {
        AccumulatedReward accRewardToUpdate = accRewards.get(state);
        if (accRewardToUpdate == null) {
            accRewards.put(state, accReward);
        } else {
            accRewardToUpdate.mergeWith(accReward);
        }
    }
    
    @Override
    public AccumulatedReward getAccumulatedReward(String state) {
        AccumulatedReward value = accRewards.get(state);
        if (value == null) {
            value = nonAccumulations();
        }
        return value;
    }
    
    private AccumulatedReward nonAccumulations() {
        AccumulatedReward accReward = new AccumulatedReward(0.0, 1);
        return accReward;
    }
}