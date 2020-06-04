package org.palladiosimulator.simexp.markovian.activity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface RewardReceiver {
	
	public Reward<?> obtain(Sample sample);
}
