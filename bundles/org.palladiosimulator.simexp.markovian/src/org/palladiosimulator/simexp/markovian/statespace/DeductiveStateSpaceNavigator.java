package org.palladiosimulator.simexp.markovian.statespace;

import org.palladiosimulator.simexp.markovian.access.MarkovModelAccessor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;

public abstract class DeductiveStateSpaceNavigator<T> extends StateSpaceNavigator {

    protected final MarkovModelAccessor<T> markovModelAccessor;

    public DeductiveStateSpaceNavigator(MarkovModel<T> markovModel) {
        this.markovModelAccessor = MarkovModelAccessor.of(markovModel);
    }

}
