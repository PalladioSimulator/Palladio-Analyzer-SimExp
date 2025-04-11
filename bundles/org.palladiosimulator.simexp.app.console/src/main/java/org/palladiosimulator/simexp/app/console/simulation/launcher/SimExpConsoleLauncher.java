package org.palladiosimulator.simexp.app.console.simulation.launcher;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.palladiosimulator.simexp.app.console.simulation.OptimizableValues;
import org.palladiosimulator.simexp.app.console.simulation.OptimizableValues.BoolEntry;
import org.palladiosimulator.simexp.app.console.simulation.OptimizableValues.DoubleEntry;
import org.palladiosimulator.simexp.app.console.simulation.OptimizableValues.IntEntry;
import org.palladiosimulator.simexp.app.console.simulation.OptimizableValues.StringEntry;
import org.palladiosimulator.simexp.app.console.simulation.workflow.ConsoleWorkflow;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.workflow.api.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.launcher.SimExpLauncher;

import com.google.gson.Gson;

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
    protected Map<String, Object> getOptimizedValues(ILaunchConfiguration configuration) throws CoreException {
        if (!configuration.hasAttribute(SimulationConstants.OPTIMIZED_VALUES)) {
            return null;
        }

        String jsonValues = configuration.getAttribute(SimulationConstants.OPTIMIZED_VALUES, "");
        Gson gson = new Gson();
        OptimizableValues optimizableValues = gson.fromJson(jsonValues, OptimizableValues.class);
        Map<String, Object> optimizedValues = new HashMap<>();
        for (BoolEntry entry : optimizableValues.boolValues) {
            optimizedValues.put(entry.name, entry.value);
        }
        for (StringEntry entry : optimizableValues.stringValues) {
            optimizedValues.put(entry.name, entry.value);
        }
        for (IntEntry entry : optimizableValues.intValues) {
            optimizedValues.put(entry.name, entry.value);
        }
        for (DoubleEntry entry : optimizableValues.doubleValues) {
            optimizedValues.put(entry.name, entry.value);
        }
        return optimizedValues;
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
