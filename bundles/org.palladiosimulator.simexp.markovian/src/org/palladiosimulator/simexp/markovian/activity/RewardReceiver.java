package org.palladiosimulator.simexp.markovian.activity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface RewardReceiver<T> {
	
	public Reward<T> obtain(Sample<T> sample);
}
