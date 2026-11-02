package org.palladiosimulator.simexp.dsl.dispatcher.preferences.impl;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.palladiosimulator.simexp.dsl.dispatcher.preferences.DispatcherPreferenceConstants;

public class DispatcherPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        Preferences defaults = DefaultScope.INSTANCE.getNode(DispatcherPreferenceConstants.ID);

        defaults.put(DispatcherPreferenceConstants.DISPATCHER, DispatcherPreferenceConstants.DEFAULT_DISPATCHER);
    }
}
