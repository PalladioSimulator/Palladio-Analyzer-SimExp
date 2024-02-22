package org.palladiosimulator.simexp.markovian.activity;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface BasePolicy<A> {
    public String getId();

    public A select(State source, Set<A> options);

}
