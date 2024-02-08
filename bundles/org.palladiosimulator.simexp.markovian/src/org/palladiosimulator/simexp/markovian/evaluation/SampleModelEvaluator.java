package org.palladiosimulator.simexp.markovian.evaluation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.access.MarkovModelAccessor;
import org.palladiosimulator.simexp.markovian.model.factory.MarkovModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;
import org.palladiosimulator.simexp.markovian.type.MarkovianResult;

public class SampleModelEvaluator<A> {

    private static class TransitionCache<A> {

        private final static int CACHE_SIZE = 100;

        private final Map<Integer, Transition<A>> cache;

        public TransitionCache() {
            cache = new HashMap<>();
        }

        public Optional<Transition<A>> findTransition(Sample<A, Double> sample) {
            Transition<A> value = cache.get(sample.hashCode());
            return Optional.ofNullable(value);
        }

        public void put(Sample<A, Double> sample, Transition<A> transition) {
            if (isCacheFull() == false) {
                cache.put(sample.hashCode(), transition);
            }
        }

        private boolean isCacheFull() {
            return cache.size() == CACHE_SIZE;
        }
    }

    private final TransitionCache<A> transitionCache;
    private final MarkovModelAccessor<A, Double> modelAccessor;
    private final ProbabilityMassFunction<State> initialStateDist;

    private SampleModelEvaluator(MarkovModel<A, Double> model, ProbabilityMassFunction<State> initialStateDist) {
        this.transitionCache = new TransitionCache<>();
        this.modelAccessor = MarkovModelAccessor.of(model);
        this.initialStateDist = initialStateDist;
    }

    public static <A> SampleModelEvaluator<A> of(MarkovModel<A, Double> model,
            ProbabilityMassFunction<State> initialStateDist) {
        return new SampleModelEvaluator<>(model, initialStateDist);
    }

    public List<MarkovianResult<A, Double>> evaluate(SampleModel<A, Double> sampleModelToEval) {
        return sampleModelToEval.getTrajectories()
            .stream()
            .map(this::evaluate)
            .collect(Collectors.toList());
    }

    public MarkovianResult<A, Double> evaluate(Trajectory<A, Double> trajToEval) {
        double total = 0;
        double probability = computeInitial(trajToEval);

        for (Sample<A, Double> each : trajToEval.getSamplePath()) {
            total += each.getReward()
                .getValue();
            probability *= getProbability(each);
        }

        Reward<Double> totalReward = new MarkovModelFactory().createRewardSignal(total);
        return MarkovianResult.of(trajToEval)
            .withProbability(probability)
            .andReward(totalReward)
            .build();
    }

    private double computeInitial(Trajectory<A, Double> trajToEval) {
        Sample<A, Double> sample = trajToEval.getSamplePath()
            .get(0);
        State initial = sample.getCurrent();
        ProbabilityMassFunction.Sample<State> of = ProbabilityMassFunction.Sample.of(initial);
        return initialStateDist.probability(of);
    }

    private double getProbability(Sample<A, Double> sample) {
        Transition<A> result = transitionCache.findTransition(sample)
            .orElse(queryMarkovModelAndCacheResult(sample));
        return result.getProbability();
    }

    private Transition<A> queryMarkovModelAndCacheResult(Sample<A, Double> sample) {
        // TODO exception handling
        Transition<A> result = modelAccessor.findTransition(sample.getCurrent(), sample.getNext())
            .orElseThrow(() -> new RuntimeException(""));
        transitionCache.put(sample, result);
        return result;
    }
}
