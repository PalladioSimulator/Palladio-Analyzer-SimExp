package org.palladiosimulator.simexp.markovian.termination;

import java.util.function.Predicate;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;

public interface TerminationCriterion<S, A, R, O> {
    public Predicate<SampleModel<S, A, R, O>> isReached();
}
