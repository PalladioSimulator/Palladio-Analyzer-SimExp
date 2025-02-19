package org.palladiosimulator.simexp.pcm.util;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.palladiosimulator.analyzer.workflow.core.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.jobs.LoadPCMModelsIntoBlackboardJob;
import org.palladiosimulator.analyzer.workflow.jobs.PreparePCMBlackboardPartitionJob;
import org.palladiosimulator.experimentautomation.application.jobs.CopyPartitionJob;
import org.palladiosimulator.experimentautomation.application.jobs.LoadModelsIntoBlackboardJob;
import org.palladiosimulator.experimentautomation.application.jobs.PrepareBlackboardJob;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simulizar.launcher.jobs.LoadSimuLizarModelsIntoBlackboardJob;

import de.uka.ipd.sdq.workflow.extension.AbstractExtendableJob;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class InitialPcmPartitionLoader {

	private static class InitialPcmPartitionLoaderJob extends AbstractExtendableJob<MDSDBlackboard> {
		
		public InitialPcmPartitionLoaderJob(Experiment experiment) {
			add(new PreparePCMBlackboardPartitionJob());
			add(new PrepareBlackboardJob());
			add(new LoadModelsIntoBlackboardJob(experiment.getInitialModel(), true));
		}
		
		public PCMResourceSetPartition loadInitialPcmPartition() {
			return (PCMResourceSetPartition) loadBlackboard().getPartition(LoadModelsIntoBlackboardJob.PCM_MODELS_ORIGINAL_PARTITION_ID);
		}
		
		public MDSDBlackboard loadInitialBlackboard() {
			add(new CopyPartitionJob(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID,
	                LoadSimuLizarModelsIntoBlackboardJob.PCM_MODELS_ANALYZED_PARTITION_ID));
			add(new CopyUriPreservingPartitionJob(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID,
	                PcmSimulatedExperienceConstants.PCM_WORKING_PARTITION));
			return loadBlackboard();
		}
		
		@SuppressWarnings("unchecked")
		private MDSDBlackboard loadBlackboard() {
			MDSDBlackboard blackboard = new MDSDBlackboard();
			this.myJobs.forEach(job -> ((IBlackboardInteractingJob<MDSDBlackboard>) job).setBlackboard(blackboard));
			this.myJobs.forEach(job -> {
				try {
					job.execute(new NullProgressMonitor());
				} catch (JobFailedException | UserCanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			return blackboard;
		}
		
	}
	
	public static MDSDBlackboard loadInitialBlackboard(Experiment experiment) {
		return new InitialPcmPartitionLoaderJob(experiment).loadInitialBlackboard();
	}
	
	public static PCMResourceSetPartition loadInitialPcmPartition(Experiment experiment) {
		return new InitialPcmPartitionLoaderJob(experiment).loadInitialPcmPartition();
	}
	
}
