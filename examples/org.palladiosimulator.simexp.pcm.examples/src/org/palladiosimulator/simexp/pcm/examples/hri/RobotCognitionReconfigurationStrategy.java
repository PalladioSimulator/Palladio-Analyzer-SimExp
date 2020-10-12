package org.palladiosimulator.simexp.pcm.examples.hri;

import static org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutor.LOWER_THRESHOLD_REL;
import static org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutor.UPPER_THRESHOLD_RT;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public class RobotCognitionReconfigurationStrategy implements Policy<Action<?>>, Initializable {

	private static final Threshold THRESHOLD_RT = Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT);
	private static final Threshold THRESHOLD_REL = Threshold.greaterThanOrEqualTo(LOWER_THRESHOLD_REL);

	private final SimulatedMeasurementSpecification reliabilitySpec;
	private final SimulatedMeasurementSpecification responseTimeSpec;

	private boolean isDefaultMLModelActivated = true;
	private boolean isFilteringActivated = false;

	public RobotCognitionReconfigurationStrategy(SimulatedMeasurementSpecification reliabilitySpec,
			SimulatedMeasurementSpecification responseTimeSpec) {
		this.reliabilitySpec = reliabilitySpec;
		this.responseTimeSpec = responseTimeSpec;
	}

	@Override
	public void initialize() {
		isDefaultMLModelActivated = true;
		isFilteringActivated = false;
	}

	@Override
	public String getId() {
		return "SimpleStrategy";
	}

	@Override
	public Action<?> select(State source, Set<Action<?>> options) {
		if ((source instanceof SelfAdaptiveSystemState<?>) == false) {
			throw new RuntimeException("");
		}

		var stateQuantity = ((SelfAdaptiveSystemState<?>) source).getQuantifiedState();
		SimulatedMeasurement relSimMeasurement = stateQuantity.findMeasurementWith(reliabilitySpec).orElseThrow();
		SimulatedMeasurement rtSimMeasurement = stateQuantity.findMeasurementWith(responseTimeSpec).orElseThrow();

		var isReliabilityNotSatisfied = THRESHOLD_REL.isNotSatisfied(relSimMeasurement.getValue());
		var isResponseTimeNotSatisfied = THRESHOLD_RT.isNotSatisfied(rtSimMeasurement.getValue());

		if (isReliabilityNotSatisfied) {
			return manageReliability(asReconfigurations(options));
		} else if (isResponseTimeNotSatisfied) {
			return managePerformance(asReconfigurations(options));
		} else {
			return QVToReconfiguration.empty();
		}
	}

	private Action<?> manageReliability(List<QVToReconfiguration> options) {
		if (isFilteringActivated == false) {
			return activateFilteringReconfiguration(options);
		} else if (isDefaultMLModelActivated) {
			return switchToRobustMLModel(options);
		} else {
			return QVToReconfiguration.empty();
		}
	}

	private Action<?> managePerformance(List<QVToReconfiguration> options) {
		if (isDefaultMLModelActivated == false) {
			return switchToDefaultMLModel(options);
		} else if (isFilteringActivated) {
			return deactivateFilteringReconfiguration(options);
		} else {
			return QVToReconfiguration.empty();
		}
	}

	private Action<?> activateFilteringReconfiguration(List<QVToReconfiguration> options) {
		isFilteringActivated = true;

		return selectOptionWith("ActivateFilterComponent", options);
	}

	private Action<?> deactivateFilteringReconfiguration(List<QVToReconfiguration> options) {
		isFilteringActivated = false;

		return selectOptionWith("DeactivateFilterComponent", options);
	}

	private Action<?> switchToRobustMLModel(List<QVToReconfiguration> options) {
		isDefaultMLModelActivated = false;

		return selectOptionWith("SwitchToRobustMLModel", options);
	}

	private Action<?> switchToDefaultMLModel(List<QVToReconfiguration> options) {
		isDefaultMLModelActivated = true;

		return selectOptionWith("SwitchToDefaultMLModel", options);
	}

	private Action<?> selectOptionWith(String queriedName, List<QVToReconfiguration> options) {
		for (QVToReconfiguration each : options) {
			String reconfName = each.getStringRepresentation();
			if (reconfName.equals(queriedName)) {
				return each;
			}
		}

		throw new RuntimeException("");
	}

	private List<QVToReconfiguration> asReconfigurations(Set<Action<?>> options) {
		return options.stream().map(each -> (QVToReconfiguration) each).collect(Collectors.toList());
	}

}
