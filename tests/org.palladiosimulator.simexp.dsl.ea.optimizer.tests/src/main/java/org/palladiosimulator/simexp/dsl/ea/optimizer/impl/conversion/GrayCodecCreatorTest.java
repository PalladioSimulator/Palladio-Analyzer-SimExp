package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion.codecs.GrayCodecCreator;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.util.ISeq;

public class GrayCodecCreatorTest {

    @Mock
    private BitChromosome bitChromo;

    @Mock
    private BitSet bitSet;

    private GrayCodecCreator codecCreator;

    @Before
    public void setUp() {
        initMocks(this);
        codecCreator = new GrayCodecCreator();
    }

    @Test
    public void encodeSetTest() {
        Double[] doubleArray = { 1.0, 2.0, 5.0, 6.5, 8.73651, 9.0, 27.83727462, 13.573, 1.0, 99.999 };
        ISeq<Double> seq = ISeq.of(doubleArray);
        InvertibleCodec<ISeq<Double>, BitGene> codecOfSubSet = codecCreator.createCodecOfSubSet(seq, 0.5);
        BitSet bitSet = codecOfSubSet.encoding()
            .newInstance()
            .chromosome()
            .as(BitChromosome.class)
            .toBitSet();
        bitSet.size();

        Genotype<BitGene> firstEncoded = codecOfSubSet.encode(ISeq.of(5.0));
        Genotype<BitGene> secondEncoded = codecOfSubSet.encode(ISeq.of(13.573));

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

        InvertibleCodec<ISeq<Double>, BitGene> codecOfSubSet = codecCreator.createCodecOfSubSet(seq, 0.5);

        BitSet bitSet = codecOfSubSet.encoding()
            .newInstance()
            .chromosome()
            .as(BitChromosome.class)
            .toBitSet();
        codecOfSubSet.encode(ISeq.of(1.5));
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
        InvertibleCodec<ISeq<Double>, BitGene> codecOfSubSet = codecCreator.createCodecOfSubSet(seq, 0.5);

        ISeq<Double> decoded = codecOfSubSet.decode(gt);
        assertEquals(1, decoded.size());
        Double decodedValue = decoded.get(0);
        assertEquals(doubleArray[4], decodedValue, 0.0001);
    }

    @Test
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
        InvertibleCodec<ISeq<Double>, BitGene> codecOfSubSet = codecCreator.createCodecOfSubSet(seq, 0.5);

        ISeq<Double> decoded = codecOfSubSet.decode(gt);
        assertNull(decoded);
    }

}