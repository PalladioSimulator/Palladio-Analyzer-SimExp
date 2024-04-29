package org.palladiosimulator.simexp.pcm.modelled;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;

public interface ModelledModelLoader extends ModelLoader {
    Smodel loadSModel(ResourceSet rs, URI smodelURI);
}
