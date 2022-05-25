package org.palladiosimulator.simexp.core.valuefunction;

import org.palladiosimulator.simexp.core.evaluation.SampleModelIterator;

public interface ValueFunctionEstimator {
	
	public IValueFunction estimate(SampleModelIterator iterator);
}
