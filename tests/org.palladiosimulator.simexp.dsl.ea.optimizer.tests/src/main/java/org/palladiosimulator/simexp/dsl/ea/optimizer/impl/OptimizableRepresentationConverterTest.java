package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion.AbstractConverter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion.GrayRepresentationConverter;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.RangeBoundsHelper;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.SetBoundsHelper;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.InvertibleCodec;

public class OptimizableRepresentationConverterTest {

    private AbstractConverter converter;

    @Mock
    public Optimizable optimizable;

    @Mock
    private IExpressionCalculator calculator;

    @Mock
    private Optimizable boolOptimizable;

    @Mock
    private Optimizable intOptimizable;

    private SmodelCreator smodelCreator;

    private List<Integer> smallIntList = List.of(1, 3, 7, 3, 8, 2, 9);

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        converter = new GrayRepresentationConverter();

    }

    @Test
    public void parseOptimizablesTest() {
        IOptimizableProvider provider = mock(IOptimizableProvider.class);
        SetBounds boolBound = SetBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                calculator);
        when(boolOptimizable.getValues()).thenReturn(boolBound);
        when(boolOptimizable.getDataType()).thenReturn(DataType.BOOL);
        SetBounds intBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator, smallIntList, calculator);
        when(intOptimizable.getValues()).thenReturn(intBound);
        when(intOptimizable.getDataType()).thenReturn(DataType.INT);
        Collection<Optimizable> optimizables = new ArrayList<>();
        optimizables.add(boolOptimizable);
        optimizables.add(intOptimizable);
        when(provider.getOptimizables()).thenReturn(optimizables);
        when(provider.getExpressionCalculator()).thenReturn(calculator);

        List<CodecOptimizablePair> parsedOptimizables = converter.parseOptimizables(provider);

        CodecOptimizablePair firstOptimizablePair = parsedOptimizables.get(0);
        assertEquals(firstOptimizablePair.second(), boolOptimizable);
        checkBooleanGenotype(firstOptimizablePair.first());

        CodecOptimizablePair secondOptimizablePair = parsedOptimizables.get(1);
        assertEquals(secondOptimizablePair.second(), intOptimizable);
        checkSmallIntListGenotype(secondOptimizablePair.first());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToGenotypeBoolean() {
        SetBounds setBound = SetBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
        when(optimizable.getDataType()).thenReturn(DataType.BOOL);
        when(optimizable.getValues()).thenReturn(setBound);

        Codec<?, ?> genotype = converter.toGenotype(optimizable, calculator);

        checkBooleanGenotype(genotype);
    }

    private void checkBooleanGenotype(Codec<?, ?> genotype) {
        assertTrue(genotype instanceof InvertibleCodec);
        InvertibleCodec<Boolean, BitGene> castedGenotype = (InvertibleCodec<Boolean, BitGene>) genotype;
        // Encoding
        assertNotNull(castedGenotype.encoding());
        // Encoder
        Genotype<BitGene> encodedValue = castedGenotype.encoder()
            .apply(true);
        assertTrue(encodedValue.gene()
            .allele());
        // Decoder
        Genotype<BitGene> genoToDecode = Genotype.of(BitChromosome.of(1, 1.0));
        assertTrue(castedGenotype.decode(genoToDecode));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToGenotypeIntegerSet() {
        SetBounds setBound = SetBoundsHelper.initializeIntegerSetBound(smodelCreator, smallIntList, calculator);
        when(optimizable.getDataType()).thenReturn(DataType.INT);
        when(optimizable.getValues()).thenReturn(setBound);

        Codec<?, ?> genotype = converter.toGenotype(optimizable, calculator);

        checkSmallIntListGenotype(genotype);
    }

    private void checkSmallIntListGenotype(Codec<?, ?> genotype) {
        assertTrue(genotype instanceof InvertibleCodec);
        InvertibleCodec<Integer, BitGene> castedGenotype = (InvertibleCodec<Integer, BitGene>) genotype;
        // Encoding
        assertNotNull(castedGenotype.encoding());
        // Encoder
        Genotype<BitGene> encodedValue = castedGenotype.encoder()
            .apply(7);
        assertEquals("00000011", encodedValue.chromosome()
            .toString());
        Genotype<BitGene> ambiguousEncodedValue = castedGenotype.encoder()
            .apply(3);
        assertEquals("00000001", ambiguousEncodedValue.chromosome()
            .toString());
        // Decoder
        Genotype<BitGene> genoToDecode = Genotype.of(BitChromosome.of(new BitSet(0), castedGenotype.encoding()
            .newInstance()
            .length()));
        assertEquals((Integer) 1, castedGenotype.decode(genoToDecode));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToGenotypeIntegerRange() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0, 20, 1);
        when(optimizable.getDataType()).thenReturn(DataType.INT);
        when(optimizable.getValues()).thenReturn(rangeBound);

        Codec<?, ?> genotype = converter.toGenotype(optimizable, calculator);

        assertTrue(genotype instanceof InvertibleCodec);
        InvertibleCodec<Integer, BitGene> castedGenotype = (InvertibleCodec<Integer, BitGene>) genotype;
        // Encoding
        assertNotNull(castedGenotype.encoding());
        // Encoder
        Genotype<BitGene> encodedValue = castedGenotype.encoder()
            .apply(7);
        assertEquals("00000100", encodedValue.chromosome()
            .toString());
        Genotype<BitGene> secondEncodedValue = castedGenotype.encoder()
            .apply(18);
        assertEquals("00011011", secondEncodedValue.chromosome()
            .toString());
        // Decoder
        Genotype<BitGene> genoToDecode = Genotype.of(BitChromosome.of(new BitSet(0), castedGenotype.encoding()
            .newInstance()
            .length()));
        assertEquals((Integer) 0, castedGenotype.decode(genoToDecode));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToGenotypeDoubleSet() {
        SetBounds setBound = SetBoundsHelper.initializeDoubleSetBound(smodelCreator,
                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 2.0, 9.0), calculator);
        when(optimizable.getDataType()).thenReturn(DataType.DOUBLE);
        when(optimizable.getValues()).thenReturn(setBound);

        Codec<?, ?> genotype = converter.toGenotype(optimizable, calculator);

        assertTrue(genotype instanceof InvertibleCodec);
        InvertibleCodec<Double, BitGene> castedGenotype = (InvertibleCodec<Double, BitGene>) genotype;
        // Encoding
        assertNotNull(castedGenotype.encoding());
        // Encoder
        Genotype<BitGene> encodedValue = castedGenotype.encoder()
            .apply(5.0);
        assertEquals("00000011", encodedValue.chromosome()
            .toString());
        Genotype<BitGene> ambiguousEncodedValue = castedGenotype.encoder()
            .apply(2.0);
        assertEquals("00000001", ambiguousEncodedValue.chromosome()
            .toString());
        // Decoder
        Genotype<BitGene> genoToDecode = Genotype.of(BitChromosome.of(new BitSet(0), castedGenotype.encoding()
            .newInstance()
            .length()));
        assertEquals((Double) 1.0, castedGenotype.decode(genoToDecode));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToGenotypeDoubleRange() {
        RangeBounds rangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.0, 20.0,
                1.0);
        when(optimizable.getDataType()).thenReturn(DataType.DOUBLE);
        when(optimizable.getValues()).thenReturn(rangeBound);

        Codec<?, ?> genotype = converter.toGenotype(optimizable, calculator);

        assertTrue(genotype instanceof InvertibleCodec);
        InvertibleCodec<Double, BitGene> castedGenotype = (InvertibleCodec<Double, BitGene>) genotype;
        // Encoding
        assertNotNull(castedGenotype.encoding());
        // Encoder
        Genotype<BitGene> encodedValue = castedGenotype.encoder()
            .apply(5.0);
        assertEquals("00000111", encodedValue.chromosome()
            .toString());
        Genotype<BitGene> ambiguousEncodedValue = castedGenotype.encoder()
            .apply(18.0);
        assertEquals("00011011", ambiguousEncodedValue.chromosome()
            .toString());
        // Decoder
        Genotype<BitGene> genoToDecode = Genotype.of(BitChromosome.of(new BitSet(0), castedGenotype.encoding()
            .newInstance()
            .length()));
        assertEquals((Double) 0.0, castedGenotype.decode(genoToDecode));
    }

    // TODO nbruening Test toPhenoValue methods
}
