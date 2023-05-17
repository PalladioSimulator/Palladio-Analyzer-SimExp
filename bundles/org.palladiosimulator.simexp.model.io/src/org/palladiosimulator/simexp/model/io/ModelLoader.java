package org.palladiosimulator.simexp.model.io;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

public interface ModelLoader<T> {
    T load(ResourceSet rs, URI uri);
}
