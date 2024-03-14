package org.palladiosimulator.simexp.model.io;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SModelLoader implements ModelLoader<Smodel> {

    @Override
    public Smodel load(ResourceSet rs, URI uri) {
        SmodelStandaloneSetup.doSetup();
        Resource resource = rs.getResource(uri, true);
        // TODO: check for errors
        EList<EObject> contents = resource.getContents();
        if (contents.isEmpty()) {
            return null;
        }
        Smodel model = (Smodel) contents.get(0);
        return model;
    }

}
