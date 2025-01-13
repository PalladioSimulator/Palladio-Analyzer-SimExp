package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

/**
 * Helps interpreting {@link SmodelBitset} A BitInterpreter can either 
 * decode a given SmodelBitSet or encode a given {@link int} value as 
 * a {@link SmodelBitset}. The actual encoding (classic binary, gray, 
 * one-hot, etc.) depends on the implementation of BitInterpreter.
 */
public interface BitInterpreter {

    /**
     * Decodes the given SmodelBitset and returns its value.
     * 
     * @param bitSet The SmodelBitset that should be decoded
     * @return The value of the SmodelBitset
     */
    public int toInt(SmodelBitset bitSet);

    /**
     * Decodes the given value as a SmodelBitset instance.
     * The decoding depends on the implementation of BitInterpreter.
     * 
     * @param value The value to be encoded
     * @return The encoded value
     */
    public BitSet toBitSet(int value);

}
