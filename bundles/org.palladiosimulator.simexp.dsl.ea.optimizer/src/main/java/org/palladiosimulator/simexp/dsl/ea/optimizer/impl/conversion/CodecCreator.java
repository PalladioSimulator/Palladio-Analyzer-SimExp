package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

import io.jenetics.BitGene;
import io.jenetics.engine.InvertibleCodec;
import io.jenetics.util.ISeq;

public interface CodecCreator {

    <T> InvertibleCodec<ISeq<T>, BitGene> createCodecOfSubSet(ISeq<? extends T> basicSet, double p);

}