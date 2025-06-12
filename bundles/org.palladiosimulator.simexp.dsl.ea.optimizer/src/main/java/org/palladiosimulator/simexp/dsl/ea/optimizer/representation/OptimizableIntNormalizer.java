package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.ITranscoder;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

import io.jenetics.Genotype;
import io.jenetics.IntegerGene;

public class OptimizableIntNormalizer implements ITranscoder<IntegerGene> {
    private final PowerUtil powerUtil;
    private final IExpressionCalculator expressionCalculator;

    public OptimizableIntNormalizer(IExpressionCalculator expressionCalculator) {
        this.powerUtil = new PowerUtil(expressionCalculator);
        this.expressionCalculator = expressionCalculator;
    }

    @Override
    public Genotype<IntegerGene> toGenotype(List<Optimizable> optimizables) {
        List<SmodelIntChromosome> normalizedOptimizables = toNormalized(optimizables);
        Genotype<IntegerGene> genotype = Genotype.of(normalizedOptimizables);
        return genotype;
    }

    List<SmodelIntChromosome> toNormalized(List<Optimizable> optimizables) {
        List<SmodelIntChromosome> chromosomes = optimizables.stream()
            .map(o -> toNormalized(o))
            .toList();
        return chromosomes;
    }

    SmodelIntChromosome toNormalized(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return toNormalizedSet(optimizable, setBounds.getValues()
                .size());
        }

        if (bounds instanceof RangeBounds rangeBounds) {
            DataType dataType = optimizable.getDataType();
            switch (dataType) {
            case INT:
                return toNormalizedRangeInt(optimizable, rangeBounds);
            case DOUBLE:
                return toNormalizedRangeDouble(optimizable, rangeBounds);
            default:
                throw new RuntimeException("Unsupported type: " + dataType);
            }
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    @Override
    public List<OptimizableValue<?>> toOptimizableValues(Genotype<IntegerGene> genotype) {
        List<SmodelIntChromosome> chromosomes = genotype.stream()
            .map(g -> g.as(SmodelIntChromosome.class))
            .toList();
        return toOptimizableValues(chromosomes);
    }

    List<OptimizableValue<?>> toOptimizableValues(List<SmodelIntChromosome> chromosomes) {
        return chromosomes.stream()
            .map(c -> toOptimizable(c))
            .collect(Collectors.toList());
    }

    OptimizableValue<?> toOptimizable(SmodelIntChromosome chromosome) {
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
            throw new RuntimeException("Unsupported type: " + dataType);
        }
    }

    protected SmodelIntChromosome toNormalizedSet(Optimizable optimizable, int boundsSize) {
        return SmodelIntChromosome.of(optimizable, 0, boundsSize - 1);
    }

    private SmodelIntChromosome toNormalizedRangeInt(Optimizable optimizable, RangeBounds rangeBounds) {
        int power = powerUtil.getPowerRangeInt(rangeBounds);
        return toNormalizedSet(optimizable, power);
    }

    private SmodelIntChromosome toNormalizedRangeDouble(Optimizable optimizable, RangeBounds rangeBounds) {
        int power = powerUtil.getPowerRangeDouble(rangeBounds);
        return toNormalizedSet(optimizable, power);
    }

    private OptimizableValue<Integer> toOptimizableInt(Optimizable optimizable, SmodelIntChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Integer> valueList = getValueListInt(optimizable);
        int value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<Double> toOptimizableDouble(Optimizable optimizable, SmodelIntChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Double> valueList = getValueListDouble(optimizable);
        double value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<Boolean> toOptimizableBool(Optimizable optimizable, SmodelIntChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Boolean> valueList = getValueListBoolean(optimizable);
        Boolean value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<String> toOptimizableString(Optimizable optimizable, SmodelIntChromosome chromosome) {
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
                .toList();
        }
        if (bounds instanceof RangeBounds rangeBounds) {
            return powerUtil.getValueListInt(rangeBounds);
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private List<Double> getValueListDouble(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .stream()
                .map(e -> expressionCalculator.calculateDouble(e))
                .toList();
        }
        if (bounds instanceof RangeBounds rangeBounds) {
            return powerUtil.getValueListDouble(rangeBounds);
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private List<Boolean> getValueListBoolean(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .stream()
                .map(e -> expressionCalculator.calculateBoolean(e))
                .toList();
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private List<String> getValueListString(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .stream()
                .map(e -> expressionCalculator.calculateString(e))
                .toList();
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }

}
