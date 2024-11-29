package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.function.Function;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public class SingleChromosome {

    private Function first;
    private Object second;
    private Optimizable third;

    public SingleChromosome(Function first, Object second, Optimizable third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public Function first() {
        return first;
    }

    public Object second() {
        return second;
    }

    public void setSecond(Object second) {
        this.second = second;
    }

    public Optimizable third() {
        return third;
    }
}
