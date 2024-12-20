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

    public SmodelBitset toNormalized(Optimizable optimizable, IExpressionCalculator expressionCalculator) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            int minLength = powerUtil.minBitSizeForPower(setBounds.getValues()
                .size());
            return new SmodelBitset(optimizable, minLength);
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
            return new SmodelBitset(optimizable, minLength);
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    // Convert Binary to Gray Code; used formula: Gray = Binary XOR (Binary >> 1)
    private int binaryToGray(int binary) {
        return binary ^ (binary >> 1);
    }

    public OptimizableValue<?> toOptimizable(SmodelBitset bitSet) {
        return null;
    }

    // Convert Gray Code to Binary
    public static int grayToBinary(int gray) {
        int binary = gray;
        while ((gray >>= 1) > 0) {
            binary ^= gray;
        }
        return binary;
    }
}
