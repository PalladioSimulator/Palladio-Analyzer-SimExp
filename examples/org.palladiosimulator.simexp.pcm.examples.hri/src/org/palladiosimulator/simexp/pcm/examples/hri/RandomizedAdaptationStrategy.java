package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

import com.google.common.collect.Lists;

public class RandomizedAdaptationStrategy<C> extends ReliabilityPrioritizedStrategy<C> {

    private final Random random = new Random();

    public RandomizedAdaptationStrategy(SimulatedMeasurementSpecification responseTimeSpec, double thresholdRt) {
        super(responseTimeSpec, thresholdRt);
    }

    @Override
    public String getId() {
        return "RandomizedAdaptationStrategy";
    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        return true;
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        List<String> availableOptions = Lists.newArrayList();
        availableOptions.add("EmptyReconf");

        if (isFilteringActivated) {
            availableOptions.add("DeactivateFilterComponent");
        } else {
            availableOptions.add("ActivateFilterComponent");
        }

        if (isDefaultMLModelActivated) {
            availableOptions.add("SwitchToRobustMLModel");
        } else {
            availableOptions.add("SwitchToDefaultMLModel");
        }

        var randomlySelect = availableOptions.get(random.nextInt(3));
        if (randomlySelect.equals("ActivateFilterComponent")) {
            return activateFilteringReconfiguration(options);
        } else if (randomlySelect.equals("DeactivateFilterComponent")) {
            return deactivateFilteringReconfiguration(options);
        } else if (randomlySelect.equals("SwitchToDefaultMLModel")) {
            return switchToDefaultMLModel(options);
        } else if (randomlySelect.equals("SwitchToRobustMLModel")) {
            return switchToRobustMLModel(options);
        } else {
            return QVToReconfiguration.empty();
        }
    }

}
