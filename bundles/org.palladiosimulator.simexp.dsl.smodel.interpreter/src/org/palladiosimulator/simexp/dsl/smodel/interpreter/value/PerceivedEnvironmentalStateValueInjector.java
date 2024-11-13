package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public interface PerceivedEnvironmentalStateValueInjector {

    void injectPerceivedEnvironmentStateValues(List<InputValue<CategoricalValue>> currentValues);

}
