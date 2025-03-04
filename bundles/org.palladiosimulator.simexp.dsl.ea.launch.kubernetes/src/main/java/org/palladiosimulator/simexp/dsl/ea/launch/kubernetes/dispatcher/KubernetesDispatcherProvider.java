package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDispatcherProvider;

public class KubernetesDispatcherProvider implements IDispatcherProvider {

    @Override
    public String getName() {
        return "Kubernetes";
    }

}
