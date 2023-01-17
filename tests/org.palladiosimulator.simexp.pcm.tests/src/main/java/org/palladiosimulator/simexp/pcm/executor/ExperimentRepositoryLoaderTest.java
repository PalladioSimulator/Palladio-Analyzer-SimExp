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
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.experimentautomation.application.tooladapter.simulizar.model.SimulizartooladapterPackage;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.experimentautomation.experiments.ExperimentsPackage;
import org.palladiosimulator.experimentautomation.experiments.util.ExperimentsResourceFactoryImpl;
import org.palladiosimulator.simexp.pcm.examples.executor.ExperimentRepositoryLoader;

public class ExperimentRepositoryLoaderTest {
    
	private ExperimentRepositoryLoader experimentRepositoryLoader;

    private ResourceSet rs;
	
	@Before
    public void setUp() throws Exception {
	    registerFactories();
	    
	    rs = new ResourceSetImpl();
	    registerDefaultPackages(rs);
		this.experimentRepositoryLoader = new ExperimentRepositoryLoader();
    }
	
    private void registerFactories() {
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        reg.getExtensionToFactoryMap().put("experiments", new ExperimentsResourceFactoryImpl());
    }
    
    private void registerDefaultPackages(ResourceSet set) {
        set.getPackageRegistry().put(ExperimentsPackage.eNS_URI, ExperimentsPackage.eINSTANCE);
        set.getPackageRegistry().put(SimulizartooladapterPackage.eNS_URI, SimulizartooladapterPackage.eINSTANCE);
    }

	@Test
	public void testExistingFileLoading() throws URISyntaxException {
	    ClassLoader classLoader = getClass().getClassLoader();
	    String resourcePath = getClass().getPackageName().replace(".", "/") + "/simexp.experiments";
        URL resourceURL = classLoader.getResource(resourcePath);
        
	    ExperimentRepository experimentRepository = experimentRepositoryLoader.load(rs, URI.createURI(resourceURL.toURI().toString()));
		
		assertNotNull(experimentRepository);
	}
	
	@Test(expected = WrappedException.class)
	public void testNonExistingFileLoading() throws URISyntaxException {
        Path file = Paths.get("not_exist.experiments");
        
        experimentRepositoryLoader.load(rs, URI.createURI(file.toUri().toString()));
	}
}
