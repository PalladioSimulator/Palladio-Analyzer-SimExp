package org.palladiosimulator.simexp.ui.workflow.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.palladiosimulator.simexp.workflow.preferences.KubernetesSettings;

public class KubernetesPreferenceInitializer extends AbstractPreferenceInitializer {

    public KubernetesPreferenceInitializer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initializeDefaultPreferences() {
        // TODO Auto-generated method stub
        IEclipsePreferences preferences = DefaultScope.INSTANCE.getNode(KubernetesSettings.KUBERNETES_PREFERENCES);
        preferences.put(KubernetesSettings.CLUSTER_URL, "http://something");

    }

}
