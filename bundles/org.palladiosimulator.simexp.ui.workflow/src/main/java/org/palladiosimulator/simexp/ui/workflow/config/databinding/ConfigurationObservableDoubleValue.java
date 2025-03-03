package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import java.util.Locale;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

class ConfigurationObservableDoubleValue extends AbstractObservableValue<Double> {
    private final ILaunchConfiguration configuration;
    private final String key;
    private final boolean isPrimitive;

    public ConfigurationObservableDoubleValue(ILaunchConfiguration configuration, String key, boolean isPrimitive) {
        this.configuration = configuration;
        this.key = key;
        this.isPrimitive = isPrimitive;
    }

    @Override
    public Object getValueType() {
        return Double.class;
    }

    @Override
    protected Double doGetValue() {
        try {
            if (!configuration.hasAttribute(key)) {
                if (!isPrimitive) {
                    return null;
                }
            }
            String stringValue = configuration.getAttribute(key, "0");
            double value = Double.parseDouble(stringValue);
            return value;
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doSetValue(Double value) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            String stringValue = null;
            if (value != null) {
                stringValue = String.format(Locale.US, "%f", value);
            }
            launchConfigurationWorkingCopy.setAttribute(key, stringValue);
        } else {
            throw new RuntimeException("not supported");
        }
    }
}
