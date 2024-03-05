package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ConfigurationEnumProperty<E extends Enum<E>> extends ValueProperty<ILaunchConfiguration, E> {
    private final String key;
    private final Class<E> enumType;

    public ConfigurationEnumProperty(String key, Class<E> enumType) {
        this.key = key;
        this.enumType = enumType;
    }

    @Override
    public Object getValueType() {
        return enumType;
    }

    @Override
    public IObservableValue<E> observe(Realm realm, ILaunchConfiguration source) {
        return new ConfigurationObservableEnumValue<>(source, key, enumType);
    }
}
