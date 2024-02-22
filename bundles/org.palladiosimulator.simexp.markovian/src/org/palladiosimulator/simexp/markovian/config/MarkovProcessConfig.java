package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.termination.TerminationCriterion;

public class MarkovProcessConfig<A, R> {
    public int horizonLength;
    public MarkovModel<A, R> markovModel;
    public TerminationCriterion<A, R> criterion;
    private SampleModel<A, R> sampleModel;

    public Optional<SampleModel<A, R>> getSampleModel() {
        return sampleModel == null ? Optional.empty() : Optional.of(sampleModel);
    }
}
