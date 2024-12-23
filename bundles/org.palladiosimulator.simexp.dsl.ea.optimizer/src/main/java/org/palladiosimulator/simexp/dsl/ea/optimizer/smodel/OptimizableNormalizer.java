package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

public class OptimizableNormalizer {
    private final PowerUtil powerUtil = new PowerUtil();

    public SmodelBitChromosome toNormalized(Optimizable optimizable, IExpressionCalculator expressionCalculator) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            int minLength = powerUtil.minBitSizeForPower(setBounds.getValues()
                .size());
            return SmodelBitChromosome.of(new SmodelBitset(minLength), optimizable);
        }

        if (bounds instanceof RangeBounds rangeBounds) {
            int startValue = expressionCalculator.calculateInteger(rangeBounds.getStartValue());
            int endValue = expressionCalculator.calculateInteger(rangeBounds.getEndValue());
            int stepSize = expressionCalculator.calculateInteger(rangeBounds.getStepSize());

            List<Integer> ints = IntStream.iterate(startValue, n -> n + stepSize)
                .takeWhile(n -> n < endValue)
                .boxed()
                .collect(Collectors.toList());
            int minLength = powerUtil.minBitSizeForPower(ints.size());
            return SmodelBitChromosome.of(new SmodelBitset(minLength), optimizable);
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    public OptimizableValue<?> toOptimizable(SmodelBitChromosome chromosome) {
        return null;
    }
}
