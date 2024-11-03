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
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;

public class EnvironmentalProcessBuilder<A, Aa extends Action<A>, R, V> {

    private ProbabilityMassFunction<State> initialDist = null;
    private SampleDumper sampleDumper = null;
    private ObservationProducer obsProducer = null;
    private Optional<MarkovModel<A, R>> model = Optional.empty();
    private boolean isHidden = false;

    private EnvironmentalProcessBuilder(MarkovModel<A, R> model) {
        this.model = Optional.ofNullable(model);
    }

    public static <A, Aa extends Action<A>, R, V> EnvironmentalProcessBuilder<A, Aa, R, V> describedBy(
            MarkovModel<A, R> model) {
        return new EnvironmentalProcessBuilder<>(model);
    }

    public EnvironmentalProcessBuilder<A, Aa, R, V> andInitiallyDistributedWith(
            ProbabilityMassFunction<State> initialDist) {
        this.initialDist = initialDist;
        return this;
    }

    public EnvironmentalProcessBuilder<A, Aa, R, V> usingSampleDumper(SampleDumper sampleDumper) {
        this.sampleDumper = sampleDumper;
        return this;
    }

    public EnvironmentalProcessBuilder<A, Aa, R, V> asHiddenProcessWith(ObservationProducer obsProducer) {
        this.isHidden = true;
        this.obsProducer = obsProducer;
        return this;
    }

    public EnvironmentProcess<A, R, V> build() {
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

    private EnvironmentProcess<A, R, V> buildAsDescribableProcess() {
        if (isHidden) {
            return new UnobservableEnvironmentProcess<A, Aa, R, V>(model.get(), sampleDumper, initialDist, obsProducer);
        }
        return new ObservableEnvironmentProcess<A, Aa, R, V>(model.get(), sampleDumper, initialDist);
    }

}
