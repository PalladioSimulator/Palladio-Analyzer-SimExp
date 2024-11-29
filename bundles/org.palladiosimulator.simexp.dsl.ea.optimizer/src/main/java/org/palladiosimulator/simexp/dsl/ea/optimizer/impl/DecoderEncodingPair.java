package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.function.Function;

public class DecoderEncodingPair extends Pair<Function<?, ?>, Object> {

    public DecoderEncodingPair(Function<?, ?> first, Object second) {
        super(first, second);
    }

}
