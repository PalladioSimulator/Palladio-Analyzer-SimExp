package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class RobotCognitionReconfigurationStrategy<C>
        implements Policy<QVTOReconfigurator, Action<QVTOReconfigurator>>, Initializable {

    private final Threshold thresholdRt;
    private final Threshold thresholdRel;;

    private final SimulatedMeasurementSpecification reliabilitySpec;
    private final SimulatedMeasurementSpecification responseTimeSpec;

    private boolean isDefaultMLModelActivated = true;
    private boolean isFilteringActivated = false;

    public RobotCognitionReconfigurationStrategy(SimulatedMeasurementSpecification reliabilitySpec,
            SimulatedMeasurementSpecification responseTimeSpec, double thresholdRt, double thresholdRel) {
        this.reliabilitySpec = reliabilitySpec;
        this.responseTimeSpec = responseTimeSpec;
        this.thresholdRt = Threshold.lessThanOrEqualTo(thresholdRt);
        this.thresholdRel = Threshold.greaterThanOrEqualTo(thresholdRel);
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
    public Action<QVTOReconfigurator> select(State source, Set<Action<QVTOReconfigurator>> options) {
        if ((source instanceof SelfAdaptiveSystemState) == false) {
            throw new RuntimeException("");
        }

        var stateQuantity = ((SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue<CategoricalValue>>>) source)
            .getQuantifiedState();
        SimulatedMeasurement relSimMeasurement = stateQuantity.findMeasurementWith(reliabilitySpec)
            .orElseThrow();
        SimulatedMeasurement rtSimMeasurement = stateQuantity.findMeasurementWith(responseTimeSpec)
            .orElseThrow();

        var isReliabilityNotSatisfied = thresholdRel.isNotSatisfied(relSimMeasurement.getValue());
        var isResponseTimeNotSatisfied = thresholdRt.isNotSatisfied(rtSimMeasurement.getValue());

        if (isReliabilityNotSatisfied) {
            return manageReliability(asReconfigurations(options));
        } else if (isResponseTimeNotSatisfied) {
            return managePerformance(asReconfigurations(options));
        } else {
            return QVToReconfiguration.empty();
        }
    }

    private Action<QVTOReconfigurator> manageReliability(List<QVToReconfiguration> options) {
        if (isFilteringActivated == false) {
            return activateFilteringReconfiguration(options);
        } else if (isDefaultMLModelActivated) {
            return switchToRobustMLModel(options);
        } else {
            return QVToReconfiguration.empty();
        }
    }

    private Action<QVTOReconfigurator> managePerformance(List<QVToReconfiguration> options) {
        if (isDefaultMLModelActivated == false) {
            return switchToDefaultMLModel(options);
        } else if (isFilteringActivated) {
            return deactivateFilteringReconfiguration(options);
        } else {
            return QVToReconfiguration.empty();
        }
    }

    private Action<QVTOReconfigurator> activateFilteringReconfiguration(List<QVToReconfiguration> options) {
        isFilteringActivated = true;

        return selectOptionWith("ActivateFilterComponent", options);
    }

    private Action<QVTOReconfigurator> deactivateFilteringReconfiguration(List<QVToReconfiguration> options) {
        isFilteringActivated = false;

        return selectOptionWith("DeactivateFilterComponent", options);
    }

    private Action<QVTOReconfigurator> switchToRobustMLModel(List<QVToReconfiguration> options) {
        isDefaultMLModelActivated = false;

        return selectOptionWith("SwitchToRobustMLModel", options);
    }

    private Action<QVTOReconfigurator> switchToDefaultMLModel(List<QVToReconfiguration> options) {
        isDefaultMLModelActivated = true;

        return selectOptionWith("SwitchToDefaultMLModel", options);
    }

    private Action<QVTOReconfigurator> selectOptionWith(String queriedName, List<QVToReconfiguration> options) {
        for (QVToReconfiguration each : options) {
            String reconfName = each.getStringRepresentation();
            if (reconfName.equals(queriedName)) {
                return each;
            }
        }

        throw new RuntimeException("");
    }

    private List<QVToReconfiguration> asReconfigurations(Set<Action<QVTOReconfigurator>> options) {
        return options.stream()
            .map(each -> (QVToReconfiguration) each)
            .collect(Collectors.toList());
    }

}
