package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;

public enum DynamicBayesianNetworkResourceSetManager implements IDynamicBayesianNetworkResourceSetManager {
    INSTANCE;

	private final ResourceSet resourceSet = new ResourceSetImpl();
	
	private DynamicBayesianNetworkResourceSetManager() {
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(TemplatevariablePackage.eNS_URI, TemplatevariablePackage.eINSTANCE);
	}


	@Override
    public ResourceSet getResourceSet() {
        return resourceSet;
    }
}
