package org.palladiosimulator.simexp.markovian.statespace;

import org.palladiosimulator.simexp.markovian.access.MarkovModelAccessor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;

public abstract class DeductiveStateSpaceNavigator<A, R> extends StateSpaceNavigator<A> {

    protected final MarkovModelAccessor<A, R> markovModelAccessor;

    public DeductiveStateSpaceNavigator(MarkovModel<A, R> markovModel) {
        this.markovModelAccessor = MarkovModelAccessor.of(markovModel);
    }

}
