package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import io.jenetics.engine.Codec;

public class CodecOptimizablePair extends Pair<Codec<?, ?>, Optimizable> {

    public CodecOptimizablePair(Codec<?, ?> first, Optimizable second) {
        super(first, second);
    }

}
