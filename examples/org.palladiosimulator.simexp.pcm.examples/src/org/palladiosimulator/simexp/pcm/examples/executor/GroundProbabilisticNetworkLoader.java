package org.palladiosimulator.simexp.pcm.examples.executor;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;

public class GroundProbabilisticNetworkLoader {
	
	public GroundProbabilisticNetwork load(ResourceSet rs, URI uri) {
		Resource resource = rs.getResource(uri, true);
		EList<EObject> contents = resource.getContents();
	    if (contents.isEmpty())  {
	        return null;
	    }
	    return (GroundProbabilisticNetwork) contents.get(0);
	}
}
