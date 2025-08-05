package org.palladiosimulator.simexp.workflow.jobs;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.workflow.api.job.IRootJob;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PcmExperienceSimulationJob implements IBlackboardInteractingJob<MDSDBlackboard>, IRootJob {

    private static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationJob.class.getName());

    private final SimulationExecutor simulationExecutor;

    private ISimulationResult simulationResult;

    public PcmExperienceSimulationJob(SimulationExecutor simulationExecutor) {
        this.simulationExecutor = simulationExecutor;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        LOGGER.info("**** PcmExperienceSimulationJob.execute ****");
        try {
            simulationResult = null;
            simulationExecutor.execute();
            simulationResult = simulationExecutor.evaluate();
            LOGGER.info("***********************************************************************");
            LOGGER.info(String.format("The %s is %s", simulationResult.getRewardDescription(),
                    simulationResult.getTotalReward()));
            List<String> detailDescription = simulationResult.getDetailDescription();
            detailDescription.stream()
                .forEach(l -> LOGGER.info(l));
            LOGGER.info("***********************************************************************");
        } finally {
            simulationExecutor.dispose();
        }
        LOGGER.info("**** PcmExperienceSimulationJob.execute - Done.****");
    }

    @Override
    public ISimulationResult getRootJobResult() {
        return simulationResult;
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
    }
}
