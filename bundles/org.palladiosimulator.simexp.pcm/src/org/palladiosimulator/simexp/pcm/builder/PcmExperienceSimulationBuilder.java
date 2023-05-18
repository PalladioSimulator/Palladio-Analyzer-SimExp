package org.palladiosimulator.simexp.pcm.builder;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.builder.ExperienceSimulationBuilder;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PcmExperienceSimulationBuilder extends ExperienceSimulationBuilder {
	
	private List<ExperienceSimulationRunner> simRunner = Lists.newArrayList();
	private Set<SimulatedMeasurementSpecification> specs = Sets.newHashSet();
	private Experiment initial = null;
	
	public class GlobalPcmSettingsBuilder {
		
		public GlobalPcmSettingsBuilder withInitialExperiment(Experiment initial) {
			PcmExperienceSimulationBuilder.this.initial = initial;
			return this;
		}
		
		public GlobalPcmSettingsBuilder andSimulatedMeasurementSpecs(Set<SimulatedMeasurementSpecification> specs) {
			PcmExperienceSimulationBuilder.this.specs = specs;
			return this;
		}
		
		public GlobalPcmSettingsBuilder addExperienceSimulationRunner(ExperienceSimulationRunner runner) {
			PcmExperienceSimulationBuilder.this.simRunner.add(runner);
			return this;
		}
		
		public GlobalPcmSettingsBuilder addExperienceSimulationRunners(Set<ExperienceSimulationRunner> runners) {
			PcmExperienceSimulationBuilder.this.simRunner.addAll(runners);
			return this;
		}
		
		public PcmExperienceSimulationBuilder done() {
			return PcmExperienceSimulationBuilder.this;
		}
		
	}
	
	@Override
	public ExperienceSimulator build() {
		//TODO Exception handling
		Objects.requireNonNull(initial, "");
		if (Boolean.logicalOr(specs.isEmpty(), simRunner.isEmpty())) {
			throw new RuntimeException("");
		}
		
		ExperimentProvider.create(initial);
		
		return super.build();
	}

	public static PcmExperienceSimulationBuilder newBuilder() {
		return new PcmExperienceSimulationBuilder();
	}
	
	public GlobalPcmSettingsBuilder makeGlobalPcmSettings() {
		return new GlobalPcmSettingsBuilder();
	}

	@Override
	protected List<ExperienceSimulationRunner> getSimulationRunner() {
		return simRunner;
	}

	@Override
	protected InitialSelfAdaptiveSystemStateCreator createInitialSassCreator() {
		return new InitialPcmStateCreator(specs);
	}

}
