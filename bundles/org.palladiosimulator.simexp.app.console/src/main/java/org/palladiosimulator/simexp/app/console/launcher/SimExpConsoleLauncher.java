package org.palladiosimulator.simexp.app.console.launcher;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
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

    @Override
    protected Appender createAppender(ILaunchConfiguration configuration) throws Exception {
        Logger rootLogger = Logger.getRootLogger();
        @SuppressWarnings("unchecked")
        Enumeration<Appender> allAppenders = rootLogger.getAllAppenders();
        Appender nextElement = allAppenders.nextElement();
        return nextElement;
    }
}
