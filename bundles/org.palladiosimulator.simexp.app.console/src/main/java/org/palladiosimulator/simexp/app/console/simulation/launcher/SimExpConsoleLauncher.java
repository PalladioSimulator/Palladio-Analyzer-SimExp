package org.palladiosimulator.simexp.app.console.simulation.launcher;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.palladiosimulator.simexp.app.console.simulation.workflow.ConsoleWorkflow;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.workflow.api.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.launcher.SimExpLauncher;

import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.WorkflowExceptionHandler;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimExpConsoleLauncher extends SimExpLauncher
        implements ILaunchConfigurationDelegate2, ISimulationResultProvider {
    private ConsoleWorkflow consoleWorkflow;

    @Override
    protected BlackboardBasedWorkflow<MDSDBlackboard> createWorkflow(SimExpWorkflowConfiguration workflowConfiguration,
            IProgressMonitor monitor, ILaunch launch) throws CoreException {
        IJob workflowJob = createWorkflowJob(workflowConfiguration, launch);
        WorkflowExceptionHandler exceptionHandler = createExceptionHandler(workflowConfiguration.isInteractive());
        MDSDBlackboard blackboard = createBlackboard();
        consoleWorkflow = new ConsoleWorkflow(workflowJob, monitor, exceptionHandler, blackboard);
        return consoleWorkflow;
    }

    @Override
    public ISimulationResult getSimulationResult() {
        return consoleWorkflow.getSimulationResult();
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

    @Override
    public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
        return new SimulationLaunch(configuration, mode, null, this);
    }

    @Override
    public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
            throws CoreException {
        return false;
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
            throws CoreException {
        return true;
    }

    @Override
    public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
            throws CoreException {
        return true;
    }
}
