package org.palladiosimulator.simexp.environmentaldynamics.process;

import static org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalDynamic.describedBy;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.access.SampleModelAccessor;
import org.palladiosimulator.simexp.markovian.config.MarkovianConfig;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public abstract class EnvironmentProcess<S, A, R> {

    private final boolean isHiddenProcess;

    protected final MarkovSampling<S, A, R> sampler;

    public EnvironmentProcess(MarkovModel<S, A, R> model, ProbabilityMassFunction<State<S>> initialDistribution) {
        Markovian<S, A, R> markovian = buildMarkovian(buildEnvironmentalDynamics(model), initialDistribution);
        this.sampler = new MarkovSampling<>(MarkovianConfig.with(markovian));
        this.isHiddenProcess = isHiddenProcess(model);
    }

    public EnvironmentProcess(DerivableEnvironmentalDynamic<S, A> dynamics,
            ProbabilityMassFunction<State<S>> initialDistribution) {
        this.sampler = new MarkovSampling<>(MarkovianConfig.with(buildMarkovian(dynamics, initialDistribution)));
        this.isHiddenProcess = dynamics.isHiddenProcess();
    }

    private boolean isHiddenProcess(MarkovModel<S, A, R> model) {
        PerceivableEnvironmentalState any = (PerceivableEnvironmentalState) model.getStateSpace()
            .get(0);
        return any.isHidden();
    }

    private StateSpaceNavigator<S, A> buildEnvironmentalDynamics(MarkovModel<S, A, R> model) {
        return (StateSpaceNavigator<S, A>) describedBy(model).asExploitationProcess()
            .build();
    }

    protected Sample<S, A, R> determineNextSampleGiven(State<S> last) {
        Sample<S, A, R> lastAsSample = SampleModelAccessor.createSampleBy(last);
        return sampler.drawSampleGiven(lastAsSample);
    }

    public boolean isHiddenProcess() {
        return isHiddenProcess;
    }

    protected abstract Markovian<S, A, R> buildMarkovian(StateSpaceNavigator<S, A> environmentalDynamics,
            ProbabilityMassFunction<State<S>> initialDistribution);

    public abstract PerceivableEnvironmentalState determineInitial();

    public abstract PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last);

}
