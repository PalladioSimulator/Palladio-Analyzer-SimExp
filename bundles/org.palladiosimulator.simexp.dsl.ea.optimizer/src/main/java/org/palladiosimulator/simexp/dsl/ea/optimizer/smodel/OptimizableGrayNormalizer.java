package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelGrayBitset;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public class OptimizableGrayNormalizer extends OptimizableNormalizer {

    public OptimizableGrayNormalizer(IExpressionCalculator expressionCalculator) {
        super(expressionCalculator);
    }

    @Override
    protected SmodelBitChromosome toNormalizedSet(Optimizable optimizable, int boundsSize, int minLength) {
        return SmodelBitChromosome.of(new SmodelGrayBitset(minLength), optimizable, boundsSize);
    }

}
