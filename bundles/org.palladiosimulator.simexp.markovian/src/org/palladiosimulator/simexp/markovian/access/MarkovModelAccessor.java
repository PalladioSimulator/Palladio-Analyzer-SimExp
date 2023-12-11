package org.palladiosimulator.simexp.markovian.access;

import static org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil.withLabel;
import static org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil.withSource;
import static org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil.withTarget;

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

public class MarkovModelAccessor<T> {
	
	private final MarkovModel<T> model;
	
	private MarkovModelAccessor(MarkovModel<T> model) {
		this.model = model;
	}
	
	public  static <T> MarkovModelAccessor<T> of(MarkovModel<T> model) {
		return new MarkovModelAccessor<T>(model);
	}
	
	private Stream<Transition<T>> getTransitionStream() {
		return model.getTransitions().stream();
	}
	
	public Set<State<T>> getStates() {
		return new HashSet<State<T>>(model.getStateSpace());
	}
	
	public Set<Transition<T>> filterTransitions(Predicate<Transition<T>> criterion) {
		return getTransitionStream().filter(criterion).collect(Collectors.toSet());
	}
	
	public Optional<Transition<T>> findTransition(State<T> source, Action<T> label) {
		return getTransitionStream().filter(withSource(source).and(withLabel(label))).findFirst();
	}
	
	public Optional<Transition<T>> findTransition(State<T> source, State<T> target) {
		return getTransitionStream().filter(withSource(source).and(withTarget(target))).findFirst();
	}
}
