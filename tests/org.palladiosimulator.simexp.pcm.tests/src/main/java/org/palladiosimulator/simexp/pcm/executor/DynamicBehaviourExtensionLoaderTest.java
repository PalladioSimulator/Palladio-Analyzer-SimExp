package org.palladiosimulator.simexp.pcm.executor;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicmodelPackage;
import org.palladiosimulator.envdyn.environment.dynamicmodel.util.DynamicmodelAdapterFactory;
import org.palladiosimulator.simexp.pcm.examples.executor.DynamicBehaviourExtensionLoader;

public class DynamicBehaviourExtensionLoaderTest {
	
	private DynamicBehaviourExtensionLoader dbeLoader;
	private ResourceSet rs;
	
	@Before
    public void setUp() throws Exception {
	    rs = new ResourceSetImpl();
	    register(rs);
		this.dbeLoader = new DynamicBehaviourExtensionLoader();
    }
	
	private void register(ResourceSet set) {
    	set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("dynamicmodel", new DynamicmodelAdapterFactory());
    	set.getPackageRegistry().put(DynamicmodelPackage.eNS_URI, DynamicmodelPackage.eINSTANCE);
    }
	
	@Test
	public void testExistingFileLoading() throws URISyntaxException {
	    ClassLoader classLoader = getClass().getClassLoader();
	    String resourcePath = getClass().getPackageName().replace(".", "/") + "/environmentalDynamics.dynamicmodel";
        URL resourceURL = classLoader.getResource(resourcePath);
        
	    DynamicBehaviourExtension experimentRepository = dbeLoader.load(rs, URI.createURI(resourceURL.toURI().toString()));
		
		assertNotNull(experimentRepository);
	}
	
	@Test(expected = WrappedException.class)
	public void testNonExistingFileLoading() throws URISyntaxException {
        Path file = Paths.get("not_exist.dynamicmodel");
        
        dbeLoader.load(rs, URI.createURI(file.toUri().toString()));
	}
}