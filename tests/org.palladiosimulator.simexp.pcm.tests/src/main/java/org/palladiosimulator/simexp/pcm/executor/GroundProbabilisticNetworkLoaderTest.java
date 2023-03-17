package org.palladiosimulator.simexp.pcm.executor;

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
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.StaticmodelPackage;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;
import org.palladiosimulator.simexp.pcm.examples.executor.GroundProbabilisticNetworkLoader;

public class GroundProbabilisticNetworkLoaderTest {
	
	private GroundProbabilisticNetworkLoader gpnLoader;
	private ResourceSet rs;
	
	@Before
    public void setUp() throws Exception {
		registerFactories();
		
	    rs = new ResourceSetImpl();
	    registerDefaultPackages(rs);
		this.gpnLoader = new GroundProbabilisticNetworkLoader();
    }
	
	private void registerFactories() {
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        reg.getExtensionToFactoryMap().put("staticmodel", new XMIResourceFactoryImpl());
	}
	
	private void registerDefaultPackages(ResourceSet set) {
    	set.getPackageRegistry().put(StaticmodelPackage.eNS_URI, StaticmodelPackage.eINSTANCE);
		set.getPackageRegistry().put(TemplatevariablePackage.eNS_URI, TemplatevariablePackage.eINSTANCE);
    }
	
	@Test
	public void testExistingFileLoading() throws URISyntaxException {
	    ClassLoader classLoader = getClass().getClassLoader();
	    String resourcePath = getClass().getPackageName().replace(".", "/") + "/environmentalStatics.staticmodel";
        URL resourceURL = classLoader.getResource(resourcePath);
        
	    GroundProbabilisticNetwork experimentRepository = gpnLoader.load(rs, URI.createURI(resourceURL.toURI().toString()));
		
		assertNotNull(experimentRepository);
	}
	
	@Test(expected = WrappedException.class)
	public void testNonExistingFileLoading() throws URISyntaxException {
        Path file = Paths.get("not_exist.staticmodel");
        
        gpnLoader.load(rs, URI.createURI(file.toUri().toString()));
	}
}
