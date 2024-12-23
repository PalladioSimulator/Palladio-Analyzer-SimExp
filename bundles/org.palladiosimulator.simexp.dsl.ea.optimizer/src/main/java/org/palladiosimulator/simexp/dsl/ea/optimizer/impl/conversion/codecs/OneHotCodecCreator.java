package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion.codecs;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.internal.util.Bits;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

public class OneHotCodecCreator implements CodecCreator {

    @Override
    public <T> InvertibleCodec<ISeq<T>, BitGene> createCodecOfSubSet(final ISeq<? extends T> basicSet, double p) {
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
                    if (Objects.equals(basicSet.get(i), v)) {
                        Bits.set(bits, i);
                    } else if (i == basicSet.size()) {
                        throw new RuntimeException("Given value to decode is not in the based set");
                    }
                }

                return Genotype.of(new BitChromosome(bits, 0, basicSet.size()));
            });
    }

}
