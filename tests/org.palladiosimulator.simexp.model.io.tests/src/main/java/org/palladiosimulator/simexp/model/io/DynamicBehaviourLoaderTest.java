package org.palladiosimulator.simexp.model.io;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicmodelPackage;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;

public class DynamicBehaviourLoaderTest {
	
	private DynamicBehaviourLoader dbeLoader;
	private ResourceSet rs;
	
	@Before
    public void setUp() throws Exception {
		registerFactories();
		
	    rs = new ResourceSetImpl();
	    registerDefaultPackages(rs);
		this.dbeLoader = new DynamicBehaviourLoader();
    }
	
	private void registerFactories() {
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        reg.getExtensionToFactoryMap().put("dynamicmodel", new XMIResourceFactoryImpl());
	}
	
	private void registerDefaultPackages(ResourceSet set) {
		set.getPackageRegistry().put(DynamicmodelPackage.eNS_URI, DynamicmodelPackage.eINSTANCE);
		set.getPackageRegistry().put(TemplatevariablePackage.eNS_URI, TemplatevariablePackage.eINSTANCE);
    }
	
	
	@Test
	public void testExistingFileLoading() throws URISyntaxException {
	    ClassLoader classLoader = getClass().getClassLoader();
	    String resourcePath = getClass().getPackageName().replace(".", "/") + "/environmentalDynamics.dynamicmodel";
        URL resourceURL = classLoader.getResource(resourcePath);
        
	    DynamicBehaviourRepository experimentRepository = dbeLoader.load(rs, URI.createURI(resourceURL.toURI().toString()));
		
		assertNotNull(experimentRepository);
	}
	
	@Test(expected = WrappedException.class)
	public void testNonExistingFileLoading() throws URISyntaxException {
        Path file = Paths.get("not_exist.dynamicmodel");
        
        dbeLoader.load(rs, URI.createURI(file.toUri().toString()));
	}
}
