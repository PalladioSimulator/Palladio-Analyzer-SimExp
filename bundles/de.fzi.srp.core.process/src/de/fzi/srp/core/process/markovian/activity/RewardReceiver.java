package de.fzi.srp.core.process.markovian.activity;

import de.fzi.srp.core.model.markovmodel.markoventity.Reward;
import de.fzi.srp.core.model.markovmodel.samplemodel.Sample;

public interface RewardReceiver {
	
	public Reward<?> obtain(Sample sample);
}
