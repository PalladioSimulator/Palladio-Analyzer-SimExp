package org.palladiosimulator.simexp.workflow.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.launcher.SamplerLoggerFile;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimExpAnalyzerRootJob extends SequentialBlackboardInteractingJob<MDSDBlackboard> implements ICompositeJob {
    private final SamplerLoggerFile samplerLogger;

    public SimExpAnalyzerRootJob(SimExpWorkflowConfiguration config, SimulationExecutor simulationExecutor,
            ILaunch launch, SamplerLoggerFile samplerLogger) {
        super(SimExpAnalyzerRootJob.class.getName(), false);

        // add all contained jobs here
        this.addJob(new SimExpServiceRegistrationJob());
        this.addJob(new PcmExperienceSimulationJob(simulationExecutor));
//         this.addJob(PerformabilityWorkflowJobFactory.createPerformabilityBlackboardInitializePartitionsJob()); //new PreparePCMBlackboardPartitionJob());
        this.samplerLogger = samplerLogger;

    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        samplerLogger.close();
        super.cleanup(monitor);
    }

}
