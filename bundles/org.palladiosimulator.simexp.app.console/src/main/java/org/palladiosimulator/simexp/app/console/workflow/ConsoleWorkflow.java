package org.palladiosimulator.simexp.app.console.workflow;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.workflow.api.job.IRootJob;

import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.WorkflowExceptionHandler;
import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class ConsoleWorkflow extends BlackboardBasedWorkflow<MDSDBlackboard> {
    private final IJob mainJob;

    public ConsoleWorkflow(IJob job, IProgressMonitor monitor, WorkflowExceptionHandler handler,
            MDSDBlackboard blackboard) {
        super(job, monitor, handler, blackboard);
        this.mainJob = job;

    }

    @Override
    public void run() {
        super.run();
        IRootJob rootJob = findRootJob(mainJob);
        ISimulationResult simulationResult = rootJob.getRootJobResult();
    }

    private IRootJob findRootJob(IJob job) {
        if (job instanceof IRootJob rootJob) {
            return rootJob;
        }
        if (job instanceof ICompositeJob compositeJob) {
            for (int i = 0; i < compositeJob.size(); ++i) {
                IJob subJob = compositeJob.get(i);
                IRootJob rootJob = findRootJob(subJob);
                if (rootJob != null) {
                    return rootJob;
                }
            }
        }
        return null;
    }
}
