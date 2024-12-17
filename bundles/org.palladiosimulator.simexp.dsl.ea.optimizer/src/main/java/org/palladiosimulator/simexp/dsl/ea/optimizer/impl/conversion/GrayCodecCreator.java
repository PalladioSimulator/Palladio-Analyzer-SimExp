package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

import static java.util.Objects.requireNonNull;

import java.util.BitSet;
import java.util.Objects;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.util.GrayConverterHelper;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

public class GrayCodecCreator implements CodecCreator {

    //TODO delete
//    public <T> InvertibleCodec<T, BitGene> createGrayCodecOfSubSet(final ISeq<? extends T> basicSet, double p) {
//        requireNonNull(basicSet);
//        Requires.positive(basicSet.length());
//
//        int lengthOfGrayCode = (int) Math.ceil(Math.log(basicSet.length()) / Math.log(2));
//
//        return InvertibleCodec.of(Genotype.of(BitChromosome.of(lengthOfGrayCode, p)), gt -> {
//            int idx = GrayConverterHelper.grayToIdx(gt.chromosome()
//                .as(BitChromosome.class)
//                .toBitSet());
//            if (idx < basicSet.size()) {
//                return basicSet.get(idx);
//            } else {
//                return null;
//            }
//        }, values -> {
//            BitSet bitSet = null;
//
//            for (int i = 0; (i < basicSet.size()) && (bitSet == null); i++) {
//                if (Objects.equals(values, basicSet.get(i))) {
//                    bitSet = GrayConverterHelper.idxToGray(i, lengthOfGrayCode);
//
//                }
//            }
//            if (bitSet != null) {
//                return Genotype.of(new BitChromosome(bitSet.toByteArray(), 0, bitSet.length()));
//            } else {
//                throw new RuntimeException("Tried to encode a number which is not in the underlying set");
//            }
//        });
//    }

    @Override
    public <T> InvertibleCodec<ISeq<T>, BitGene> createCodecOfSubSet(ISeq<? extends T> basicSet, double p) {

        requireNonNull(basicSet);
        Requires.positive(basicSet.length());

        int lengthOfGrayCode = (int) Math.ceil(Math.log(basicSet.length()) / Math.log(2));

        return InvertibleCodec.of(Genotype.of(BitChromosome.of(lengthOfGrayCode, p)), gt -> {
            int idx = GrayConverterHelper.grayToIdx(gt.chromosome()
                .as(BitChromosome.class)
                .toBitSet());
            if (idx < basicSet.size()) {
                return ISeq.of(basicSet.get(idx));
            } else {
                return null;
            }
        }, values -> {
            if (values.size() > 1) {
                throw new RuntimeException("Only one element can be encoded in gray code");
            }
            T element = values.get(0);
            BitSet bitSet = null;

            for (int i = 0; (i < basicSet.size()) && (bitSet == null); i++) {
                if (Objects.equals(element, basicSet.get(i))) {
                    bitSet = GrayConverterHelper.idxToGray(i, lengthOfGrayCode);

                }
            }
            if (bitSet != null) {
                return Genotype.of(new BitChromosome(bitSet.toByteArray(), 0, bitSet.length()));
            } else {
                throw new RuntimeException("Tried to encode a number which is not in the underlying set");
            }
        });

    }

}
