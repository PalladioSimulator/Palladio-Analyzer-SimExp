package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

class ConfigurationObservableDoubleValue extends AbstractObservableValue<Double> {
    private static final Locale FORMAT_LOCALE = Locale.US;

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
            NumberFormat numberFormat = DecimalFormat.getInstance(FORMAT_LOCALE);
            Number value = numberFormat.parse(stringValue);
            return value.doubleValue();
        } catch (CoreException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doSetValue(Double value) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            String stringValue = null;
            if (value != null) {
                NumberFormat numberFormat = DecimalFormat.getInstance(FORMAT_LOCALE);
                stringValue = numberFormat.format(value);
            }
            launchConfigurationWorkingCopy.setAttribute(key, stringValue);
        } else {
            throw new RuntimeException("not supported");
        }
    }
}
