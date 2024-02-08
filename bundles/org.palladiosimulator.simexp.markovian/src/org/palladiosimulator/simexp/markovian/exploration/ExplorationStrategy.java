package org.palladiosimulator.simexp.markovian.exploration;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface ExplorationStrategy<A, Aa extends Action<A>> extends Policy<A, Aa> {

    @Override
    default Aa select(State source, Set<Aa> options) {
        return select(options);
    }

    public Aa select(Set<Aa> options);
}
