package org.palladiosimulator.simexp.pcm.reliability.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.analyzer.workflow.jobs.LoadPCMModelsIntoBlackboardJob;
import org.palladiosimulator.commons.emfutils.EMFCopyHelper;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Lists;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class CopyPcmModelToBlackboardJob implements IJob, IBlackboardInteractingJob<MDSDBlackboard> {

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
		var partition = blackboard.getPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID);

		var allocResourceCopy = copyAllocationModel(partition.getResourceSet());
		var usageResourceCopy = copyUsageModel(partition.getResourceSet());
		partition.getResourceSet().getResources().addAll(Lists.newArrayList(allocResourceCopy, usageResourceCopy));

		partition.resolveAllProxies();
	}

	private Resource copyAllocationModel(ResourceSet rs) {
		return copy(pcm.getAllocation().eResource(), rs);
	}

	private Resource copyUsageModel(ResourceSet rs) {
		return copy(pcm.getUsageModel().eResource(), rs);
	}

	private Resource copy(Resource resourceToCopy, ResourceSet rs) {
		var copiedObj = EMFCopyHelper.deepCopyEObjectList(resourceToCopy.getContents());
		var copy = rs.createResource(resourceToCopy.getURI());
		copy.getContents().add(copiedObj.get(0));
		return copy;
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
