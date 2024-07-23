package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;
import java.util.List;

public class TrafoNameProvider implements ITrafoNameProvider {
    // private final IQVTOModelTransformationLoader qvtoModelTransformationLoader;

    public TrafoNameProvider() {
        // this.qvtoModelTransformationLoader = qvtoModelTransformationLoader;
    }

    @Override
    public List<String> getAvailableTransformations() {
        return Arrays.asList("one", "two", "three");
    }

}
