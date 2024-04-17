package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

class ConfigurationIntegerProperty extends ValueProperty<ILaunchConfiguration, Integer> {
    private final String key;

    public ConfigurationIntegerProperty(String key) {
        this.key = key;
    }

    @Override
    public Object getValueType() {
        return Integer.class;
    }

    @Override
    public IObservableValue<Integer> observe(Realm realm, ILaunchConfiguration source) {
        return new ConfigurationObservableIntegerValue(source, key);
    }
}
