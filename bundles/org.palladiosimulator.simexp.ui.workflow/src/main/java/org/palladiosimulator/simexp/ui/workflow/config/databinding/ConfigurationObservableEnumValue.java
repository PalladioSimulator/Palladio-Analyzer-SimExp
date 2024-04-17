package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

class ConfigurationObservableEnumValue<E extends Enum<E>> extends AbstractObservableValue<E> {

    private final ILaunchConfiguration configuration;
    private final String key;
    private final Class<E> enumType;

    public ConfigurationObservableEnumValue(ILaunchConfiguration configuration, String key, Class<E> enumType) {
        this.configuration = configuration;
        this.key = key;
        this.enumType = enumType;
    }

    @Override
    public Object getValueType() {
        return Integer.class;
    }

    @Override
    protected E doGetValue() {
        try {
            String enumLiteral = configuration.getAttribute(key, "");
            E enumValue = Enum.valueOf(enumType, enumLiteral);
            return enumValue;
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doSetValue(E value) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            launchConfigurationWorkingCopy.setAttribute(key, value.name());
        } else {
            throw new RuntimeException("not supported");
        }
    }
}
