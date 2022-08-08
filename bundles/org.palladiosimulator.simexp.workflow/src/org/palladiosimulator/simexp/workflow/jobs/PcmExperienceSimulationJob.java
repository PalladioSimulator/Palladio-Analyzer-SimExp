package org.palladiosimulator.simexp.workflow.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutor;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PcmExperienceSimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {

    private static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationJob.class.getName());
    
    private final  LoadBalancingSimulationExecutor simulationExecutor;
    
    private MDSDBlackboard blackboard;
    
    public PcmExperienceSimulationJob( LoadBalancingSimulationExecutor simulationExecutor) {
        this.simulationExecutor = simulationExecutor;
    }
    

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        LOGGER.info("**** PcmExperienceSimulationJob.execute ****");
        
        simulationExecutor.execute();
        simulationExecutor.evaluate();
        
        LOGGER.info("**** PcmExperienceSimulationJob.execute - Done.****");
    }

    private Kmodel loadModel(URI kmodelUri) throws IOException {
        KmodelStandaloneSetup.doSetup();
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(kmodelUri, true);
        EList<EObject> contents = resource.getContents();
        Kmodel model = (Kmodel) contents.get(0);
        return model;
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        /**
         * 
         * nothing to do here
         * 
         */
    }

    @Override
    public String getName() {
        return this.getClass()
            .getCanonicalName();
    }

    @Override
    public void setBlackboard(MDSDBlackboard blackboard) {
        this.blackboard = blackboard;

    }

}
