package org.palladiosimulator.simexp.workflow.jobs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.core.simulation.SimulationExecutor;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PcmExperienceSimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {

    private static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationJob.class.getName());

    private final SimulationExecutor simulationExecutor;

    private MDSDBlackboard blackboard;

    public PcmExperienceSimulationJob(SimulationExecutor simulationExecutor) {
        this.simulationExecutor = simulationExecutor;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        LOGGER.info("**** PcmExperienceSimulationJob.execute ****");

        try {
            simulationExecutor.execute();
            simulationExecutor.evaluate();
        } finally {
            simulationExecutor.dispose();
        }

        LOGGER.info("**** PcmExperienceSimulationJob.execute - Done.****");
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
