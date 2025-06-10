package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StartKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StopKind;

public class PowerUtil {
    private final IExpressionCalculator expressionCalculator;

    public PowerUtil(IExpressionCalculator expressionCalculator) {
        this.expressionCalculator = expressionCalculator;
    }

    public int minBitSizeForPower(int power) {
        if (power == 0) {
            return 0;
        }
        int minSize = (int) Math.ceil(Math.log(power) / Math.log(2));
        return minSize;
    }

    public int getPower(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return getPowerSet(setBounds);
        }

        if (bounds instanceof RangeBounds rangeBounds) {
            DataType dataType = optimizable.getDataType();
            switch (dataType) {
            case INT:
                return getPowerRangeInt(rangeBounds);
            case DOUBLE:
                return getPowerRangeDouble(rangeBounds);
            default:
                throw new RuntimeException("Unsupported type: " + dataType);
            }
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    public int getPowerSet(SetBounds setBounds) {
        return setBounds.getValues()
            .size();
    }

    public int getPowerRangeInt(RangeBounds rangeBounds) {
        List<Integer> values = getValueListInt(rangeBounds);
        return values.size();
    }

    public int getPowerRangeDouble(RangeBounds rangeBounds) {
        List<Double> values = getValueListDouble(rangeBounds);
        return values.size();
    }

    public List<Integer> getValueListInt(RangeBounds rangeBounds) {
        int startValue = expressionCalculator.calculateInteger(rangeBounds.getStartValue());
        int endValue = expressionCalculator.calculateInteger(rangeBounds.getEndValue());
        int stepSize = expressionCalculator.calculateInteger(rangeBounds.getStepSize());

        if (rangeBounds.getStart() == StartKind.OPEN) {
            startValue += stepSize;
        }

        IntStream stream = IntStream.iterate(startValue, n -> n + stepSize)
            .takeWhile(n -> n <= endValue);
        if (rangeBounds.getStop() == StopKind.OPEN) {
            stream = stream.filter(n -> n != endValue);
        }
        List<Integer> values = stream.boxed()
            .collect(Collectors.toList());
        return values;
    }

    public List<Double> getValueListDouble(RangeBounds rangeBounds) {
        double startValue = expressionCalculator.calculateDouble(rangeBounds.getStartValue());
        double endValue = expressionCalculator.calculateDouble(rangeBounds.getEndValue());
        double stepSize = expressionCalculator.calculateDouble(rangeBounds.getStepSize());
        if (rangeBounds.getStart() == StartKind.OPEN) {
            startValue += stepSize;
        }

        DoubleStream stream = DoubleStream.iterate(startValue, n -> n + stepSize)
            .takeWhile(n -> n <= endValue);
        if (rangeBounds.getStop() == StopKind.OPEN) {
            double epsilon = expressionCalculator.getEpsilon();
            stream = stream.filter(n -> Math.abs(n - endValue) >= epsilon);
        }
        List<Double> values = stream.boxed()
            .collect(Collectors.toList());
        return values;
    }
}
