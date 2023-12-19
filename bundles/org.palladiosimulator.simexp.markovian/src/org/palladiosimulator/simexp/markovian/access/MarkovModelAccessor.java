package org.palladiosimulator.simexp.markovian.access;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;
import org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil;

public class MarkovModelAccessor<T, A, R> {

    private final MarkovModel<T, A, R> model;

    private MarkovModelAccessor(MarkovModel<T, A, R> model) {
        this.model = model;
    }

    public static <T, A, R> MarkovModelAccessor<T, A, R> of(MarkovModel<T, A, R> model) {
        return new MarkovModelAccessor<>(model);
    }

    private Stream<Transition<T, A>> getTransitionStream() {
        return model.getTransitions()
            .stream();
    }

    public Set<State<T>> getStates() {
        return new HashSet<State<T>>(model.getStateSpace());
    }

    public Set<Transition<T, A>> filterTransitions(Predicate<Transition<T, A>> criterion) {
        return getTransitionStream().filter(criterion)
            .collect(Collectors.toSet());
    }

    public Optional<Transition<T, A>> findTransition(State<T> source, Action<A> label) {
        Predicate<Transition<T, A>> filterPredicate = FilterCriterionUtil.<T, A> withSource(source)
            .and(FilterCriterionUtil.withLabel(label));
        return getTransitionStream().filter(filterPredicate)
            .findFirst();
    }

    public Optional<Transition<T, A>> findTransition(State<T> source, State<T> target) {
        return getTransitionStream().filter(FilterCriterionUtil.<T, A> withSource(source)
            .and(FilterCriterionUtil.withTarget(target)))
            .findFirst();
    }
}
