package de.fzi.srp.core.process.exploration;

import static de.fzi.srp.core.process.util.MarkovianUtil.maxTransition;
import static java.lang.Math.exp;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.model.markovmodel.markoventity.Transition;
import de.fzi.srp.core.process.markovian.activity.Policy;
import de.fzi.srp.distribution.factory.ProbabilityDistributionFactory;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public class EpsilonGreedyStrategy implements Policy<Transition> {
	
	private final static String STRATEGY_ID = "EpsilonGreedy";
	private final static double DEFAULT_INIT_EPSILON = 0.01;
	private final static int EXPLORATION_FACTOR = 100;
	
	private static class TransitionHelper {
		
		private Set<Transition> transitions;
		
		public TransitionHelper(Set<Transition> transitions) {
			this.transitions = transitions;
		}
		
		public Transition getMostProbableTransition() {
			return maxTransition(transitions);
		}
		
		public Set<Transition> filterAllExcept(Transition transition) {
			return transitions.stream().filter(t -> t.equals(transition) == false).collect(Collectors.toSet());
		}
	}
	
	private double epsilon;
	private Function<Integer, Double> epsilonAdjustementLaw;
	
	public EpsilonGreedyStrategy() {
		this.epsilon = DEFAULT_INIT_EPSILON;
		this.epsilonAdjustementLaw = getDefaultEpsilonAdjustementLaw();
	}
	
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
	
	public void setEpsilonAdjustementLaw(Function<Integer, Double> epsilonAdjustementLaw) {
		this.epsilonAdjustementLaw = epsilonAdjustementLaw;
	}
	

//	@Override
//	public String getId() {
//		return STRATEGY_ID;
//	}
//
//	@Override
//	public Transition selectTransition(State source, Set<Transition> options) {
//		return null;
//	}
	
	@Override
	public Transition select(State source, Set<Transition> options) {	
		TransitionHelper transHelper = new TransitionHelper(options);
		Transition max = transHelper.getMostProbableTransition();
		ProbabilityMassFunction.Sample maxSample = ProbabilityMassFunction.Sample.of(max, epsilon);
		ProbabilityMassFunction.Sample otherSamples = ProbabilityMassFunction.Sample.of(null, 1 - epsilon);
		ProbabilityMassFunction.Sample result = ProbabilityDistributionFactory.INSTANCE.pmfOver(maxSample, otherSamples).drawSample();
		if (result.getValue() == max) {
			return max;
		}
		return selectRandomly(transHelper.filterAllExcept(max));
	}
	
	public void adjust(int numberOfIteration) {
		epsilon = epsilonAdjustementLaw.apply(numberOfIteration);
	}
	
	private Transition selectRandomly(Set<Transition> transitions) {
		return new RandomizedStrategy<Transition>().select(null, transitions);
	}
	
	private Function<Integer, Double> getDefaultEpsilonAdjustementLaw() {
		return value -> exp(value - EXPLORATION_FACTOR);
	}

	@Override
	public String getId() {
		return STRATEGY_ID;
	}
	
}
