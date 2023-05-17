package org.palladiosimulator.simexp.model.io;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;

public class DynamicBehaviourExtensionLoader implements ModelLoader<DynamicBehaviourExtension> {
	
	@Override
	public DynamicBehaviourExtension load(ResourceSet rs, URI uri) {
		Resource resource = rs.getResource(uri, true);
		EList<EObject> contents = resource.getContents();
	    if (contents.isEmpty())  {
	        return null;
	    }
	    return (DynamicBehaviourExtension) contents.get(0);
	}
}
