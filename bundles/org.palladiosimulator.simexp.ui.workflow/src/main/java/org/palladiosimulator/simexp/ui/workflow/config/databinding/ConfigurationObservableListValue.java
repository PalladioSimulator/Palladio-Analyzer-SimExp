package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

class ConfigurationObservableListValue extends AbstractObservableList<String> {

    private final ILaunchConfiguration configuration;
    private final String key;

    public ConfigurationObservableListValue(ILaunchConfiguration configuration, String key) {
        this.configuration = configuration;
        this.key = key;
    }

    @Override
    public Object getElementType() {
        return String.class;
    }

    private List<String> getConfigList() {
        try {
            List<String> list = configuration.getAttribute(key, Collections.emptyList());
            return list;
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getReadOnlyList() {
        return Collections.unmodifiableList(getConfigList());
    }

    private List<String> getModifyableList() {
        return new ArrayList<>(getConfigList());
    }

    @Override
    public String get(int index) {
        List<String> list = getReadOnlyList();
        return list.get(index);
    }

    @Override
    protected int doGetSize() {
        List<String> list = getReadOnlyList();
        return list.size();
    }

    @Override
    public String set(int index, String element) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            List<String> list = getModifyableList();
            String previous = list.set(index, element);
            launchConfigurationWorkingCopy.setAttribute(key, list);
            return previous;
        } else {
            throw new RuntimeException("not supported");
        }
    }

    @Override
    public void add(int index, String element) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            List<String> list = getModifyableList();
            list.add(index, element);
            launchConfigurationWorkingCopy.setAttribute(key, list);
        } else {
            throw new RuntimeException("not supported");
        }
    }

    @Override
    public String remove(int index) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
            List<String> list = getModifyableList();
            String previous = list.remove(index);
            launchConfigurationWorkingCopy.setAttribute(key, list);
            return previous;
        } else {
            throw new RuntimeException("not supported");
        }
    }
}
