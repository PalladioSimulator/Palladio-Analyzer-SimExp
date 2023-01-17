package org.palladiosimulator.simexp.pcm.examples.executor;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;

public class ExperimentRepositoryLoader {

	
	/**
	 * precondition:
	 * - resource set empty
	 * 
	 * postcondition:
	 * - resource set 1 resource containing returned ExperimentRepository 
	 * 
	 */
	public ExperimentRepository load(ResourceSet rs, URI uri) {
	    Resource resource = rs.getResource(uri, true);
	    EList<EObject> experiments = resource.getContents();
	    if (experiments.isEmpty())  {
	        return null;
	    }
	    return (ExperimentRepository) experiments.get(0);
	}
	
	
}
