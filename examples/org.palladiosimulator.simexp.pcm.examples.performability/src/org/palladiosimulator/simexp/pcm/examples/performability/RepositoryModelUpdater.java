package org.palladiosimulator.simexp.pcm.examples.performability;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;

public class RepositoryModelUpdater {
    
    
    public void updateBranchProbability(ProbabilisticBranchTransition probBranchTransition, double branchProbability) {
        probBranchTransition.setBranchProbability(branchProbability);
    }
}
