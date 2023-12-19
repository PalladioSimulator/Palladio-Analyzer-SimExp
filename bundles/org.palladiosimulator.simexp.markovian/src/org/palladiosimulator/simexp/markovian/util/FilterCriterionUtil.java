package org.palladiosimulator.simexp.markovian.util;

import java.util.function.Predicate;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class FilterCriterionUtil {

    public static <T, A> Predicate<Transition<T, A>> withSource(State<T> source) {
        return t -> t.getSource()
            .equals(source);
    }

    public static <T, A> Predicate<Transition<T, A>> withTarget(State<T> target) {
        return t -> t.getTarget()
            .equals(target);
    }

    public static <T, A> Predicate<Transition<T, A>> withLabel(Action<A> label) {
        return t -> t.getLabel()
            .equals(label);
    }
}
