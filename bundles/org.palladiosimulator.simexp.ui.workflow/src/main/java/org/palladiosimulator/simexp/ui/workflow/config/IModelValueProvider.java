package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.observable.value.IObservableValue;

public interface IModelValueProvider {
    IObservableValue<String> getExperimentsModel();
}
