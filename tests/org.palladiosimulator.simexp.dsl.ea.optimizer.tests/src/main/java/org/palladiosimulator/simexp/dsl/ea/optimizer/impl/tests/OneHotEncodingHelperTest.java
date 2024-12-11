package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OneHotEncodingCodecHelper;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.util.ISeq;

public class OneHotEncodingHelperTest {

    @Mock
    private BitChromosome bitChromo;

    @Mock
    private BitSet bitSet;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void encodeSetTest() {
        Double[] doubleArray = { 1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999 };
        ISeq<Double> seq = ISeq.of(doubleArray);
        int[] expectedSetBits = { 0, 1 };

        InvertibleCodec<Double, BitGene> codecOfSubSet = OneHotEncodingCodecHelper.createGrayCodecOfSubSet(seq, 0.5);

        BitSet bitSet = codecOfSubSet.encoding()
            .newInstance()
            .chromosome()
            .as(BitChromosome.class)
            .toBitSet();

        bitSet.size();

        Genotype<BitGene> firstEncoded = codecOfSubSet.encode(5.0);
        Genotype<BitGene> secondEncoded = codecOfSubSet.encode(13.573);

        String firstEncodedAsString = firstEncoded.chromosome()
            .as(BitChromosome.class)
            .toString();
        String secondEncodedAsString = secondEncoded.chromosome()
            .as(BitChromosome.class)
            .toString();

        assertEquals("00000011", firstEncodedAsString);
        assertEquals("00000100", secondEncodedAsString);

    }

    @Test(expected = RuntimeException.class)
    public void invalidEncodeSetTest() {
        Double[] doubleArray = { 1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999 };
        ISeq<Double> seq = ISeq.of(doubleArray);
        int[] expectedSetBits = { 0, 1 };

        InvertibleCodec<Double, BitGene> codecOfSubSet = OneHotEncodingCodecHelper.createGrayCodecOfSubSet(seq, 0.5);

        BitSet bitSet = codecOfSubSet.encoding()
            .newInstance()
            .chromosome()
            .as(BitChromosome.class)
            .toBitSet();

        codecOfSubSet.encode(1.5);
    }

    @Test
    public void decodeSetTest() {
        BitSet bitSet = new BitSet();
        bitSet.set(1);
        bitSet.set(2);
        Genotype<BitGene> gt = Genotype.of(bitChromo);
        when(bitChromo.as(BitChromosome.class)).thenReturn(bitChromo);
        when(bitChromo.toBitSet()).thenReturn(bitSet);
        Double[] doubleArray = { 1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999 };
        ISeq<Double> seq = ISeq.of(doubleArray);
        InvertibleCodec<Double, BitGene> codecOfSubSet = OneHotEncodingCodecHelper.createGrayCodecOfSubSet(seq, 0.5);

        double decodedValue = codecOfSubSet.decode(gt);

        assertEquals(doubleArray[6], decodedValue, 0.0001);
    }

    @Test(expected = RuntimeException.class)
    public void decodeInvalidSetTest() {
        BitSet bitSet = new BitSet();
        bitSet.set(1);
        bitSet.set(2);
        bitSet.set(4);
        Genotype<BitGene> gt = Genotype.of(bitChromo);
        when(bitChromo.as(BitChromosome.class)).thenReturn(bitChromo);
        when(bitChromo.toBitSet()).thenReturn(bitSet);
        Double[] doubleArray = { 1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999 };
        ISeq<Double> seq = ISeq.of(doubleArray);
        InvertibleCodec<Double, BitGene> codecOfSubSet = OneHotEncodingCodecHelper.createGrayCodecOfSubSet(seq, 0.5);

        double decodedValue = codecOfSubSet.decode(gt);

    }

}
