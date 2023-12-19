package org.palladiosimulator.simexp.markovian.activity;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface Policy<T, P, A extends Action<P>> {

    public String getId();

    public A select(State<T> source, Set<A> options);
}
