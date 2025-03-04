package org.palladiosimulator.simexp.dsl.ea.launch.dispatcher;

import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDispatcherProvider;

public class LocalDispatcherProvider implements IDispatcherProvider {

    @Override
    public String getName() {
        return "Local";
    }

}
