package org.palladiosimulator.simexp.environmentaldynamics.builder;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.ObservableEnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.UnobservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class EnvironmentalProcessBuilder<S, A, Aa extends Action<A>, R> {

    private ProbabilityMassFunction<State<S>> initialDist = null;
    private ObservationProducer<S> obsProducer = null;
    private Optional<MarkovModel<S, A, R>> model = Optional.empty();
    private boolean isHidden = false;

    private EnvironmentalProcessBuilder(MarkovModel<S, A, R> model) {
        this.model = Optional.ofNullable(model);
    }

    public static <S, A, Aa extends Action<A>, R> EnvironmentalProcessBuilder<S, A, Aa, R> describedBy(
            MarkovModel<S, A, R> model) {
        return new EnvironmentalProcessBuilder<>(model);
    }

    public EnvironmentalProcessBuilder<S, A, Aa, R> andInitiallyDistributedWith(
            ProbabilityMassFunction<State<S>> initialDist) {
        this.initialDist = initialDist;
        return this;
    }

    public EnvironmentalProcessBuilder<S, A, Aa, R> asHiddenProcessWith(ObservationProducer<S> obsProducer) {
        this.isHidden = true;
        this.obsProducer = obsProducer;
        return this;
    }

    public EnvironmentProcess<S, A, R> build() {
        // TODO exception handling
        requireNonNull(initialDist, "");
        if (isHidden) {
            requireNonNull(obsProducer, "");
        }

        if (model.isPresent()) {
            return buildAsDescribableProcess();
        }

        throw new RuntimeException("");
    }

    private EnvironmentProcess<S, A, R> buildAsDescribableProcess() {
        if (isHidden) {
            return new UnobservableEnvironmentProcess<S, A, Aa, R>(model.get(), initialDist, obsProducer);
        }
        return new ObservableEnvironmentProcess<S, A, Aa, R>(model.get(), initialDist);
    }

}
