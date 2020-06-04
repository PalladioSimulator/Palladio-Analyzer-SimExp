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

public class MarkovModelAccessor {
	
	private final MarkovModel model;
	
	private MarkovModelAccessor(MarkovModel model) {
		this.model = model;
	}
	
	public static MarkovModelAccessor of(MarkovModel model) {
		return new MarkovModelAccessor(model);
	}
	
	private Stream<Transition> getTransitionStream() {
		return model.getTransitions().stream();
	}
	
	public Set<State> getStates() {
		return new HashSet<State>(model.getStateSpace());
	}
	
	public Set<Transition> filterTransitions(Predicate<Transition> criterion) {
		return getTransitionStream().filter(criterion).collect(Collectors.toSet());
	}
	
	public Optional<Transition> findTransition(State source, Action<?> label) {
		return getTransitionStream().filter(withSource(source).and(withLabel(label))).findFirst();
	}
	
	public Optional<Transition> findTransition(State source, State target) {
		return getTransitionStream().filter(withSource(source).and(withTarget(target))).findFirst();
	}
}
