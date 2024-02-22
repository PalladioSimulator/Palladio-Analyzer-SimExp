package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;

public abstract class DerivableEnvironmentalDynamic<A> extends InductiveStateSpaceNavigator<A>
        implements EnvironmentalDynamic {

    protected boolean isHiddenProcess;

    @Override
    public boolean isHiddenProcess() {
        return isHiddenProcess;
    }

    public abstract void pursueExplorationStrategy();

    public abstract void pursueExploitationStrategy();

}
