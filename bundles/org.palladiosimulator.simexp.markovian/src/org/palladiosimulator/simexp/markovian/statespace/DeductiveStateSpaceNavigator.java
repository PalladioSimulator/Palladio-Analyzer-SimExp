package org.palladiosimulator.simexp.markovian.statespace;

import org.palladiosimulator.simexp.markovian.access.MarkovModelAccessor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;

public abstract class DeductiveStateSpaceNavigator extends StateSpaceNavigator {

	protected final MarkovModelAccessor markovModelAccessor;
	
	public DeductiveStateSpaceNavigator(MarkovModel markovModel) {
		this.markovModelAccessor = MarkovModelAccessor.of(markovModel);
	}

}
