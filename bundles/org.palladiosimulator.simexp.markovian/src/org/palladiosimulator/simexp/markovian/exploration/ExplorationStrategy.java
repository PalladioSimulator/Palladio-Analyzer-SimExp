package org.palladiosimulator.simexp.markovian.exploration;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface ExplorationStrategy<T> extends Policy<T> {

    @Override
    default T select(State<T> source, Set<T> options) {
        return select(options);
    }

    public T select(Set<T> options);
}
