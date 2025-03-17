package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableProcessingException;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

public class PowerUtil {

    private static final int PRECISION_DECIMAL = 7;

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
                throw new OptimizableProcessingException("Unsupported type: " + dataType);
            }
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    public int getPowerSet(SetBounds setBounds) {
        return setBounds.getValues()
            .size();
    }

    public int getPowerRangeInt(RangeBounds rangeBounds) {
        double startValue = expressionCalculator.calculateInteger(rangeBounds.getStartValue());
        double endValue = expressionCalculator.calculateInteger(rangeBounds.getEndValue());
        double stepSize = expressionCalculator.calculateInteger(rangeBounds.getStepSize());
        int power = (int) Math.round(((endValue * 1000000) - (startValue * 1000000)) / (stepSize * 1000000));
        return power;
    }

    public int getPowerRangeDouble(RangeBounds rangeBounds) {
        double startValue = expressionCalculator.calculateDouble(rangeBounds.getStartValue());
        double endValue = expressionCalculator.calculateDouble(rangeBounds.getEndValue());
        double stepSize = expressionCalculator.calculateDouble(rangeBounds.getStepSize());
        return (int) Math.round(((endValue * 1000000) - (startValue * 1000000)) / (stepSize * 1000000));
    }

}
