package de.fzi.srp.core.process.util;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.model.markovmodel.markoventity.Transition;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public class MarkovianUtil {
	
	private static class TransitionMapper<U,T> {
		
		private final Set<U> transitions;
		
		public TransitionMapper(Set<U> transitions) {
			this.transitions = transitions;
		}
		
		public Set<T> apply(Function<U, T> mapping) {
			return transitions.stream().map(mapping).collect(Collectors.toSet());
		}
	}
	
	public static Set<Action<?>> toActions(Set<Transition> transitions) {
		return new TransitionMapper<Transition,Action<?>>(transitions).apply(t -> t.getLabel());
	}
	
	public static Set<ProbabilityMassFunction.Sample> toSamples(Set<Transition> transitions) {
		return new TransitionMapper<Transition,ProbabilityMassFunction.Sample>(transitions).apply(t -> ProbabilityMassFunction.Sample.of(t, t.getProbability()));
	}
		
	public static Transition maxTransition(Set<Transition> transitions) {
		//TODO ExceptionHandling
		return transitions.stream().max((t1, t2) -> Double.valueOf(t1.getProbability()).compareTo(t2.getProbability()))
								   .orElseThrow(() -> new RuntimeException(""));
	}

}
