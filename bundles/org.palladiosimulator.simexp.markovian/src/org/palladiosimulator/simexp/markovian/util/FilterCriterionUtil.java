package org.palladiosimulator.simexp.markovian.util;

import java.util.function.Predicate;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class FilterCriterionUtil {
	
	public static Predicate<Transition> withSource(State source) {
		return t -> t.getSource().equals(source);
	}
	
	public static Predicate<Transition> withTarget(State target) {
		return t -> t.getTarget().equals(target);
	}
	
	public static Predicate<Transition> withLabel(Action<?> label) {
		return t -> t.getLabel().equals(label);
	}
}
