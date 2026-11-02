package org.palladiosimulator.simexp.dsl.dispatcher.preferences.impl;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.palladiosimulator.simexp.dsl.dispatcher.preferences.EAPreferenceConstants;

public class DispatcherPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        Preferences defaults = DefaultScope.INSTANCE.getNode(EAPreferenceConstants.ID);

        defaults.put(EAPreferenceConstants.DISPATCHER, EAPreferenceConstants.DEFAULT_DISPATCHER);
    }
}
