package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ConfigurationProperties {
    public static IValueProperty<ILaunchConfiguration, String> string(String key) {
        return new ConfigurationStringProperty(key);
    }

    public static IValueProperty<ILaunchConfiguration, String[]> strings(String key) {
        return new ConfigurationStringsProperty(key);
    }

    public static IValueProperty<ILaunchConfiguration, Integer> integer(String key) {
        return integer(key, true);
    }

    public static IValueProperty<ILaunchConfiguration, Integer> integer(String key, boolean isPrimitive) {
        return new ConfigurationIntegerProperty(key, isPrimitive);
    }

    public static IValueProperty<ILaunchConfiguration, Double> value(String key) {
        return value(key, true);
    }

    public static IValueProperty<ILaunchConfiguration, Double> value(String key, boolean isPrimitive) {
        return new ConfigurationDoubleProperty(key, isPrimitive);
    }

    public static <E extends Enum<E>> IValueProperty<ILaunchConfiguration, E> enummeration(String key,
            Class<E> enumType) {
        return new ConfigurationEnumProperty<>(key, enumType);
    }

    public static IListProperty<ILaunchConfiguration, String> list(String key) {
        return new ConfigurationListProperty(key);
    }

    public static ISetProperty<ILaunchConfiguration, String> set(String key) {
        return new ConfigurationSetProperty(key);
    }
}
