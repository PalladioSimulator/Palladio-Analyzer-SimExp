package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class DynamicBayesiannetworkPersistence implements IDynamicBayesianNetworkPersistence {
    private final IDynamicBayesianNetworkResourceSetManager resourceSetManager;

    public DynamicBayesiannetworkPersistence(IDynamicBayesianNetworkResourceSetManager resourceSetManager) {
        this.resourceSetManager = resourceSetManager;
    }

    @Override
    public void persist(EObject eObj, String path) {
        ResourceSet resourceSet = resourceSetManager.getResourceSet();
        Resource resource = resourceSet.createResource(URI.createFileURI(path));
        resource.getContents().add(eObj);
        try {
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
