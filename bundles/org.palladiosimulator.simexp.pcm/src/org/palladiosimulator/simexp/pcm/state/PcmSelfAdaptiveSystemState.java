package org.palladiosimulator.simexp.pcm.state;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.solver.models.PCMInstance;

public class PcmSelfAdaptiveSystemState extends SelfAdaptiveSystemState<PCMInstance> {
	
	public static class PcmSassBuilder {
		
		private Set<SimulatedMeasurementSpecification> specs = new HashSet<>();
		private PcmArchitecturalConfiguration initialArch = null; 
		private PerceivableEnvironmentalState initialEnv = null;
		private boolean isInitial = false;
		
		public PcmSassBuilder withStructuralState(PcmArchitecturalConfiguration initialArch, PerceivableEnvironmentalState initialEnv) {
			this.initialArch = initialArch;
			this.initialEnv = initialEnv;
			return this;
		}
		
		public PcmSassBuilder andMetricDescriptions(Set<SimulatedMeasurementSpecification> specs) {
			this.specs.addAll(specs);
			return this;
		}
		
		public PcmSassBuilder asInitial() {
			isInitial = true;
			return this;
		}
		
		public PcmSelfAdaptiveSystemState build() {
			//TODO Exception handling
			Objects.requireNonNull(initialArch, "");
			Objects.requireNonNull(initialEnv, "");
			if (specs.isEmpty()) {
				throw new RuntimeException("");
			}
			
			return new PcmSelfAdaptiveSystemState(initialArch, initialEnv, specs, isInitial);
		}
		
	}
	
	private PcmSelfAdaptiveSystemState(PcmArchitecturalConfiguration archConf, 
									   PerceivableEnvironmentalState perceivedState, 
									   Set<SimulatedMeasurementSpecification> specs,
									   boolean isInitial) {
		this.quantifiedState = StateQuantity.of(toMeasuredQuantities(specs));
		this.archConfiguration = archConf;
		this.perceivedState = perceivedState;
		
		applyChanges(perceivedState);
		if (isInitial) {
			determineQuantifiedState();
		}
	}
	
	public static PcmSassBuilder newBuilder() {
		return new PcmSassBuilder();
	}
	
	private List<SimulatedMeasurement> toMeasuredQuantities(Set<SimulatedMeasurementSpecification> specs) {
		return specs.stream().map(each -> SimulatedMeasurement.with(each)).collect(Collectors.toList());
	}
	
	public boolean isSteadyState() {
		for (SimulatedMeasurement each : quantifiedState.getMeasurements()) {
			Optional<Threshold> evaluator = ((PcmMeasurementSpecification) each.getSpecification()).getSteadyStateEvaluator();
			if (evaluator.isPresent()) {
				if (evaluator.get().isNotSatisfied(each.getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public SelfAdaptiveSystemState<?> transitToNext(PerceivableEnvironmentalState perceivedState, ArchitecturalConfiguration<?> archConf) {
		return newBuilder()
				.withStructuralState((PcmArchitecturalConfiguration) archConf, perceivedState)
				.andMetricDescriptions(InitialPcmStateCreator.getMeasurementSpecs())
				.build();
	}
	
	private void applyChanges(PerceivableEnvironmentalState perceivedState) {
		if (perceivedState instanceof PcmModelChange) {
			((PcmModelChange) perceivedState).apply(perceivedState.getValue());
		}
		//TODO logging or exception handling
	}
	
}
