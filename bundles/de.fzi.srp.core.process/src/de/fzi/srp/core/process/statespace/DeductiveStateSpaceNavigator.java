package de.fzi.srp.core.process.statespace;

import de.fzi.srp.core.model.markovmodel.markoventity.MarkovModel;
import de.fzi.srp.core.process.access.MarkovModelAccessor;

public abstract class DeductiveStateSpaceNavigator extends StateSpaceNavigator {

	protected final MarkovModelAccessor markovModelAccessor;
	
	public DeductiveStateSpaceNavigator(MarkovModel markovModel) {
		this.markovModelAccessor = MarkovModelAccessor.of(markovModel);
	}

}
