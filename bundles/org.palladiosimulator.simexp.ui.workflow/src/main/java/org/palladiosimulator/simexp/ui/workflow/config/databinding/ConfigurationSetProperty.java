package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.set.SetProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ConfigurationSetProperty extends SetProperty<ILaunchConfiguration, String> {
    private final String key;

    public ConfigurationSetProperty(String key) {
        this.key = key;
    }

    @Override
    public Object getElementType() {
        return String.class;
    }

    @Override
    public IObservableSet<String> observe(Realm realm, ILaunchConfiguration source) {
        return new ConfigurationObservableSetValue(source, key);
    }

}
