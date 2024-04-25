package org.palladiosimulator.simexp.pcm.config;

import java.util.List;

import org.eclipse.emf.common.util.URI;

public interface IPrismWorkflowConfiguration extends IWorkflowConfiguration {
    List<URI> getModuleFiles();

    List<URI> getPropertyFiles();

}
