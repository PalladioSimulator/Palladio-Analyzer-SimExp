package org.palladiosimulator.simexp.core.valuefunction;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

public interface IValueFunction {

    Double getExpectedRewardFor(String state);

    Double getExpectedRewardFor(SelfAdaptiveSystemState<?> state);

}