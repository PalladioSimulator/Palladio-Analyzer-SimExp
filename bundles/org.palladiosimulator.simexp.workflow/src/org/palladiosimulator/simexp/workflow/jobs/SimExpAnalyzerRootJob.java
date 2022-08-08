package org.palladiosimulator.simexp.workflow.jobs;

import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutor;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;

import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimExpAnalyzerRootJob extends SequentialBlackboardInteractingJob<MDSDBlackboard> implements ICompositeJob {
    
    public SimExpAnalyzerRootJob(SimExpWorkflowConfiguration config, LoadBalancingSimulationExecutor simulationExecutor, ILaunch launch) {
        super(SimExpAnalyzerRootJob.class.getName(), false);
        
        // add all contained jobs here
        this.addJob(new SimExpServiceRegistrationJob());
        this.addJob(new PcmExperienceSimulationJob(simulationExecutor));
//         this.addJob(PerformabilityWorkflowJobFactory.createPerformabilityBlackboardInitializePartitionsJob()); //new PreparePCMBlackboardPartitionJob());
         
    }

}
