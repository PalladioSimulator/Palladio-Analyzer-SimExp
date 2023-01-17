package org.palladiosimulator.simexp.pcm.examples.executor;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.experimentautomation.application.tooladapter.simulizar.model.SimulizartooladapterPackage;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.experimentautomation.experiments.ExperimentsPackage;

public class ExperimentRepositoryLoader {

	public ExperimentRepositoryLoader() {
		registerFactories();
	}
	
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
	
	public Experiment loadExperiment(String file) {
		ExperimentRepository loadExperimentRepository = loadExperimentRepository(file);
        EList<Experiment> experiments = loadExperimentRepository.getExperiments();
        Experiment exp = getFirstElement(experiments);
		EcoreUtil.resolveAll(exp);
		return exp;
	}
	
	private ExperimentRepository loadExperimentRepository(String file) {
		registerFactories();
		ResourceSet rs = new ResourceSetImpl();
		registerDefaultPackages(rs);
		
		URI uri = URI.createURI(file, true);
        EList<EObject> experiments = rs.getResource(uri, true).getContents();
		return (ExperimentRepository) getFirstElement(experiments);
	}
	
	private void registerFactories() {
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());       
	}
	
	private void registerDefaultPackages(ResourceSet set) {
		set.getPackageRegistry().put(ExperimentsPackage.eNS_URI, ExperimentsPackage.eINSTANCE);
		set.getPackageRegistry().put(SimulizartooladapterPackage.eNS_URI, SimulizartooladapterPackage.eINSTANCE);
	}
	
	private <T> T getFirstElement(EList<T> list) {
		if (list.isEmpty()) {
			throw new RuntimeException(String.format("The list of type %s shouldn't be empty.", list.getClass()));
		}
		return list.get(0);
	}
}
