package org.palladiosimulator.simexp.pcm.executor;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.pcm.examples.executor.ExperimentLoader;

public class ExperimentLoaderTest {
	private ExperimentLoader experimentLoader;
	
	@BeforeEach
    public void setUp() throws Exception {
		this.experimentLoader = new ExperimentLoader();
    }

	@Test
	public void testExistingFileLoading() {
		Experiment experiment = experimentLoader.loadExperiment("platform:/resource/org.palladiosimulator.simexp.pcm.examples.loadbalancer.faulttolerant/experiments/simexp.experiments");
		
		Assertions.assertNotNull(experiment);
	}
	
	@Test
	public void testNonExistingFileLoading() {
		Exception e = Assertions.assertThrows(FileNotFoundException.class, 
				() -> experimentLoader.loadExperiment("some.experiments"));
		
		Assertions.assertEquals(e.getLocalizedMessage(), "some.experiments (The system cannot find the file specified)");
	}
}
