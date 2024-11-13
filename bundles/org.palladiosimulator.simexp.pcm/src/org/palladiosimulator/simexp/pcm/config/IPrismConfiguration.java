package org.palladiosimulator.simexp.pcm.config;

import java.util.List;

import org.eclipse.emf.common.util.URI;

public interface IPrismConfiguration {
    List<URI> getModuleFiles();

    List<URI> getPropertyFiles();

}
