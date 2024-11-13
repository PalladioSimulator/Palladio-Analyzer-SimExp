package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

class ConfigurationObservableSetValue extends AbstractObservableSet<String> {

    private final ILaunchConfiguration configuration;
    private final String key;

    public ConfigurationObservableSetValue(ILaunchConfiguration configuration, String key) {
        this.configuration = configuration;
        this.key = key;
    }

    @Override
    public Object getElementType() {
        return String.class;
    }

    private Set<String> getConfigSet() {
        try {
            Set<String> set = configuration.getAttribute(key, Collections.emptySet());
            return set;
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> getReadOnlySet() {
        return Collections.unmodifiableSet(getConfigSet());
    }

    private Set<String> getModifyableSet() {
        return new HashSet<>(getConfigSet());
    }

    @Override
    protected Set<String> getWrappedSet() {
        return getReadOnlySet();
    }

    @Override
    public boolean add(String element) {
        if (!(configuration instanceof ILaunchConfigurationWorkingCopy)) {
            throw new RuntimeException("not supported");
        }
        ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
        Set<String> set = getModifyableSet();
        boolean added = set.add(element);
        launchConfigurationWorkingCopy.setAttribute(key, set);
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        if (!(configuration instanceof ILaunchConfigurationWorkingCopy)) {
            throw new RuntimeException("not supported");
        }
        ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
        Set<String> set = getModifyableSet();
        boolean added = set.addAll(c);
        launchConfigurationWorkingCopy.setAttribute(key, set);
        return added;
    }

    @Override
    public boolean remove(Object element) {
        if (!(configuration instanceof ILaunchConfigurationWorkingCopy)) {
            throw new RuntimeException("not supported");
        }
        ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
        Set<String> set = getModifyableSet();
        boolean removed = set.remove(element);
        launchConfigurationWorkingCopy.setAttribute(key, set);
        return removed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (!(configuration instanceof ILaunchConfigurationWorkingCopy)) {
            throw new RuntimeException("not supported");
        }
        ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
        Set<String> set = getModifyableSet();
        boolean removed = set.removeAll(c);
        launchConfigurationWorkingCopy.setAttribute(key, set);
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (!(configuration instanceof ILaunchConfigurationWorkingCopy)) {
            throw new RuntimeException("not supported");
        }
        ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
        Set<String> set = getModifyableSet();
        boolean retained = set.retainAll(c);
        launchConfigurationWorkingCopy.setAttribute(key, set);
        return retained;
    }

    @Override
    public void clear() {
        if (!(configuration instanceof ILaunchConfigurationWorkingCopy)) {
            throw new RuntimeException("not supported");
        }
        ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
        launchConfigurationWorkingCopy.setAttribute(key, Collections.emptySet());
    }
}
