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

public class MarkovModelAccessor<S, A, R> {

    private final MarkovModel<S, A, R> model;

    private MarkovModelAccessor(MarkovModel<S, A, R> model) {
        this.model = model;
    }

    public static <T, A, R> MarkovModelAccessor<T, A, R> of(MarkovModel<T, A, R> model) {
        return new MarkovModelAccessor<>(model);
    }

    private Stream<Transition<S, A>> getTransitionStream() {
        return model.getTransitions()
            .stream();
    }

    public Set<State<S>> getStates() {
        return new HashSet<State<S>>(model.getStateSpace());
    }

    public Set<Transition<S, A>> filterTransitions(Predicate<Transition<S, A>> criterion) {
        return getTransitionStream().filter(criterion)
            .collect(Collectors.toSet());
    }

    public Optional<Transition<S, A>> findTransition(State<S> source, Action<A> label) {
        Predicate<Transition<S, A>> filterPredicate = FilterCriterionUtil.<S, A> withSource(source)
            .and(FilterCriterionUtil.withLabel(label));
        return getTransitionStream().filter(filterPredicate)
            .findFirst();
    }

    public Optional<Transition<S, A>> findTransition(State<S> source, State<S> target) {
        return getTransitionStream().filter(FilterCriterionUtil.<S, A> withSource(source)
            .and(FilterCriterionUtil.withTarget(target)))
            .findFirst();
    }
}
