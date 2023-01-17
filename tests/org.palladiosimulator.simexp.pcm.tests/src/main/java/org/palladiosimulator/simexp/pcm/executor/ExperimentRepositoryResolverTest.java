package org.palladiosimulator.simexp.pcm.executor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.experimentautomation.experiments.ExperimentsFactory;
import org.palladiosimulator.simexp.pcm.examples.executor.ExperimentRepositoryResolver;

public class ExperimentRepositoryResolverTest {


    private ExperimentRepositoryResolver resolver;

    @Before
    public void setUp() throws Exception {
        resolver = new ExperimentRepositoryResolver();
    }

    @Test
    public void testResolveExperimentRepository() {
        ExperimentRepository experimentRepository = ExperimentsFactory.eINSTANCE.createExperimentRepository();
        Experiment expectedExperiment = ExperimentsFactory.eINSTANCE.createExperiment();
        experimentRepository.getExperiments().add(expectedExperiment);

        Experiment actualExperiment = resolver.resolveExperiment(experimentRepository);
        
        assertEquals(expectedExperiment, actualExperiment);
    }
    
     @Test
    public void testUnableToResolveExperimentRepository() throws Exception {
         ExperimentRepository experimentRepository = ExperimentsFactory.eINSTANCE.createExperimentRepository();
         
         Experiment actualExperiment = resolver.resolveExperiment(experimentRepository);
         
         assertNull(actualExperiment);
    }

}
