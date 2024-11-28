package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

public class Triple<A, B, C> {

    private A first;
    private B second;
    private C third;

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public C third() {
        return third;
    }
}
