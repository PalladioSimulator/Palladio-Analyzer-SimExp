package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.list.ListProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

class ConfigurationListProperty extends ListProperty<ILaunchConfiguration, String> {
    private final String key;

    public ConfigurationListProperty(String key) {
        this.key = key;
    }

    @Override
    public Object getElementType() {
        return String.class;
    }

    @Override
    public IObservableList<String> observe(Realm realm, ILaunchConfiguration source) {
        return new ConfigurationObservableListValue(source, key);
    }
}
