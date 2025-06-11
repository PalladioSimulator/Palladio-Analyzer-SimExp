package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

import io.jenetics.BitGene;
import io.jenetics.Genotype;

public class OptimizableNormalizer {
    private final PowerUtil powerUtil;
    private final IExpressionCalculator expressionCalculator;

    public OptimizableNormalizer(IExpressionCalculator expressionCalculator) {
        this.powerUtil = new PowerUtil(expressionCalculator);
        this.expressionCalculator = expressionCalculator;
    }

    public Genotype<BitGene> toGenotype(List<Optimizable> optimizables) {
        List<SmodelBitChromosome> normalizedOptimizables = toNormalized(optimizables);
        Genotype<BitGene> genotype = Genotype.of(normalizedOptimizables);
        return genotype;
    }

    List<SmodelBitChromosome> toNormalized(List<Optimizable> optimizables) {
        List<SmodelBitChromosome> chromosomes = optimizables.stream()
            .map(o -> toNormalized(o))
            .toList();
        return chromosomes;
    }

    SmodelBitChromosome toNormalized(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            int power = powerUtil.getPowerSet(setBounds);
            int minLength = powerUtil.minBitSizeForPower(power);
            return toNormalizedSet(optimizable, setBounds.getValues()
                .size(), minLength);

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

    public List<OptimizableValue<?>> toOptimizableValues(Genotype<BitGene> genotype) {
        List<SmodelBitChromosome> chromosomes = genotype.stream()
            .map(g -> g.as(SmodelBitChromosome.class))
            .toList();
        return toOptimizableValues(chromosomes);
    }

    List<OptimizableValue<?>> toOptimizableValues(List<SmodelBitChromosome> chromosomes) {
        return chromosomes.stream()
            .map(c -> toOptimizable(c))
            .collect(Collectors.toList());
    }

    OptimizableValue<?> toOptimizable(SmodelBitChromosome chromosome) {
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

    protected SmodelBitChromosome toNormalizedSet(Optimizable optimizable, int boundsSize, int minLength) {
        return SmodelBitChromosome.of(new SmodelBitset(minLength), optimizable, boundsSize, new BinaryBitInterpreter());
    }

    private SmodelBitChromosome toNormalizedRangeInt(Optimizable optimizable, RangeBounds rangeBounds) {
        int power = powerUtil.getPowerRangeInt(rangeBounds);
        int minLength = powerUtil.minBitSizeForPower(power);
        return toNormalizedSet(optimizable, power, minLength);
    }

    private SmodelBitChromosome toNormalizedRangeDouble(Optimizable optimizable, RangeBounds rangeBounds) {
        int power = powerUtil.getPowerRangeDouble(rangeBounds);
        int minLength = powerUtil.minBitSizeForPower(power);
        return toNormalizedSet(optimizable, power, minLength);
    }

    private OptimizableValue<Integer> toOptimizableInt(Optimizable optimizable, SmodelBitChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Integer> valueList = getValueListInt(optimizable);
        int value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<Double> toOptimizableDouble(Optimizable optimizable, SmodelBitChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Double> valueList = getValueListDouble(optimizable);
        double value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<Boolean> toOptimizableBool(Optimizable optimizable, SmodelBitChromosome chromosome) {
        int valueIndex = chromosome.intValue();
        List<Boolean> valueList = getValueListBoolean(optimizable);
        Boolean value = valueList.get(valueIndex);
        return new OptimizableValue<>(optimizable, value);
    }

    private OptimizableValue<String> toOptimizableString(Optimizable optimizable, SmodelBitChromosome chromosome) {
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
