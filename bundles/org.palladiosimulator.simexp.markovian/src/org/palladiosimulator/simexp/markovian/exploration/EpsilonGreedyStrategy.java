package org.palladiosimulator.simexp.markovian.exploration;

import static java.lang.Math.exp;
import static org.palladiosimulator.simexp.markovian.util.MarkovianUtil.maxTransition;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.distribution.factory.ProbabilityDistributionFactory;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.activity.BasePolicy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class EpsilonGreedyStrategy<A> implements BasePolicy<Transition<A>> {

    private final static String STRATEGY_ID = "EpsilonGreedy";
    private final static double DEFAULT_INIT_EPSILON = 0.01;
    private final static int EXPLORATION_FACTOR = 100;

    private static class TransitionHelper<A> {

        private Set<Transition<A>> transitions;

        public TransitionHelper(Set<Transition<A>> transitions) {
            this.transitions = transitions;
        }

        public Transition<A> getMostProbableTransition() {
            return maxTransition(transitions);
        }

        public Set<Transition<A>> filterAllExcept(Transition<A> transition) {
            return transitions.stream()
                .filter(t -> t.equals(transition) == false)
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
    public Transition<A> select(State source, Set<Transition<A>> options) {
        TransitionHelper<A> transHelper = new TransitionHelper<>(options);
        Transition<A> max = transHelper.getMostProbableTransition();
        ProbabilityMassFunction.Sample<Transition<A>> maxSample = ProbabilityMassFunction.Sample.of(max, epsilon);
        ProbabilityMassFunction.Sample<Transition<A>> otherSamples = ProbabilityMassFunction.Sample.of(null,
                1 - epsilon);
        Set<ProbabilityMassFunction.Sample<Transition<A>>> samples = new LinkedHashSet<>(
                Arrays.asList(maxSample, otherSamples));
        ProbabilityMassFunction<Transition<A>> pmfOver = ProbabilityDistributionFactory.INSTANCE.pmfOver(samples);
        ProbabilityMassFunction.Sample<Transition<A>> result = pmfOver.drawSample();
        if (result.getValue() == max) {
            return max;
        }
        Set<Transition<A>> all = transHelper.filterAllExcept(max);
        return selectRandomly(all);
    }

    public void adjust(int numberOfIteration) {
        epsilon = epsilonAdjustementLaw.apply(numberOfIteration);
    }

    private Transition<A> selectRandomly(Set<Transition<A>> transitions) {
        RandomizedStrategy<Transition<A>> randomizedStrategy = new RandomizedStrategy<>();
        Transition<A> transition = randomizedStrategy.select(null, transitions);
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
