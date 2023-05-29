package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class NStepLoadBalancerStrategy implements Policy<Action<?>> {
	
	private final static String SIMPLE_ADAPTATION_STRATEGY_NAME = "StepAdaptationStrategy";
	
	private final Threshold upperRtThreshold;;
	private final Threshold lowerRtThreshold;
	
	private final int stepSize;
	private final PcmMeasurementSpecification pcmSpec;
	
	public NStepLoadBalancerStrategy(int stepSize, PcmMeasurementSpecification pcmSpec, double upperThresholdRt, double lowerThresholdRt) {
		this.stepSize = stepSize;
		this.pcmSpec = pcmSpec;
		this.upperRtThreshold = Threshold.lessThanOrEqualTo(upperThresholdRt);
		this.lowerRtThreshold = Threshold.greaterThanOrEqualTo(lowerThresholdRt);
	}
	
	@Override
	public String getId() {
		return stepSize + SIMPLE_ADAPTATION_STRATEGY_NAME;
	}

	@Override
	public Action<?> select(State source, Set<Action<?>> options) {
		// TODO Exception handling
		if ((source instanceof SelfAdaptiveSystemState<?>) == false) {
			throw new RuntimeException("");
		}
		
		SelfAdaptiveSystemState<?> sassState = (SelfAdaptiveSystemState<?>) source;
		SimulatedMeasurement simMeasurement = sassState.getQuantifiedState().findMeasurementWith(pcmSpec)
																		    .orElseThrow(() -> new RuntimeException(""));
		Double value = simMeasurement.getValue();
		if (upperRtThreshold.isNotSatisfied(value)) {
			return outSource(asReconfigurations(options));
		}
		
		if (lowerRtThreshold.isNotSatisfied(value)) {
			return scaleIn(asReconfigurations(options));
		}
		
		return QVToReconfiguration.empty();
	}

	private Action<?> outSource(List<QVToReconfiguration> options) {
		for (QVToReconfiguration each : options) {
			String reconfName = each.getStringRepresentation();
			if (reconfName.equals(outsource())) {
				return each;
			}
		}
		
		throw new RuntimeException("");
	}

	private Action<?> scaleIn(List<QVToReconfiguration> options) {
		for (QVToReconfiguration each : options) {
			String reconfName = each.getStringRepresentation();
			if (reconfName.equals(scaleIn())) {
				return each;
			}
		}
		
		throw new RuntimeException("");
	}
	
	private List<QVToReconfiguration> asReconfigurations(Set<Action<?>> options) {
		return options.stream().map(each -> (QVToReconfiguration) each).collect(Collectors.toList());
	}
	
	private String scaleIn() {
		return String.format("Step%sScaleIn", Integer.toString(stepSize));
	}
	
	private String outsource() {
		return String.format("Step%sOutsource", Integer.toString(stepSize));
	}
	
}
