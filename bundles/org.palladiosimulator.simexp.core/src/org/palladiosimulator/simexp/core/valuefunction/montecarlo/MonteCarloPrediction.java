package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.DefaultSimulatedExperience;
import org.palladiosimulator.simexp.core.entity.StateAwareSimulatedExperience;
import org.palladiosimulator.simexp.core.evaluation.SampleModelIterator;
import org.palladiosimulator.simexp.core.valuefunction.IValueFunction;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;
import org.palladiosimulator.simexp.core.valuefunction.ValueFunctionEstimator;

public class MonteCarloPrediction implements ValueFunctionEstimator {

    private final IValueFunction valueFunction;
	private final MonteCaroEstimator predictionEstimator;
	
	MonteCarloPrediction(ValueFunction valueFunction, MonteCaroEstimator predictionEstimator) {
		this.valueFunction = valueFunction;
		this.predictionEstimator = predictionEstimator;
	}

	@Override
	public IValueFunction estimate(SampleModelIterator iterator) {	
		while(iterator.hasNext()) {
		    // todo; rework type cast
			List<StateAwareSimulatedExperience> traj = (List<StateAwareSimulatedExperience>) (Object) iterator.next();
			
			Set<String> distinctTrajStates = traj.stream()
					.map(each -> DefaultSimulatedExperience.getCurrentStateFrom(each))
					.distinct()
					.collect(Collectors.toSet());
			
			predictionEstimator.estimate(distinctTrajStates, traj);			
		}
		
		return valueFunction;
	}
}
