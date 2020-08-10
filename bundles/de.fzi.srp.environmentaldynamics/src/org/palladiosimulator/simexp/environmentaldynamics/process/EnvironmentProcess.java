package org.palladiosimulator.simexp.environmentaldynamics.process;

import static org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalDynamic.describedBy;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.HiddenEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.access.SampleModelAccessor;
import org.palladiosimulator.simexp.markovian.config.MarkovianConfig;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public abstract class EnvironmentProcess {

	private final boolean isHiddenProcess;

	protected final MarkovSampling sampler;

	public EnvironmentProcess(MarkovModel model, ProbabilityMassFunction initialDistribution) {
		Markovian markovian = buildMarkovian(buildEnvironmentalDynamics(model), initialDistribution);
		this.sampler = new MarkovSampling(MarkovianConfig.with(markovian));
		this.isHiddenProcess = isHiddenProcess(model);
	}

	public EnvironmentProcess(DerivableEnvironmentalDynamic dynamics, ProbabilityMassFunction initialDistribution) {
		this.sampler = new MarkovSampling(MarkovianConfig.with(buildMarkovian(dynamics, initialDistribution)));
		this.isHiddenProcess = dynamics.isHiddenProcess();
	}

	private boolean isHiddenProcess(MarkovModel model) {
		return model.getStateSpace().get(0) instanceof HiddenEnvironmentalState;
	}
	
	private StateSpaceNavigator buildEnvironmentalDynamics(MarkovModel model) {
		return (StateSpaceNavigator) describedBy(model).asExploitationProcess().build();
	}

	protected Sample determineNextSampleGiven(State last) {
		Sample lastAsSample = SampleModelAccessor.createSampleBy(last);
		return sampler.drawSampleGiven(lastAsSample);
	}

	public boolean isHiddenProcess() {
		return isHiddenProcess;
	}

	protected abstract Markovian buildMarkovian(StateSpaceNavigator environmentalDynamics,
			ProbabilityMassFunction initialDistribution);

	public abstract PerceivableEnvironmentalState determineInitial();

	public abstract PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last);

}
