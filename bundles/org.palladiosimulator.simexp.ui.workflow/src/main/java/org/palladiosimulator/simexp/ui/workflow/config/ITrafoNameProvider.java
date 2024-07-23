package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.List;

import org.eclipse.emf.common.util.URI;

public interface ITrafoNameProvider {
    List<String> getAvailableTransformations(URI experimentsFile);
}
