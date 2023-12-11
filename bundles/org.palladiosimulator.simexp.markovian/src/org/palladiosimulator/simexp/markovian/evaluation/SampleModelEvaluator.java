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

public class SampleModelEvaluator {
	
	private static class TransitionCache {
		
		private final static int CACHE_SIZE = 100;
		
		private final Map<Integer, Transition<Double>> cache;
		
		public TransitionCache() {
			cache = new HashMap<>();
		}
		
		public Optional<Transition<Double>> findTransition(Sample<Double> sample) {
			Transition<Double> value = cache.get(sample.hashCode());
			return Optional.ofNullable(value);
		}
		
		public void put(Sample<Double> sample, Transition<Double> transition) {
			if (isCacheFull() == false) {
				cache.put(sample.hashCode(), transition);
			}
		}
		
		private boolean isCacheFull() {
			return cache.size() == CACHE_SIZE;
		}
	}
	
	private final TransitionCache transitionCache;
	private final MarkovModelAccessor<Double> modelAccessor;
	private final ProbabilityMassFunction initialStateDist;
	
	private SampleModelEvaluator(MarkovModel<Double> model, ProbabilityMassFunction initialStateDist) {
		this.transitionCache = new TransitionCache();
		this.modelAccessor = MarkovModelAccessor.of(model);
		this.initialStateDist = initialStateDist;
	}
	
	public static SampleModelEvaluator of(MarkovModel<Double> model, ProbabilityMassFunction initialStateDist) {
		return new SampleModelEvaluator(model, initialStateDist);
	}
	
	public List<MarkovianResult> evaluate(SampleModel<Double> sampleModelToEval) {
		return sampleModelToEval.getTrajectories().stream().map(this::evaluate).collect(Collectors.toList());
	}
	
	public MarkovianResult evaluate(Trajectory<Double> trajToEval) {
		double total = 0;
		double probability = computeInitial(trajToEval);
		
		for (Sample<Double> each : trajToEval.getSamplePath()) {
			total += each.getReward().getValue();
			probability *= getProbability(each);
		}
		
		Reward<Double> totalReward = MarkovModelFactory.get().createRewardSignal(total);
		return MarkovianResult.of(trajToEval).withProbability(probability)
											 .andReward(totalReward)
											 .build();
	}

	private double computeInitial(Trajectory<Double> trajToEval) {
		State<Double> initial = trajToEval.getSamplePath().get(0).getCurrent();
		return initialStateDist.probability(ProbabilityMassFunction.Sample.of(initial));
	}

	private double getProbability(Sample<Double> sample) {
		Transition<Double> result = transitionCache.findTransition(sample)
										   .orElse(queryMarkovModelAndCacheResult(sample));
		return result.getProbability();
	}

	private Transition<Double> queryMarkovModelAndCacheResult(Sample<Double> sample) {
		//TODO exception handling
		Transition<Double> result = modelAccessor.findTransition(sample.getCurrent(), sample.getNext())
										 .orElseThrow(() -> new RuntimeException(""));
		transitionCache.put(sample, result);
		return result;
	}
}
