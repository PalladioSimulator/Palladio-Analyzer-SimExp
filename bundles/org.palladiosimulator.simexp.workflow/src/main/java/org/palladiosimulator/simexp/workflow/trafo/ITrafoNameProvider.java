package org.palladiosimulator.simexp.workflow.trafo;

import java.util.List;

import org.eclipse.emf.common.util.URI;

public interface ITrafoNameProvider {
    List<String> getAvailableTransformations(URI experimentsFile);
}
