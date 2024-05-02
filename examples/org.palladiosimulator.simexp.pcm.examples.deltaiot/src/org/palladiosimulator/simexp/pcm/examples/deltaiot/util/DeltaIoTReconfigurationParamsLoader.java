package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage;

public class DeltaIoTReconfigurationParamsLoader {

	public DeltaIoTReconfigurationParamsLoader() {
		registerFactories();
	}

	public DeltaIoTReconfigurationParamRepository load(String file) {
		DeltaIoTReconfigurationParamRepository repo = loadRepo(file);
		EcoreUtil.resolveAll(repo);
		return repo;
	}

	private DeltaIoTReconfigurationParamRepository loadRepo(String file) {
		registerFactories();
		ResourceSet rs = new ResourceSetImpl();
		registerDefaultPackages(rs);
		EList<EObject> result = rs.getResource(URI.createPlatformResourceURI(file, true), true).getContents();
		return (DeltaIoTReconfigurationParamRepository) getFirstElement(result);
	}

	private void registerFactories() {
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	}

	private void registerDefaultPackages(ResourceSet set) {
		set.getPackageRegistry().put(ReconfigurationparamsPackage.eNS_URI, ReconfigurationparamsPackage.eINSTANCE);
	}

	private <T> T getFirstElement(EList<T> list) {
		if (list.isEmpty()) {
			throw new RuntimeException(String.format("The list of type %s shouldn't be empty.", list.getClass()));
		}
		return list.get(0);
	}
}
