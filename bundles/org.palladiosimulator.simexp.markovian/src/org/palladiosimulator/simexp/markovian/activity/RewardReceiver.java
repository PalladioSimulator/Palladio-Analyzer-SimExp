package org.palladiosimulator.simexp.markovian.activity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface RewardReceiver<A, R> {

    public Reward<R> obtain(Sample<A, R> sample);
}
