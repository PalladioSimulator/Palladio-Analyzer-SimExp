package org.palladiosimulator.simexp.dsl.ea.api.preferences.impl;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.palladiosimulator.simexp.dsl.ea.api.preferences.EADispatcherType;
import org.palladiosimulator.simexp.dsl.ea.api.preferences.EAPreferenceConstants;

public class EAPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        Preferences defaults = DefaultScope.INSTANCE.getNode(EAPreferenceConstants.ID);

        defaults.put(EAPreferenceConstants.DISPATCHER, EADispatcherType.LOCAl.name());
    }
}
