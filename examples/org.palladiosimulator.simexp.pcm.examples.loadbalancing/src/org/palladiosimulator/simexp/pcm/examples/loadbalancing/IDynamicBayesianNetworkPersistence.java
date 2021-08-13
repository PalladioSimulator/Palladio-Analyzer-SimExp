package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.ecore.EObject;

public interface IDynamicBayesianNetworkPersistence {

    void persist(EObject eObj, String path);
    
}
