package org.palladiosimulator.simexp.core.valuefunction;

import java.util.Iterator;
import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface ValueFunctionEstimator {

    ValueFunction estimate(Iterator<List<SimulatedExperience>> iterator);
}
