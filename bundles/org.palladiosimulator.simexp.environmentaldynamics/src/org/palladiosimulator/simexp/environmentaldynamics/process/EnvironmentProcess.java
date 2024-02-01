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

public abstract class EnvironmentProcess<S, A, R, V> {

    private final boolean isHiddenProcess;

    protected final MarkovSampling<S, A, R> sampler;

    public EnvironmentProcess(Markovian<S, A, R> markovian, MarkovModel<S, A, R> model,
            ProbabilityMassFunction<State<S>> initialDistribution) {
        this.sampler = new MarkovSampling<>(MarkovianConfig.with(markovian));
        this.isHiddenProcess = isHiddenProcess(model);
    }

    public EnvironmentProcess(Markovian<S, A, R> markovian, DerivableEnvironmentalDynamic<S, A> dynamics,
            ProbabilityMassFunction<State<S>> initialDistribution) {
        this.sampler = new MarkovSampling<>(MarkovianConfig.with(markovian));
        this.isHiddenProcess = dynamics.isHiddenProcess();
    }

    private boolean isHiddenProcess(MarkovModel<S, A, R> model) {
        PerceivableEnvironmentalState<V> any = (PerceivableEnvironmentalState<V>) model.getStateSpace()
            .get(0);
        return any.isHidden();
    }

    protected static <S, A, R> StateSpaceNavigator<S, A> buildEnvironmentalDynamics(MarkovModel<S, A, R> model) {
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

    public abstract PerceivableEnvironmentalState<V> determineInitial();

    public abstract PerceivableEnvironmentalState<V> determineNextGiven(PerceivableEnvironmentalState<V> last);

}
