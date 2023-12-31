package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;

public abstract class DerivableEnvironmentalDynamic<S, A> extends InductiveStateSpaceNavigator<S, A>
        implements EnvironmentalDynamic {

    protected boolean isHiddenProcess;

    @Override
    public boolean isHiddenProcess() {
        return isHiddenProcess;
    }

    public abstract void pursueExplorationStrategy();

    public abstract void pursueExploitationStrategy();

}
