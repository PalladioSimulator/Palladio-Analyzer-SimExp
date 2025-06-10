package org.palladiosimulator.simexp.dsl.ea.optimizer.utility;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SetBoundsHelper {

    public SetBounds initializeIntegerSetBound(SmodelCreator smodelCreator, List<Integer> elementsInSet,
            IExpressionCalculator calculator) {
        List<IntLiteral> intLiterals = new ArrayList<>();
        for (Integer element : elementsInSet) {
            IntLiteral elementLiteral = smodelCreator.createIntLiteral(element);
            intLiterals.add(elementLiteral);
            when(calculator.calculateInteger(elementLiteral)).thenReturn(element);
        }
        IntLiteral[] doubleLiteralsAsArray = intLiterals.toArray(new IntLiteral[intLiterals.size()]);
        return smodelCreator.createSetBounds(doubleLiteralsAsArray);
    }

    public SetBounds initializeDoubleSetBound(SmodelCreator smodelCreator, List<Double> elementsInSet,
            IExpressionCalculator calculator) {
        List<DoubleLiteral> doubleLiterals = new ArrayList<>();
        for (Double element : elementsInSet) {
            DoubleLiteral elementLiteral = smodelCreator.createDoubleLiteral(element);
            doubleLiterals.add(elementLiteral);
            when(calculator.calculateDouble(elementLiteral)).thenReturn(element);
        }
        DoubleLiteral[] doubleLiteralsAsArray = doubleLiterals.toArray(new DoubleLiteral[doubleLiterals.size()]);
        return smodelCreator.createSetBounds(doubleLiteralsAsArray);
    }

    public SetBounds initializeBooleanSetBound(SmodelCreator smodelCreator, List<Boolean> elementsInSet,
            IExpressionCalculator calculator) {
        List<BoolLiteral> boolLiterals = new ArrayList<>();
        for (Boolean element : elementsInSet) {
            BoolLiteral elementLiteral = smodelCreator.createBoolLiteral(element);
            boolLiterals.add(elementLiteral);
            when(calculator.calculateBoolean(elementLiteral)).thenReturn(element);
        }
        BoolLiteral[] boolLiteralsAsArray = boolLiterals.toArray(new BoolLiteral[boolLiterals.size()]);
        return smodelCreator.createSetBounds(boolLiteralsAsArray);
    }

    public SetBounds initializeStringSetBound(SmodelCreator smodelCreator, List<String> elementsInSet,
            IExpressionCalculator calculator) {
        List<StringLiteral> stringLiterals = new ArrayList<>();
        for (String element : elementsInSet) {
            StringLiteral elementLiteral = smodelCreator.createStringLiteral(element);
            stringLiterals.add(elementLiteral);
            when(calculator.calculateString(elementLiteral)).thenReturn(element);
        }
        StringLiteral[] stringLiteralsAsArray = stringLiterals.toArray(new StringLiteral[stringLiterals.size()]);
        return smodelCreator.createSetBounds(stringLiteralsAsArray);
    }

}
