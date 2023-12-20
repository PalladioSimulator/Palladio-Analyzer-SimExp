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
import org.palladiosimulator.simexp.markovian.activity.BasePolicy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class EpsilonGreedyStrategy<S, A> implements BasePolicy<S, Transition<S, A>> {

    private final static String STRATEGY_ID = "EpsilonGreedy";
    private final static double DEFAULT_INIT_EPSILON = 0.01;
    private final static int EXPLORATION_FACTOR = 100;

    private static class TransitionHelper<S, A> {

        private Set<Transition<S, A>> transitions;

        public TransitionHelper(Set<Transition<S, A>> transitions) {
            this.transitions = transitions;
        }

        public Transition<S, A> getMostProbableTransition() {
            return maxTransition(transitions);
        }

        public Set<Transition<S, A>> filterAllExcept(Transition<S, A> transition) {
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
    public Transition<S, A> select(State<S> source, Set<Transition<S, A>> options) {
        TransitionHelper<S, A> transHelper = new TransitionHelper<>(options);
        Transition<S, A> max = transHelper.getMostProbableTransition();
        ProbabilityMassFunction.Sample<Transition<S, A>> maxSample = ProbabilityMassFunction.Sample.of(max, epsilon);
        ProbabilityMassFunction.Sample<Transition<S, A>> otherSamples = ProbabilityMassFunction.Sample.of(null,
                1 - epsilon);
        Set<ProbabilityMassFunction.Sample<Transition<S, A>>> samples = new HashSet<>(
                Arrays.asList(maxSample, otherSamples));
        ProbabilityMassFunction<Transition<S, A>> pmfOver = ProbabilityDistributionFactory.INSTANCE.pmfOver(samples);
        ProbabilityMassFunction.Sample<Transition<S, A>> result = pmfOver.drawSample();
        if (result.getValue() == max) {
            return max;
        }
        Set<Transition<S, A>> all = transHelper.filterAllExcept(max);
        return selectRandomly(all);
    }

    public void adjust(int numberOfIteration) {
        epsilon = epsilonAdjustementLaw.apply(numberOfIteration);
    }

    private Transition<S, A> selectRandomly(Set<Transition<S, A>> transitions) {
        RandomizedStrategy<S, Transition<S, A>> randomizedStrategy = new RandomizedStrategy<S, Transition<S, A>>();
        Transition<S, A> transition = randomizedStrategy.select(null, transitions);
        return transition;
    }

    private Function<Integer, Double> getDefaultEpsilonAdjustementLaw() {
        return value -> exp(value - EXPLORATION_FACTOR);
    }

    @Override
    public String getId() {
        return STRATEGY_ID;
    }

}
