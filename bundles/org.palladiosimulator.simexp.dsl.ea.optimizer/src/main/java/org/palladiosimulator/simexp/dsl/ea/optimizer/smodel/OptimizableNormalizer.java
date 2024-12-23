package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableProcessingException;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

public class OptimizableNormalizer {
    private final PowerUtil powerUtil;
    private final IExpressionCalculator expressionCalculator;

    public OptimizableNormalizer(IExpressionCalculator expressionCalculator) {
        this.powerUtil = new PowerUtil();
        this.expressionCalculator = expressionCalculator;
    }

    public List<SmodelBitChromosome> toNormalized(List<Optimizable> optimizables) {
        return optimizables.stream()
            .map(o -> toNormalized(o))
            .collect(Collectors.toList());
    }

    public SmodelBitChromosome toNormalized(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            int minLength = powerUtil.minBitSizeForPower(setBounds.getValues()
                .size());
            return SmodelBitChromosome.of(new SmodelBitset(minLength), optimizable);
        }

        if (bounds instanceof RangeBounds rangeBounds) {
            DataType dataType = optimizable.getDataType();
            switch (dataType) {
            case INT:
                return toNormalizedRangeInt(optimizable, rangeBounds);
            default:
                throw new OptimizableProcessingException("Unsupported type: " + dataType);
            }
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private SmodelBitChromosome toNormalizedRangeInt(Optimizable optimizable, RangeBounds rangeBounds) {
        int startValue = expressionCalculator.calculateInteger(rangeBounds.getStartValue());
        int endValue = expressionCalculator.calculateInteger(rangeBounds.getEndValue());
        int stepSize = expressionCalculator.calculateInteger(rangeBounds.getStepSize());

        int power = (endValue - startValue) / stepSize;
        int minLength = powerUtil.minBitSizeForPower(power);
        return SmodelBitChromosome.of(new SmodelBitset(minLength), optimizable);
    }

    public List<OptimizableValue<?>> toOptimizableValues(List<SmodelBitChromosome> chromosomes) {
        return chromosomes.stream()
            .map(c -> toOptimizable(c))
            .collect(Collectors.toList());
    }

    public OptimizableValue<?> toOptimizable(SmodelBitChromosome chromosome) {
        Optimizable optimizable = chromosome.getOptimizable();
        DataType dataType = optimizable.getDataType();
        switch (dataType) {
        case INT:
            return toOptimizableInt(optimizable, chromosome);
        default:
            throw new OptimizableProcessingException("Unsupported type: " + dataType);
        }
    }

    private OptimizableValue<Integer> toOptimizableInt(Optimizable optimizable, SmodelBitChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Integer> valueList = getValueListInt(optimizable);
        int value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private List<Integer> getValueListInt(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .stream()
                .map(e -> expressionCalculator.calculateInteger(e))
                .collect(Collectors.toList());
        }
        if (bounds instanceof RangeBounds rangeBounds) {
            int startValue = expressionCalculator.calculateInteger(rangeBounds.getStartValue());
            int endValue = expressionCalculator.calculateInteger(rangeBounds.getEndValue());
            int stepSize = expressionCalculator.calculateInteger(rangeBounds.getStepSize());
            return IntStream.iterate(startValue, n -> n + stepSize)
                .takeWhile(n -> n < endValue)
                .boxed()
                .collect(Collectors.toList());
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }
}
