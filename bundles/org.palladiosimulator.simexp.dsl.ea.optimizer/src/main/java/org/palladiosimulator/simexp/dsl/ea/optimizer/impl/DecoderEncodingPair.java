package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.function.Function;

public class DecoderEncodingPair extends Pair<Function<Object, ?>, Object> {

    public DecoderEncodingPair(Function<Object, ?> first, Object second) {
        super(first, second);
    }

}
