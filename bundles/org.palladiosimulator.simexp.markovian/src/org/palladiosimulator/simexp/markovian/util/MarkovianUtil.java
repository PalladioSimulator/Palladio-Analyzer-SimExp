package org.palladiosimulator.simexp.markovian.util;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class MarkovianUtil {

    private static class TransitionMapper<U, T> {

        private final Set<U> transitions;

        public TransitionMapper(Set<U> transitions) {
            this.transitions = transitions;
        }

        public Set<T> apply(Function<U, T> mapping) {
            return transitions.stream()
                .map(mapping)
                .collect(Collectors.toSet());
        }
    }

    public static <T, A> Set<Action<A>> toActions(Set<Transition<T, A>> transitions) {
        return new TransitionMapper<Transition<T, A>, Action<A>>(transitions).apply(t -> t.getLabel());
    }

    public static <T> Set<ProbabilityMassFunction.Sample<Transition<T, Double>>> toSamples(
            Set<Transition<T, Double>> transitions) {
        return new TransitionMapper<Transition<T, Double>, ProbabilityMassFunction.Sample<Transition<T, Double>>>(
                transitions).apply(t -> ProbabilityMassFunction.Sample.of(t, t.getProbability()));
    }

    public static <T, A> Transition<T, A> maxTransition(Set<Transition<T, A>> transitions) {
        // TODO ExceptionHandling
        return transitions.stream()
            .max((t1, t2) -> Double.valueOf(t1.getProbability())
                .compareTo(t2.getProbability()))
            .orElseThrow(() -> new RuntimeException(""));
    }

}
