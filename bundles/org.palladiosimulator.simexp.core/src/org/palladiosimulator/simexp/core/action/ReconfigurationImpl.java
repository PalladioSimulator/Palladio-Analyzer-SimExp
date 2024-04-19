package org.palladiosimulator.simexp.core.action;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ActionImpl;

public abstract class ReconfigurationImpl<A> extends ActionImpl<A> implements Reconfiguration<A> {

    @Override
    public String toString() {
        return getStringRepresentation();
    }

}
