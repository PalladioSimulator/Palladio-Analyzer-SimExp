package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.SetBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class PowerUtilTest {
    private static final double DOUBLE_EPSILON = 1e-15;

    private PowerUtil powerUtil;
    private SmodelCreator smodelCreator;
    private SetBoundsHelper setBoundsHelper;

    private IntLiteral startLiteralInt;
    private IntLiteral stopLiteralInt;
    private IntLiteral stepLiteralInt;
    private DoubleLiteral startLiteralDouble;
    private DoubleLiteral stopLiteralDouble;
    private DoubleLiteral stepLiteralDouble;

    @Mock
    private IExpressionCalculator calculator;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        setBoundsHelper = new SetBoundsHelper();

        when(calculator.getEpsilon()).thenReturn(DOUBLE_EPSILON);

        startLiteralInt = smodelCreator.createIntLiteral(1);
        stopLiteralInt = smodelCreator.createIntLiteral(3);
        stepLiteralInt = smodelCreator.createIntLiteral(1);
        when(calculator.calculateInteger(startLiteralInt)).thenReturn(startLiteralInt.getValue());
        when(calculator.calculateInteger(stopLiteralInt)).thenReturn(stopLiteralInt.getValue());
        when(calculator.calculateInteger(stepLiteralInt)).thenReturn(stepLiteralInt.getValue());

        startLiteralDouble = smodelCreator.createDoubleLiteral(1.0);
        stopLiteralDouble = smodelCreator.createDoubleLiteral(3.0);
        stepLiteralDouble = smodelCreator.createDoubleLiteral(1.0);
        when(calculator.calculateDouble(startLiteralDouble)).thenReturn(startLiteralDouble.getValue());
        when(calculator.calculateDouble(stopLiteralDouble)).thenReturn(stopLiteralDouble.getValue());
        when(calculator.calculateDouble(stepLiteralDouble)).thenReturn(stepLiteralDouble.getValue());

        powerUtil = new PowerUtil(calculator);
    }

    @Test
    public void testPower1() {
        int actualMinBitSize = powerUtil.minBitSizeForPower(1);

        assertEquals(0, actualMinBitSize);
    }

    @Test
    public void testPower2() {
        int actualMinBitSize = powerUtil.minBitSizeForPower(2);

        assertEquals(1, actualMinBitSize);
    }

    @Test
    public void testPower5() {
        int actualMinBitSize = powerUtil.minBitSizeForPower(5);

        assertEquals(3, actualMinBitSize);
    }

    @Test
    public void testGetPowerRangeClosedClosedIntStep1() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsClosedClosed(startLiteralInt, stopLiteralInt,
                stepLiteralInt);

        int actualPower = powerUtil.getPowerRangeInt(rangeBounds);

        assertEquals(3, actualPower);
    }

    @Test
    public void testGetPowerRangeClosedClosedIntStep3() {
        stopLiteralInt = smodelCreator.createIntLiteral(5);
        stepLiteralInt = smodelCreator.createIntLiteral(3);
        when(calculator.calculateInteger(stopLiteralInt)).thenReturn(stopLiteralInt.getValue());
        when(calculator.calculateInteger(stepLiteralInt)).thenReturn(stepLiteralInt.getValue());
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsClosedClosed(startLiteralInt, stopLiteralInt,
                stepLiteralInt);

        int actualPower = powerUtil.getPowerRangeInt(rangeBounds);

        assertEquals(2, actualPower);
    }

    @Test
    public void testGetPowerRangeClosedOpenInt() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsClosedOpen(startLiteralInt, stopLiteralInt,
                stepLiteralInt);

        int actualPower = powerUtil.getPowerRangeInt(rangeBounds);

        assertEquals(2, actualPower);
    }

    @Test
    public void testGetPowerRangeOpenOpenInt() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsOpenOpen(startLiteralInt, stopLiteralInt,
                stepLiteralInt);

        int actualPower = powerUtil.getPowerRangeInt(rangeBounds);

        assertEquals(1, actualPower);
    }

    @Test
    public void testGetPowerRangeOpenClosedInt() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsOpenClosed(startLiteralInt, stopLiteralInt,
                stepLiteralInt);

        int actualPower = powerUtil.getPowerRangeInt(rangeBounds);

        assertEquals(2, actualPower);
    }

    @Test
    public void testGetPowerRangeClosedClosedDoubleStep1() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsClosedClosed(startLiteralDouble, stopLiteralDouble,
                stepLiteralDouble);

        int actualPower = powerUtil.getPowerRangeDouble(rangeBounds);

        assertEquals(3, actualPower);
    }

    @Test
    public void testGetPowerRangeClosedClosedDoubleStep3() {
        stopLiteralDouble = smodelCreator.createDoubleLiteral(5.0);
        stepLiteralDouble = smodelCreator.createDoubleLiteral(3.0);
        when(calculator.calculateDouble(stopLiteralDouble)).thenReturn(stopLiteralDouble.getValue());
        when(calculator.calculateDouble(stepLiteralDouble)).thenReturn(stepLiteralDouble.getValue());
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsClosedClosed(startLiteralDouble, stopLiteralDouble,
                stepLiteralDouble);

        int actualPower = powerUtil.getPowerRangeDouble(rangeBounds);

        assertEquals(2, actualPower);
    }

    @Test
    public void testGetPowerRangeClosedOpenDouble() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsClosedOpen(startLiteralDouble, stopLiteralDouble,
                stepLiteralDouble);

        int actualPower = powerUtil.getPowerRangeDouble(rangeBounds);

        assertEquals(2, actualPower);
    }

    @Test
    public void testGetPowerRangeOpenOpenDouble() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsOpenOpen(startLiteralDouble, stopLiteralDouble,
                stepLiteralDouble);

        int actualPower = powerUtil.getPowerRangeDouble(rangeBounds);

        assertEquals(1, actualPower);
    }

    @Test
    public void testGetPowerRangeOpenClosedDouble() {
        RangeBounds rangeBounds = smodelCreator.createRangeBoundsOpenClosed(startLiteralDouble, stopLiteralDouble,
                stepLiteralDouble);

        int actualPower = powerUtil.getPowerRangeDouble(rangeBounds);

        assertEquals(2, actualPower);
    }

    @Test
    public void testGetValueListInteger() {
        IntLiteral lowerBound = smodelCreator.createIntLiteral(0);
        IntLiteral upperBound = smodelCreator.createIntLiteral(5);
        IntLiteral step = smodelCreator.createIntLiteral(1);
        when(calculator.calculateInteger(lowerBound)).thenReturn(lowerBound.getValue());
        when(calculator.calculateInteger(upperBound)).thenReturn(upperBound.getValue());
        when(calculator.calculateInteger(step)).thenReturn(step.getValue());
        RangeBounds rangeBound = smodelCreator.createRangeBoundsOpenClosed(lowerBound, upperBound, step);

        List<Integer> actualValueList = powerUtil.getValueListInt(rangeBound);

        assertThat(actualValueList).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void testGetValueListDouble() {
        DoubleLiteral lowerBoundDouble = smodelCreator.createDoubleLiteral(0.15);
        DoubleLiteral upperBoundDouble = smodelCreator.createDoubleLiteral(5.0);
        DoubleLiteral stepDouble = smodelCreator.createDoubleLiteral(0.5);
        when(calculator.calculateDouble(lowerBoundDouble)).thenReturn(lowerBoundDouble.getValue());
        when(calculator.calculateDouble(upperBoundDouble)).thenReturn(upperBoundDouble.getValue());
        when(calculator.calculateDouble(stepDouble)).thenReturn(stepDouble.getValue());
        RangeBounds rangeBound = smodelCreator.createRangeBoundsOpenClosed(lowerBoundDouble, upperBoundDouble,
                stepDouble);

        List<Double> actualValueList = powerUtil.getValueListDouble(rangeBound);

        assertThat(actualValueList).containsExactly(0.65, 1.15, 1.65, 2.15, 2.65, 3.15, 3.65, 4.15, 4.65);
    }

    @Test
    public void testCalculateComplexityBool() {
        SetBounds setBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.BOOL, setBound);
        Collection<Optimizable> optimizables = Arrays.asList(optimizable);

        int actualComplexity = powerUtil.calculateComplexity(optimizables);

        assertEquals(2, actualComplexity);
    }

    @Test
    public void testCalculateComplexityInt() {
        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, List.of(1, 2, 3), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        Collection<Optimizable> optimizables = Arrays.asList(optimizable);

        int actualComplexity = powerUtil.calculateComplexity(optimizables);

        assertEquals(3, actualComplexity);
    }

    @Test
    public void testCalculateComplexityDouble() {
        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator, List.of(1.0, 2.0), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        Collection<Optimizable> optimizables = Arrays.asList(optimizable);

        int actualComplexity = powerUtil.calculateComplexity(optimizables);

        assertEquals(2, actualComplexity);
    }

    @Test
    public void testCalculateComplexityString() {
        SetBounds setBound = setBoundsHelper.initializeStringSetBound(smodelCreator, List.of("123"), calculator);
        Optimizable optimizable = smodelCreator.createOptimizable("test", DataType.INT, setBound);
        Collection<Optimizable> optimizables = Arrays.asList(optimizable);

        int actualComplexity = powerUtil.calculateComplexity(optimizables);

        assertEquals(1, actualComplexity);
    }
}
