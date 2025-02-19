package org.palladiosimulator.simexp.pcm.reliability.job;

import org.palladiosimulator.analyzer.workflow.jobs.PreparePCMBlackboardPartitionJob;
import org.palladiosimulator.solver.core.models.PCMInstance;
import org.palladiosimulator.solver.core.runconfig.PCMSolverWorkflowRunConfiguration;

import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PrepareBlackboardForReliabilityAnalysisJob extends SequentialBlackboardInteractingJob<MDSDBlackboard>
		implements ICompositeJob {

	public PrepareBlackboardForReliabilityAnalysisJob(PCMSolverWorkflowRunConfiguration config, PCMInstance pcm) {
		super(false);

		this.myBlackboard = new MDSDBlackboard();

		this.addJob(new PreparePCMBlackboardPartitionJob());
		this.addJob(new CopyPcmModelToBlackboardJob(pcm));
	}
	
	public PrepareBlackboardForReliabilityAnalysisJob(PCMInstance pcm) {
		this(null, pcm);
	}
}
