package org.palladiosimulator.simexp.core.valuefunction;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.evaluation.SampleModelIterator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MonteCarloPrediction implements ValueFunctionEstimator {

	private abstract class MonteCaroEstimator implements BiConsumer<Set<String>, List<SimulatedExperience>> {
			
		@Override
		public void accept(Set<String> t, List<SimulatedExperience> u) {
			estimate(t,u);
		}
		
		public abstract void estimate(Set<String> t, List<SimulatedExperience> u);
		
	}
	
	private static class AccumulatedReward {
		
		private double total = 0;
		private int accummulations = 0;
		
		public static AccumulatedReward nonAccumulations() {
			AccumulatedReward accReward = new AccumulatedReward();
			accReward.accummulations = 1;
			return accReward;
		}
		
		public void append(double reward) {
			total += reward;
			accummulations++;
		}
		
		public void mergeWith(AccumulatedReward accReward) {
			total += accReward.total;
			accummulations += accReward.accummulations;
		}
		
		public double calculateAverage() {
			return total / accummulations;
		}
		
	}
	
	private static class AccumulatedRewardManager {
		
		private final Map<String, AccumulatedReward> accRewards = Maps.newHashMap();
		
		public void append(String state, AccumulatedReward accReward) {
			AccumulatedReward accRewardToUpdate = accRewards.get(state);
			if (accRewardToUpdate == null) {
				accRewards.put(state, accReward);
			} else {
				accRewardToUpdate.mergeWith(accReward);
			}
		}
		
		public AccumulatedReward getAccumulatedRewardFor(String state) {
			return Optional.ofNullable(accRewards.get(state)).orElse(AccumulatedReward.nonAccumulations());
		}
		
	}
	
	private final ValueFunction valueFunction;
	private final AccumulatedRewardManager accRewardManager;
	
	private MonteCaroEstimator predictionEstimator;
	
	private MonteCarloPrediction() {
		this.valueFunction = new ValueFunction();
		this.accRewardManager = new AccumulatedRewardManager();
	}
	
	public static MonteCarloPrediction firstVisitEstimation() {
		MonteCarloPrediction estimation = new MonteCarloPrediction();
		estimation.predictionEstimator = estimation.firstVisitEstimator();
		return estimation;
	}
	
	@Override
	public ValueFunction estimate(SampleModelIterator iterator) {	
		while(iterator.hasNext()) {
			List<SimulatedExperience> traj = iterator.next();
			
			Set<String> distinctTrajStates = traj.stream()
					.map(each -> DefaultSimulatedExperience.getCurrentStateFrom(each))
					.distinct()
					.collect(Collectors.toSet());
			
			predictionEstimator.estimate(distinctTrajStates, traj);			
		}
		
		return valueFunction;
	}
	
	private MonteCaroEstimator firstVisitEstimator() {
		return new MonteCaroEstimator() {
			
			@Override
			public void estimate(Set<String> states, List<SimulatedExperience> traj) {
				for (String each : states) {
					AccumulatedReward accReward = calculateAccRewardAfterFirstVisit(each, traj);
					
					accRewardManager.append(each, accReward);
					
					AccumulatedReward currentAccReward = accRewardManager.getAccumulatedRewardFor(each);
					valueFunction.updateExpectedReward(each, currentAccReward.calculateAverage());
				}
			}

			private AccumulatedReward calculateAccRewardAfterFirstVisit(String state, List<SimulatedExperience> traj) {
				AccumulatedReward accReward = new AccumulatedReward();
				for (SimulatedExperience each : extractSubTrajectoryAfterFirstVisit(state, traj)) {
					double reward = Double.parseDouble(each.getReward());
					accReward.append(reward);
				}
				
				return accReward;
			}
			
			private List<SimulatedExperience> extractSubTrajectoryAfterFirstVisit(String state, List<SimulatedExperience> traj) {
				List<SimulatedExperience> subTraj = Lists.newArrayList();
				
				Iterator<SimulatedExperience> iterator = traj.iterator();
				while (iterator.hasNext()) {
					SimulatedExperience next = iterator.next();

					if (DefaultSimulatedExperience.getCurrentStateFrom(next).equals(state)) {
						subTraj.add(next);
						iterator.forEachRemaining(subTraj::add);
						return subTraj;
					}
				}
				
				return subTraj;
			}
			
		};
	}

}
