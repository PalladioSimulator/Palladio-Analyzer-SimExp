package org.palladiosimulator.simexp.markovian.util;

import java.util.function.Predicate;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class FilterCriterionUtil {

    public static <A> Predicate<Transition<A>> withSource(State source) {
        return t -> t.getSource()
            .equals(source);
    }

    public static <A> Predicate<Transition<A>> withTarget(State target) {
        return t -> t.getTarget()
            .equals(target);
    }

    public static <A> Predicate<Transition<A>> withLabel(Action<A> label) {
        return t -> t.getLabel()
            .equals(label);
    }
}
