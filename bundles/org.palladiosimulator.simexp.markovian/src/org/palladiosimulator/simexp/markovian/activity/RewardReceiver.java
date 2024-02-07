package org.palladiosimulator.simexp.markovian.activity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface RewardReceiver<S, A, R, O> {

    public Reward<R> obtain(Sample<S, A, R, O> sample);
}
