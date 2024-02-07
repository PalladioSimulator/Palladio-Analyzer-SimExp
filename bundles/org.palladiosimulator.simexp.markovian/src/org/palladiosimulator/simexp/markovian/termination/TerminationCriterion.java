package org.palladiosimulator.simexp.markovian.termination;

import java.util.function.Predicate;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;

public interface TerminationCriterion<S, A, R> {
    public Predicate<SampleModel<S, A, R, State<S>>> isReached();
}
