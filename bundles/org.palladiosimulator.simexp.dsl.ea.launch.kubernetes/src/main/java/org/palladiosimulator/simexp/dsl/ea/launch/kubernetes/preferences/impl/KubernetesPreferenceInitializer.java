package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.impl;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.KubernetesPreferenceConstants;

public class KubernetesPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        Preferences defaults = DefaultScope.INSTANCE.getNode(KubernetesPreferenceConstants.ID);

        defaults.put(KubernetesPreferenceConstants.CLUSTER_URL, "http://something");
    }

}
