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

public class SampleModelEvaluator<S, A, O> {

    private static class TransitionCache<S, A, O> {

        private final static int CACHE_SIZE = 100;

        private final Map<Integer, Transition<S, A>> cache;

        public TransitionCache() {
            cache = new HashMap<>();
        }

        public Optional<Transition<S, A>> findTransition(Sample<S, A, Double, O> sample) {
            Transition<S, A> value = cache.get(sample.hashCode());
            return Optional.ofNullable(value);
        }

        public void put(Sample<S, A, Double, O> sample, Transition<S, A> transition) {
            if (isCacheFull() == false) {
                cache.put(sample.hashCode(), transition);
            }
        }

        private boolean isCacheFull() {
            return cache.size() == CACHE_SIZE;
        }
    }

    private final TransitionCache<S, A, O> transitionCache;
    private final MarkovModelAccessor<S, A, Double> modelAccessor;
    private final ProbabilityMassFunction<State<S>> initialStateDist;

    private SampleModelEvaluator(MarkovModel<S, A, Double> model, ProbabilityMassFunction<State<S>> initialStateDist) {
        this.transitionCache = new TransitionCache<>();
        this.modelAccessor = MarkovModelAccessor.of(model);
        this.initialStateDist = initialStateDist;
    }

    public static <S, A, O> SampleModelEvaluator<S, A, O> of(MarkovModel<S, A, Double> model,
            ProbabilityMassFunction<State<S>> initialStateDist) {
        return new SampleModelEvaluator<>(model, initialStateDist);
    }

    public List<MarkovianResult<S, A, Double, O>> evaluate(SampleModel<S, A, Double, O> sampleModelToEval) {
        return sampleModelToEval.getTrajectories()
            .stream()
            .map(this::evaluate)
            .collect(Collectors.toList());
    }

    public MarkovianResult<S, A, Double, O> evaluate(Trajectory<S, A, Double, O> trajToEval) {
        double total = 0;
        double probability = computeInitial(trajToEval);

        for (Sample<S, A, Double, O> each : trajToEval.getSamplePath()) {
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

    private double computeInitial(Trajectory<S, A, Double, O> trajToEval) {
        Sample<S, A, Double, O> sample = trajToEval.getSamplePath()
            .get(0);
        State<S> initial = sample.getCurrent();
        ProbabilityMassFunction.Sample<State<S>> of = ProbabilityMassFunction.Sample.of(initial);
        return initialStateDist.probability(of);
    }

    private double getProbability(Sample<S, A, Double, O> sample) {
        Transition<S, A> result = transitionCache.findTransition(sample)
            .orElse(queryMarkovModelAndCacheResult(sample));
        return result.getProbability();
    }

    private Transition<S, A> queryMarkovModelAndCacheResult(Sample<S, A, Double, O> sample) {
        // TODO exception handling
        Transition<S, A> result = modelAccessor.findTransition(sample.getCurrent(), sample.getNext())
            .orElseThrow(() -> new RuntimeException(""));
        transitionCache.put(sample, result);
        return result;
    }
}
