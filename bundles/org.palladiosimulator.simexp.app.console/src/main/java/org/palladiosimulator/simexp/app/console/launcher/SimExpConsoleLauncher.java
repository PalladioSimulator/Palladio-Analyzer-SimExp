package org.palladiosimulator.simexp.app.console.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.simexp.workflow.api.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.launcher.SimExpLauncher;

import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.WorkflowExceptionHandler;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimExpConsoleLauncher extends SimExpLauncher {

    @Override
    protected BlackboardBasedWorkflow<MDSDBlackboard> createWorkflow(SimExpWorkflowConfiguration workflowConfiguration,
            IProgressMonitor monitor, ILaunch launch) throws CoreException {
        IJob workflowJob = createWorkflowJob(workflowConfiguration, launch);
        WorkflowExceptionHandler exceptionHandler = createExceptionHandler(workflowConfiguration.isInteractive());
        MDSDBlackboard blackboard = createBlackboard();
        return new BlackboardBasedWorkflow<>(workflowJob, monitor, exceptionHandler, blackboard);
    }

    @Override
    protected WorkflowExceptionHandler createExceptionHandler(boolean interactive) {
        return new WorkflowExceptionHandler(true);
    }
}
