package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;

public interface IModelledOptimizerProvider {
    SelectObservableValue<ModelledOptimizationType> getModelledOptimizationType();
}
