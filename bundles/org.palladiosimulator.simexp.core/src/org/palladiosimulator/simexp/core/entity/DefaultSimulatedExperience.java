package org.palladiosimulator.simexp.core.entity;

import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DefaultSimulatedExperience implements SimulatedExperience {

	private final static String NONE = "-";
	
	private final Sample sample;
	
	private DefaultSimulatedExperience(Sample sample) {
		this.sample = sample;
	}
	
	public static DefaultSimulatedExperience of(Sample sample) {
		return new DefaultSimulatedExperience(sample);
	}
	
	public static String deriveIdFrom(Sample sample) {
		DefaultSimulatedExperience helper = DefaultSimulatedExperience.of(sample);
		return String.format("%1s_%2s_%3s", helper.getCurrent().toString(), 
											helper.getReconfiguration(), 
											helper.getNext().toString()) ;
	}
	
	@Override
	public String getConfigurationDifferenceBefore() {
		ArchitecturalConfiguration<?> current = ((SelfAdaptiveSystemState<?>) sample.getCurrent()).getArchitecturalConfiguration(); 
		ArchitecturalConfiguration<?> next = ((SelfAdaptiveSystemState<?>) sample.getNext()).getArchitecturalConfiguration();
		return current.difference(next);
	}
	
	@Override
	public String getConfigurationDifferenceAfter() {
		ArchitecturalConfiguration<?> current = ((SelfAdaptiveSystemState<?>) sample.getCurrent()).getArchitecturalConfiguration(); 
		ArchitecturalConfiguration<?> next = ((SelfAdaptiveSystemState<?>) sample.getNext()).getArchitecturalConfiguration();
		return next.difference(current);
	}
	
	@Override
	public String getReconfiguration() {
		return sample.getAction().toString();
	}
	
	@Override
	public String getQuantifiedStateOfCurrent() {
		return getCurrent().getQuantifiedState().toString();
	}
	
	@Override
	public String getQuantifiedStateOfNext() {
		return getNext().getQuantifiedState().toString();
	}
	
	@Override
	public String getEnvironmentalStateBefore() {
		return getCurrent().getPerceivedEnvironmentalState().getStringRepresentation();
	}
	
	@Override
	public String getEnvironmentalStateAfter() {
		return getNext().getPerceivedEnvironmentalState().getStringRepresentation();
	}
	
	@Override
	public String getEnvironmentalStateObservation() {
		PerceivableEnvironmentalState perception = getNext().getPerceivedEnvironmentalState();
		if (perception.isHidden()) {
			return perception.getStringRepresentation();
		}
		return NONE;
	}
	
	@Override
	public String getId() {
		return deriveIdFrom(sample);
	}
	
	private SelfAdaptiveSystemState<?> getCurrent() {
		return ((SelfAdaptiveSystemState<?>) sample.getCurrent());
	}
	
	private SelfAdaptiveSystemState<?> getNext() {
		return ((SelfAdaptiveSystemState<?>) sample.getNext());
	}

	@Override
	public String getReward() {
		return sample.getReward().toString();
	}
}
