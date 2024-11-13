package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

class ConfigurationObservableArrayValue extends AbstractObservableValue<String[]> {

    private final ILaunchConfiguration configuration;
    private final String key;

    public ConfigurationObservableArrayValue(ILaunchConfiguration configuration, String key) {
        this.configuration = configuration;
        this.key = key;
    }

    @Override
    public Object getValueType() {
        return String[].class;
    }

    @Override
    protected String[] doGetValue() {
        try {
            List<String> list = configuration.getAttribute(key, Collections.emptyList());
            return list.toArray(new String[0]);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doSetValue(String[] value) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            launchConfigurationWorkingCopy.setAttribute(key, Arrays.asList(value));
        } else {
            throw new RuntimeException("not supported");
        }
    }
}
