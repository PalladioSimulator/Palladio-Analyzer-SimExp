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

public abstract class EnvironmentProcess<A, R> {

    private final boolean isHiddenProcess;

    protected final MarkovSampling<A, R> sampler;

    public EnvironmentProcess(Markovian<A, R> markovian, MarkovModel<A, R> model,
            ProbabilityMassFunction<State> initialDistribution) {
        this.sampler = new MarkovSampling<>(MarkovianConfig.with(markovian));
        this.isHiddenProcess = isHiddenProcess(model);
    }

    public EnvironmentProcess(Markovian<A, R> markovian, DerivableEnvironmentalDynamic<A> dynamics,
            ProbabilityMassFunction<State> initialDistribution) {
        this.sampler = new MarkovSampling<>(MarkovianConfig.with(markovian));
        this.isHiddenProcess = dynamics.isHiddenProcess();
    }

    private boolean isHiddenProcess(MarkovModel<A, R> model) {
        PerceivableEnvironmentalState any = (PerceivableEnvironmentalState) model.getStateSpace()
            .get(0);
        return any.isHidden();
    }

    protected static <A, R> StateSpaceNavigator<A> buildEnvironmentalDynamics(MarkovModel<A, R> model) {
        return (StateSpaceNavigator<A>) describedBy(model).asExploitationProcess()
            .build();
    }

    protected Sample<A, R> determineNextSampleGiven(State last) {
        Sample<A, R> lastAsSample = SampleModelAccessor.createSampleBy(last);
        return sampler.drawSampleGiven(lastAsSample);
    }

    public boolean isHiddenProcess() {
        return isHiddenProcess;
    }

    public abstract PerceivableEnvironmentalState determineInitial();

    public abstract PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last);

}
