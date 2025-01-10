package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Collection;
import java.util.Collections;

import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class OptimizableProvider implements IOptimizableProvider {
    private final Smodel smodel;

    public OptimizableProvider(Smodel smodel) {
        this.smodel = smodel;
    }

    @Override
    public Collection<Optimizable> getOptimizables() {
        return Collections.unmodifiableList(smodel.getOptimizables());
    }

    @Override
    public IExpressionCalculator getExpressionCalculator() {
        // TODO Auto-generated method stub
        return null;
    }

}
