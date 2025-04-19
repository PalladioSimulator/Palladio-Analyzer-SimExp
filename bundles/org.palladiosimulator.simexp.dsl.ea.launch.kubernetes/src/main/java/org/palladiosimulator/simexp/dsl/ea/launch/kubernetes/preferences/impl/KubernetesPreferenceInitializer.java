package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.impl;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.KubernetesPreferenceConstants;

public class KubernetesPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        Preferences defaults = DefaultScope.INSTANCE.getNode(KubernetesPreferenceConstants.ID);

        defaults.put(KubernetesPreferenceConstants.CLUSTER_URL, "https://10.0.0.10:6443");

        defaults.put(KubernetesPreferenceConstants.RABBIT_MQ_URL, "https://10.0.0.10:32001");
        defaults.put(KubernetesPreferenceConstants.INTERNAL_RABBIT_MQ_URL, "https://10.0.0.10:32001");
        defaults.put(KubernetesPreferenceConstants.INTERNAL_IMAGE_REGISTRY_URL, "http://10.0.0.10:30500");

        defaults.put(KubernetesPreferenceConstants.RABBIT_QUEUE_OUT, "simexp-queue-task");
        defaults.put(KubernetesPreferenceConstants.RABBIT_QUEUE_IN, "simexp-queue-answer");
        defaults.putInt(KubernetesPreferenceConstants.RABBIT_CONSUMER_TIMEOUT, 14);
    }
}
