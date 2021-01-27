package org.palladiosimulator.simexp.workflow.jobs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceCache;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PcmExperienceSimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {
    
    private static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationJob.class.getName());
    
    private MDSDBlackboard blackboard;
    

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        LOGGER.info("**** PcmExperienceSimulationJob.execute ****");
        
        PcmExperienceSimulationExecutor.get().execute();
        PcmExperienceSimulationExecutor.get().evaluate();
        
        LOGGER.info("**** PcmExperienceSimulationJob.execute - Done.****");
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        /**
         * 
         * nothing to do here
         * 
         * */        
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public void setBlackboard(MDSDBlackboard blackboard) {
        this.blackboard = blackboard;
        
    }

}
