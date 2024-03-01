package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class ConfigurationObservableIntegerValue extends AbstractObservableValue<Integer> {

    private final ILaunchConfiguration configuration;
    private final String key;

    public ConfigurationObservableIntegerValue(ILaunchConfiguration configuration, String key) {
        this.configuration = configuration;
        this.key = key;
    }

    @Override
    public Object getValueType() {
        return Integer.class;
    }

    @Override
    protected Integer doGetValue() {
        try {
            return configuration.getAttribute(key, 0);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doSetValue(Integer value) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            launchConfigurationWorkingCopy.setAttribute(key, value);
        } else {
            throw new RuntimeException("not supported");
        }
    }
}
