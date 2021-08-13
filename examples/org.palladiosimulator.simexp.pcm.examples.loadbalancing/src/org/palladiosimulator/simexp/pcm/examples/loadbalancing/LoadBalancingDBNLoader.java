package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;

public enum LoadBalancingDBNLoader implements IDynamicBayesianNetworkResourceSetManager {
    INSTANCE;

	private final ResourceSet resourceSet = new ResourceSetImpl();
	
	private LoadBalancingDBNLoader() {
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(TemplatevariablePackage.eNS_URI, TemplatevariablePackage.eINSTANCE);
	}


	@Override
    public ResourceSet getResourceSet() {
        return resourceSet;
    }
}
