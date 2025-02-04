package org.palladiosimulator.simexp.dsl.ea.optimizer.utility;

import static org.mockito.Mockito.when;

import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class RangeBoundsHelper {

    public RangeBounds initializeDoubleRangeBound(SmodelCreator smodelCreator, IExpressionCalculator calculator,
            double lowerBound, double upperBound, double stepSize) {
        DoubleLiteral lowerBoundLiteral = smodelCreator.createDoubleLiteral(lowerBound);
        DoubleLiteral upperBoundLiteral = smodelCreator.createDoubleLiteral(upperBound);
        DoubleLiteral stepSizeLiteral = smodelCreator.createDoubleLiteral(stepSize);

        RangeBounds rangeBound = smodelCreator.createRangeBounds(lowerBoundLiteral, upperBoundLiteral, stepSizeLiteral);

        when(calculator.calculateDouble(lowerBoundLiteral)).thenReturn(lowerBound);
        when(calculator.calculateDouble(upperBoundLiteral)).thenReturn(upperBound);
        when(calculator.calculateDouble(stepSizeLiteral)).thenReturn(stepSize);
        return rangeBound;
    }

    public RangeBounds initializeIntegerRangeBound(SmodelCreator smodelCreator, IExpressionCalculator calculator,
            int lowerBound, int upperBound, int stepSize) {
        IntLiteral lowerBoundLiteral = smodelCreator.createIntLiteral(lowerBound);
        IntLiteral upperBoundLiteral = smodelCreator.createIntLiteral(upperBound);
        IntLiteral stepSizeLiteral = smodelCreator.createIntLiteral(stepSize);

        RangeBounds rangeBound = smodelCreator.createRangeBounds(lowerBoundLiteral, upperBoundLiteral, stepSizeLiteral);

        when(calculator.calculateInteger(lowerBoundLiteral)).thenReturn(lowerBound);
        when(calculator.calculateInteger(upperBoundLiteral)).thenReturn(upperBound);
        when(calculator.calculateInteger(stepSizeLiteral)).thenReturn(stepSize);
        return rangeBound;
    }

}