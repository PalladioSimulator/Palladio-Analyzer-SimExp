package org.palladiosimulator.simexp.model.io;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.simexp.dsl.kmodel.KmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;

public class KModelLoader implements ModelLoader<Kmodel> {

	@Override
	public Kmodel load(ResourceSet rs, URI uri) {
		KmodelStandaloneSetup.doSetup();
		Resource resource = rs.getResource(uri, true);
		EList<EObject> contents = resource.getContents();
		if (contents.isEmpty()) {
			return null;
		}
		Kmodel model = (Kmodel) contents.get(0);
		return model;
	}

}
