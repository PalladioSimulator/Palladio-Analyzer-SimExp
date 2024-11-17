package org.palladiosimulator.simexp.markovian.access;

import java.util.LinkedHashSet;
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

public class MarkovModelAccessor<A, R> {

    private final MarkovModel<A, R> model;

    private MarkovModelAccessor(MarkovModel<A, R> model) {
        this.model = model;
    }

    public static <A, R> MarkovModelAccessor<A, R> of(MarkovModel<A, R> model) {
        return new MarkovModelAccessor<>(model);
    }

    private Stream<Transition<A>> getTransitionStream() {
        return model.getTransitions()
            .stream();
    }

    public Set<State> getStates() {
        return new LinkedHashSet<>(model.getStateSpace());
    }

    public Set<Transition<A>> filterTransitions(Predicate<Transition<A>> criterion) {
        return getTransitionStream().filter(criterion)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Optional<Transition<A>> findTransition(State source, Action<A> label) {
        Predicate<Transition<A>> filterPredicate = FilterCriterionUtil.<A> withSource(source)
            .and(FilterCriterionUtil.withLabel(label));
        return getTransitionStream().filter(filterPredicate)
            .findFirst();
    }

    public Optional<Transition<A>> findTransition(State source, State target) {
        return getTransitionStream().filter(FilterCriterionUtil.<A> withSource(source)
            .and(FilterCriterionUtil.withTarget(target)))
            .findFirst();
    }
}
