package org.palladiosimulator.simexp.environmentaldynamics.builder;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.ObservableEnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.UnobservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;

public class EnvironmentalProcessBuilder {

	private ProbabilityMassFunction initialDist = null;
	private ObservationProducer obsProducer = null;
	private Optional<MarkovModel> model = Optional.empty();
	private boolean isHidden = false;

	private EnvironmentalProcessBuilder(MarkovModel model) {
		this.model = Optional.ofNullable(model);
	}

	public static EnvironmentalProcessBuilder describedBy(MarkovModel model) {
		return new EnvironmentalProcessBuilder(model);
	}

	public EnvironmentalProcessBuilder andInitiallyDistributedWith(ProbabilityMassFunction initialDist) {
		this.initialDist = initialDist;
		return this;
	}

	public EnvironmentalProcessBuilder asHiddenProcessWith(ObservationProducer obsProducer) {
		this.isHidden = true;
		this.obsProducer = obsProducer;
		return this;
	}

	public EnvironmentProcess build() {
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

	private EnvironmentProcess buildAsDescribableProcess() {
		if (isHidden) {
			return new UnobservableEnvironmentProcess(model.get(), initialDist, obsProducer);
		}
		return new ObservableEnvironmentProcess(model.get(), initialDist);
	}

}
