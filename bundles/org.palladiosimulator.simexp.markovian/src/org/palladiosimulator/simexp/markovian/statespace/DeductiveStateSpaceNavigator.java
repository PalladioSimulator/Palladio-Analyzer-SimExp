package org.palladiosimulator.simexp.markovian.statespace;

import org.palladiosimulator.simexp.markovian.access.MarkovModelAccessor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;

public abstract class DeductiveStateSpaceNavigator<S, A, R> extends StateSpaceNavigator<S, A> {

    protected final MarkovModelAccessor<S, A, R> markovModelAccessor;

    public DeductiveStateSpaceNavigator(MarkovModel<S, A, R> markovModel) {
        this.markovModelAccessor = MarkovModelAccessor.of(markovModel);
    }

}
