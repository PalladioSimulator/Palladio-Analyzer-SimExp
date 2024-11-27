package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

//public record Pair<A, B>(A first, B second) {
public class Pair<A, B> {

    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;

    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public void setSecond(B newValue) {
        second = newValue;
    }

}
