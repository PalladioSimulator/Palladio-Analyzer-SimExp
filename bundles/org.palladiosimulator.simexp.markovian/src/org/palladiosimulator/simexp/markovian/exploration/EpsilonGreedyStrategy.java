package org.palladiosimulator.simexp.markovian.exploration;

import static java.lang.Math.exp;
import static org.palladiosimulator.simexp.markovian.util.MarkovianUtil.maxTransition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.distribution.factory.ProbabilityDistributionFactory;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class EpsilonGreedyStrategy<T> implements Policy<Transition<T>> {

    private final static String STRATEGY_ID = "EpsilonGreedy";
    private final static double DEFAULT_INIT_EPSILON = 0.01;
    private final static int EXPLORATION_FACTOR = 100;

    private static class TransitionHelper<T> {

        private Set<Transition<T>> transitions;

        public TransitionHelper(Set<Transition<T>> transitions) {
            this.transitions = transitions;
        }

        public Transition<T> getMostProbableTransition() {
            return maxTransition(transitions);
        }

        public Set<Transition<T>> filterAllExcept(Transition<T> transition) {
            return transitions.stream()
                .filter(t -> t.equals(transition) == false)
                .collect(Collectors.toSet());
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
    public Transition<T> select(State<Transition<T>> source, Set<Transition<T>> options) {
        TransitionHelper<T> transHelper = new TransitionHelper<>(options);
        Transition<T> max = transHelper.getMostProbableTransition();
        ProbabilityMassFunction.Sample<Transition<T>> maxSample = ProbabilityMassFunction.Sample.of(max, epsilon);
        ProbabilityMassFunction.Sample<Transition<T>> otherSamples = ProbabilityMassFunction.Sample.of(null,
                1 - epsilon);
        Set<ProbabilityMassFunction.Sample<Transition<T>>> samples = new HashSet<>(
                Arrays.asList(maxSample, otherSamples));
        ProbabilityMassFunction<Transition<T>> pmfOver = ProbabilityDistributionFactory.INSTANCE.pmfOver(samples);
        ProbabilityMassFunction.Sample<Transition<T>> result = pmfOver.drawSample();
        if (result.getValue() == max) {
            return max;
        }
        return selectRandomly(transHelper.filterAllExcept(max));
    }

    public void adjust(int numberOfIteration) {
        epsilon = epsilonAdjustementLaw.apply(numberOfIteration);
    }

    private Transition<T> selectRandomly(Set<Transition<T>> transitions) {
        return new RandomizedStrategy<Transition<T>>().select(null, transitions);
    }

    private Function<Integer, Double> getDefaultEpsilonAdjustementLaw() {
        return value -> exp(value - EXPLORATION_FACTOR);
    }

    @Override
    public String getId() {
        return STRATEGY_ID;
    }

}
