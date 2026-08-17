package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

class ConfigurationDoubleProperty extends ValueProperty<ILaunchConfiguration, Double> {
    private final String key;
    private final boolean isPrimitive;

    public ConfigurationDoubleProperty(String key, boolean isPrimitive) {
        this.key = key;
        this.isPrimitive = isPrimitive;
    }

    @Override
    public Object getValueType() {
        return Double.class;
    }

    @Override
    public IObservableValue<Double> observe(Realm realm, ILaunchConfiguration source) {
        return new ConfigurationObservableDoubleValue(source, key, isPrimitive);
    }
}
