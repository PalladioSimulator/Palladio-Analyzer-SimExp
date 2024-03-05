package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

class ConfigurationStringsProperty extends ValueProperty<ILaunchConfiguration, String[]> {
    private final String key;

    public ConfigurationStringsProperty(String key) {
        this.key = key;
    }

    @Override
    public Object getValueType() {
        return String[].class;
    }

    @Override
    public IObservableValue<String[]> observe(Realm realm, ILaunchConfiguration source) {
        return new ConfigurationObservableArrayValue(source, key);
    }
}
