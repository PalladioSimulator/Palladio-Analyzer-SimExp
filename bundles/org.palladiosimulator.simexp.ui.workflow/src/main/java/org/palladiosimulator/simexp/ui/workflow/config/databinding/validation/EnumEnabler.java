package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;

public class EnumEnabler<E extends Enum<E>> implements ControllableValidator.Enabled {
    private final E enabledEnum;
    private final IObservableValue<E> observedValue;

    public EnumEnabler(E enabledEnum, SelectObservableValue<E> observableValue) {
        this.enabledEnum = enabledEnum;
        observedValue = ComputedValue.create(() -> {
            return observableValue.getValue();
        });
    }

    @Override
    public boolean isEnabled() {
        E selectedValue = observedValue.getValue();
        return enabledEnum == selectedValue;
    }
}
