package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static java.util.Objects.requireNonNull;

import java.util.BitSet;
import java.util.Objects;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.internal.util.Bits;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

public abstract class OneHotEncodingCodecHelper {

    public static <T> InvertibleCodec<ISeq<T>, BitGene> createCodecOfSubSet(final ISeq<? extends T> basicSet,
            double p) {
        requireNonNull(basicSet);
        Requires.positive(basicSet.length());

        return InvertibleCodec.of(Genotype.of(BitChromosome.of(basicSet.length(), p)), gt -> gt.chromosome()
            .as(BitChromosome.class)
            .ones()
            .<T> mapToObj(basicSet)
            .collect(ISeq.toISeq()), values -> {
                final byte[] bits = Bits.newArray(basicSet.size());

                int i = 0;
                for (T v : values) {
                    while (i < basicSet.size() && !Objects.equals(basicSet.get(i), v)) {
                        ++i;
                    }
                    Bits.set(bits, i);
                }

                return Genotype.of(new BitChromosome(bits, 0, basicSet.size()));
            });
    }

    public synchronized static <T> InvertibleCodec<T, BitGene> createGrayCodecOfSubSet(final ISeq<? extends T> basicSet,
            double p) {
        requireNonNull(basicSet);
        Requires.positive(basicSet.length());

        int lengthOfGrayCode = (int) Math.ceil(Math.log(basicSet.length()) / Math.log(2));

        return InvertibleCodec.of(Genotype.of(BitChromosome.of(lengthOfGrayCode, p)), gt -> {
            int idx = grayToIdx(gt.chromosome()
                .as(BitChromosome.class)
                .toBitSet());
            if (idx < basicSet.size()) {
                return basicSet.get(idx);
            } else {
                return null;
            }
        }, values -> {
            BitSet bitSet = null;

            for (int i = 0; (i < basicSet.size()) && (bitSet == null); i++) {
                if (Objects.equals(values, basicSet.get(i))) {
                    bitSet = idxToGray(i, lengthOfGrayCode);

                }
            }
            if (bitSet != null) {
                return Genotype.of(new BitChromosome(bitSet.toByteArray(), 0, bitSet.length()));
            } else {
                throw new RuntimeException("Tried to encode a number which is not in the underlying set");
            }
        });
    }

    private static int grayToIdx(BitSet bitSet) {
        int idx = 0;
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                idx |= (1 << i);
            }
        }
        return idx;
    }

    private static BitSet idxToGray(int idx, int lengthBitSet) {
        int gray = idx ^ (idx >> 1);
        BitSet bitSet = new BitSet(lengthBitSet);
        int i = 0;

        while (gray != 0) {
            if ((gray & 1) == 1) {
                bitSet.set(i);
            }
            gray >>= 1;
            i++;
        }

        return bitSet;
    }

}
