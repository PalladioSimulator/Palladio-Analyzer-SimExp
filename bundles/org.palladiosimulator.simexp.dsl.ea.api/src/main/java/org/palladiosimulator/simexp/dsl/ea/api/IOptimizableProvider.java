package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Collection;

import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public interface IOptimizableProvider {
    Collection<Optimizable> getOptimizables();

    IExpressionCalculator getExpressionCalculator();
}
