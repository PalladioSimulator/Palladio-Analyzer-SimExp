package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ConfigurationProperties {
    public static IValueProperty<ILaunchConfiguration, String> string(String key) {
        return new ConfigurationStringProperty(key);
    }

    public static IValueProperty<ILaunchConfiguration, Integer> integer(String key) {
        return new ConfigurationIntegerProperty(key);
    }
}
