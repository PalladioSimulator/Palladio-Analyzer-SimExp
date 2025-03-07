package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableProcessingException;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

import io.jenetics.util.IntRange;

public class OptimizableIntegerChromoNormalizer {

    private final PowerUtil powerUtil;

    private IExpressionCalculator expressionCalculator;

    private List<SmodelIntegerChromosome> singleValueOptimizables;

    public OptimizableIntegerChromoNormalizer(IExpressionCalculator expressionCalculator) {
        this.powerUtil = new PowerUtil(expressionCalculator);
        this.expressionCalculator = expressionCalculator;
        singleValueOptimizables = new ArrayList<>();

    }

    public List<SmodelIntegerChromosome> toNormalizedAndRemoveSingleValuedChromosomes(List<Optimizable> optimizables) {
        List<SmodelIntegerChromosome> chromosomes = optimizables.stream()
            .map(o -> toNormalized(o))
            .peek(c -> saveIfLengthIsZero(c))
            .filter(c -> c.length() > 0)
            .collect(Collectors.toList());
        return chromosomes;
    }

    public SmodelIntegerChromosome toNormalized(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            int power = powerUtil.getPowerSet(setBounds);

            return toNormalizedSet(optimizable, power);
        }

        if (bounds instanceof RangeBounds rangeBounds) {
            DataType dataType = optimizable.getDataType();
            switch (dataType) {
            case INT:
                return toNormalizedRangeInt(optimizable, rangeBounds);
            case DOUBLE:
                return toNormalizedRangeDouble(optimizable, rangeBounds);
            default:
                throw new OptimizableProcessingException("Unsupported type: " + dataType);
            }
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    public List<OptimizableValue<?>> toOptimizableValues(List<SmodelIntegerChromosome> chromosomes) {
        List<OptimizableValue<?>> optimizableValuesFromChromosomes = chromosomes.stream()
            .map(c -> toOptimizable(c))
            .collect(Collectors.toList());

        addSingleValueOptimizablesIfNotContained(optimizableValuesFromChromosomes);
        return optimizableValuesFromChromosomes;
    }

    public synchronized void addSingleValueOptimizablesIfNotContained(
            List<OptimizableValue<?>> optimizableValuesFromChromosomes) {
        singleValueOptimizables.forEach(o -> {
            OptimizableValue<?> optimizable = toOptimizable(o);
            optimizableValuesFromChromosomes.add(optimizable);
        });
    }

    public OptimizableValue<?> toOptimizable(SmodelIntegerChromosome chromosome) {
        Optimizable optimizable = chromosome.getOptimizable();
        DataType dataType = optimizable.getDataType();
        switch (dataType) {
        case INT:
            return toOptimizableInt(optimizable, chromosome);
        case DOUBLE:
            return toOptimizableDouble(optimizable, chromosome);
        case BOOL:
            return toOptimizableBool(optimizable, chromosome);
        case STRING:
            return toOptimizableString(optimizable, chromosome);
        default:
            throw new OptimizableProcessingException("Unsupported type: " + dataType);
        }
    }

    protected SmodelIntegerChromosome toNormalizedSet(Optimizable optimizable, int minLength) {
        IntRange intRange = IntRange.of(0, minLength - 1);
        return SmodelIntegerChromosome.of(intRange, optimizable);
    }

    private SmodelIntegerChromosome toNormalizedRangeInt(Optimizable optimizable, RangeBounds rangeBounds) {
        int power = powerUtil.getPowerRangeInt(rangeBounds);
        return toNormalizedSet(optimizable, power);
    }

    private SmodelIntegerChromosome toNormalizedRangeDouble(Optimizable optimizable, RangeBounds rangeBounds) {
        int power = powerUtil.getPowerRangeDouble(rangeBounds);
        return toNormalizedSet(optimizable, power);
    }

    private synchronized void saveIfLengthIsZero(SmodelIntegerChromosome chromosome) {
        if (chromosome.length() == 0) {
            singleValueOptimizables.add(chromosome);
        }
    }

    private OptimizableValue<Integer> toOptimizableInt(Optimizable optimizable, SmodelIntegerChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Integer> valueList = getValueListInt(optimizable);
        int value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<Double> toOptimizableDouble(Optimizable optimizable, SmodelIntegerChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Double> valueList = getValueListDouble(optimizable);
        double value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<Boolean> toOptimizableBool(Optimizable optimizable, SmodelIntegerChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Boolean> valueList = getValueListBoolean(optimizable);
        Boolean value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<String> toOptimizableString(Optimizable optimizable, SmodelIntegerChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<String> valueList = getValueListString(optimizable);
        String value = valueList.get(valueIndex);
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

    private List<Double> getValueListDouble(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .stream()
                .map(e -> expressionCalculator.calculateDouble(e))
                .collect(Collectors.toList());
        }
        if (bounds instanceof RangeBounds rangeBounds) {
            double startValue = expressionCalculator.calculateDouble(rangeBounds.getStartValue());
            double endValue = expressionCalculator.calculateDouble(rangeBounds.getEndValue());
            double stepSize = expressionCalculator.calculateDouble(rangeBounds.getStepSize());
            return DoubleStream.iterate(startValue, n -> n + stepSize)
                .takeWhile(n -> n < endValue)
                .boxed()
                .collect(Collectors.toList());
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private List<Boolean> getValueListBoolean(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .stream()
                .map(e -> expressionCalculator.calculateBoolean(e))
                .collect(Collectors.toList());
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private List<String> getValueListString(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .stream()
                .map(e -> expressionCalculator.calculateString(e))
                .collect(Collectors.toList());
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }
}
