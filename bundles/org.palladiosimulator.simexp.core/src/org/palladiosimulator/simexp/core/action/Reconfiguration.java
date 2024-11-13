package org.palladiosimulator.simexp.core.action;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public interface Reconfiguration<A> extends Action<A> {
    String getReconfigurationName();
}
