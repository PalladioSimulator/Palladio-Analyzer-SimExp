package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class ReliabilityPrioritizedStrategy<C> extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration>
        implements Initializable {
    private static final String IMG_BRIGHTNESS_TEMPLATE = "_U5Fzu8qkEeqObY-eK2jOOA";
    private static final String IMG_NOISE_TEMPLATE = "_0uh4oCpGEeuMpaabmuiN-Q";

    private final SimulatedMeasurementSpecification responseTimeSpec;
    private final Threshold thresholdRt;

    protected boolean isDefaultMLModelActivated = true;
    protected boolean isFilteringActivated = false;

    public ReliabilityPrioritizedStrategy(SimulatedMeasurementSpecification responseTimeSpec, double thresholdRt) {
        this.responseTimeSpec = responseTimeSpec;
        this.thresholdRt = Threshold.lessThanOrEqualTo(thresholdRt);
    }

    @Override
    public String getId() {
        return "ReliabilityPrioritizedStrategy";
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {
        var sasState = (SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue>>) source;

        var stateQuantity = sasState.getQuantifiedState();
        SimulatedMeasurement rtSimMeasurement = stateQuantity.findMeasurementWith(responseTimeSpec)
            .orElseThrow();
        knowledge.store(responseTimeSpec.getId(), rtSimMeasurement);

        var envState = sasState.getPerceivedEnvironmentalState()
            .getValue()
            .getValue();
        for (InputValue each : RobotCognitionEnvironmentalDynamics.toInputs(envState)) {
            var template = each.getVariable().getInstantiatedTemplate();
            if (template.getId()
                .equals(IMG_BRIGHTNESS_TEMPLATE)) {
                knowledge.store(IMG_BRIGHTNESS_TEMPLATE, each.value);
            } else if (template.getId()
                .equals(IMG_NOISE_TEMPLATE)) {
                knowledge.store(IMG_NOISE_TEMPLATE, each.value);
            }
        }
    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        var rtSimMeasurement = knowledge.getValue(responseTimeSpec.getId())
            .map(SimulatedMeasurement.class::cast)
            .get();
        var isResponseTimeNotSatisfied = thresholdRt.isNotSatisfied(rtSimMeasurement.getValue());
        if (isResponseTimeNotSatisfied) {
            return true;
        }

        var sensorNoise = knowledge.getValue(IMG_NOISE_TEMPLATE)
            .map(CategoricalValue.class::cast)
            .get();
        var isSensorNoiseIncreased = !sensorNoise.get()
            .equals("(SensorNoise=Low)");
        if (isSensorNoiseIncreased) {
            return true;
        }

        var imgBrightness = knowledge.getValue(IMG_BRIGHTNESS_TEMPLATE)
            .map(CategoricalValue.class::cast)
            .get();
        var isImgBrightnessIncreased = !imgBrightness.get()
            .equals("(ImageBrightnessMeasure=Medium)");
        if (isImgBrightnessIncreased) {
            return true;
        }

        return false;
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        var sensorNoise = knowledge.getValue(IMG_NOISE_TEMPLATE)
            .map(CategoricalValue.class::cast)
            .get();
        var isSensorNoiseIncreased = !sensorNoise.get()
            .equals("(SensorNoise=Low)");
        var imgBrightness = knowledge.getValue(IMG_BRIGHTNESS_TEMPLATE)
            .map(CategoricalValue.class::cast)
            .get();
        var isImgBrightnessIncreased = !imgBrightness.get()
            .equals("(ImageBrightnessMeasure=Medium)");
        if (isSensorNoiseIncreased || isImgBrightnessIncreased) {
            return manageReliability(isSensorNoiseIncreased, isImgBrightnessIncreased, options);
        }

        var rtSimMeasurement = knowledge.getValue(responseTimeSpec.getId())
            .map(SimulatedMeasurement.class::cast)
            .get();
        var isResponseTimeNotSatisfied = thresholdRt.isNotSatisfied(rtSimMeasurement.getValue());
        if (isResponseTimeNotSatisfied) {
            return managePerformance(options);
        }

        return QVToReconfiguration.empty();
    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return QVToReconfiguration.empty();
    }

    @Override
    public void initialize() {
        isDefaultMLModelActivated = true;
        isFilteringActivated = false;
    }

    private QVToReconfiguration manageReliability(boolean isSensorNoiseIncreased, boolean isImgBrightnessIncreased,
            Set<QVToReconfiguration> options) {
        var bothValuesIncreased = isSensorNoiseIncreased && isImgBrightnessIncreased;
        if (bothValuesIncreased && isDefaultMLModelActivated) {
            return switchToRobustMLModel(options);
        }

        if (isSensorNoiseIncreased && isFilteringActivated == false) {
            return activateFilteringReconfiguration(options);
        }

        return QVToReconfiguration.empty();
    }

    private QVToReconfiguration managePerformance(Set<QVToReconfiguration> options) {
        if (isDefaultMLModelActivated == false) {
            return switchToDefaultMLModel(options);
        } else if (isFilteringActivated) {
            return deactivateFilteringReconfiguration(options);
        } else {
            return QVToReconfiguration.empty();
        }
    }

    protected QVToReconfiguration activateFilteringReconfiguration(Set<QVToReconfiguration> options) {
        isFilteringActivated = true;

        return selectOptionWith("ActivateFilterComponent", options);
    }

    protected QVToReconfiguration deactivateFilteringReconfiguration(Set<QVToReconfiguration> options) {
        isFilteringActivated = false;

        return selectOptionWith("DeactivateFilterComponent", options);
    }

    protected QVToReconfiguration switchToRobustMLModel(Set<QVToReconfiguration> options) {
        isDefaultMLModelActivated = false;

        return selectOptionWith("SwitchToRobustMLModel", options);
    }

    protected QVToReconfiguration switchToDefaultMLModel(Set<QVToReconfiguration> options) {
        isDefaultMLModelActivated = true;

        return selectOptionWith("SwitchToDefaultMLModel", options);
    }

    private QVToReconfiguration selectOptionWith(String queriedName, Set<QVToReconfiguration> options) {
        for (QVToReconfiguration each : options) {
            String reconfName = each.getStringRepresentation();
            if (reconfName.equals(queriedName)) {
                return each;
            }
        }

        throw new RuntimeException("");
    }

}
