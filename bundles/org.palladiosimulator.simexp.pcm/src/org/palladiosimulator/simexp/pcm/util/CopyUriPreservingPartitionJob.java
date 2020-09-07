package org.palladiosimulator.simexp.pcm.util;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.experimentautomation.application.jobs.CopyPartitionJob;

import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class CopyUriPreservingPartitionJob extends SequentialBlackboardInteractingJob<MDSDBlackboard> {

    private static final Logger LOGGER = Logger.getLogger(CopyPartitionJob.class);

    private final String sourcePartition;
    private final String targetPartition;

    public CopyUriPreservingPartitionJob(final String sourcePartition, final String targetPartition) {
        super(false);

        this.sourcePartition = sourcePartition;
        this.targetPartition = targetPartition;
    }

    @Override
    public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        LOGGER.info("Cloning " + this.sourcePartition + " to " + this.targetPartition);
        this.getBlackboard().removePartition(this.targetPartition);

        ResourceSetPartition orginal = this.getBlackboard().getPartition(this.sourcePartition);
        PCMResourceSetPartition copy = PcmUtil.copyPCMPartition(orginal);

        this.getBlackboard().addPartition(this.targetPartition, copy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Clone Partition Contents";
    }
}
