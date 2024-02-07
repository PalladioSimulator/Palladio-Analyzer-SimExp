package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.termination.TerminationCriterion;

public class MarkovProcessConfig<S, A, R, O> {
    public int horizonLength;
    public MarkovModel<S, A, R> markovModel;
    public TerminationCriterion<S, A, R, O> criterion;
    private SampleModel<S, A, R, O> sampleModel;

    public Optional<SampleModel<S, A, R, O>> getSampleModel() {
        return sampleModel == null ? Optional.empty() : Optional.of(sampleModel);
    }
}
