package org.palladiosimulator.simexp.pcm.reliability.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.analyzer.workflow.core.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.jobs.LoadPCMModelsIntoBlackboardJob;
import org.palladiosimulator.simexp.pcm.util.PcmUtil;
import org.palladiosimulator.solver.core.models.PCMInstance;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class CopyPcmModelToBlackboardJob implements IJob, IBlackboardInteractingJob<MDSDBlackboard> {

	private static class CustomResourceSetPartition extends ResourceSetPartition {
		
		public void setResourceSet(ResourceSet rs) {
			this.rs = rs;
		}
		
	}
	
	private MDSDBlackboard blackboard;

	private final PCMInstance pcm;

	public CopyPcmModelToBlackboardJob(PCMInstance pcm) {
		this.pcm = pcm;
	}

	@Override
	public void setBlackboard(MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

	@Override
	public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		var copy = makeCopy();
		removeDuplicatedModels(copy);
		addToPartition(copy);
	}

	private PCMResourceSetPartition makeCopy() {
		var original = new CustomResourceSetPartition();
		original.setResourceSet(getOriginalResourceSet());
		
		return PcmUtil.copyPCMPartition(original);
	}

	private void removeDuplicatedModels(PCMResourceSetPartition copy) {
		var partition = blackboard.getPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID);
		copy.getResourceSet().getResources().removeIf(r -> partition.hasModel(r.getURI()));
	}

	private void addToPartition(PCMResourceSetPartition copy) {
		var partition = blackboard.getPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID);
		partition.getResourceSet().getResources().addAll(copy.getResourceSet().getResources());
	}

	private ResourceSet getOriginalResourceSet() {
		var anyModelResource = pcm.getAllocation().eResource();
		return anyModelResource.getResourceSet();
	}

	@Override
	public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
		// nothing to do
	}

	@Override
	public String getName() {
		return "Copy pcm model to blackboard job";
	}

}
