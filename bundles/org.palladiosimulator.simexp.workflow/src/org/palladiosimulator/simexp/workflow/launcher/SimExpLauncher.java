package org.palladiosimulator.simexp.workflow.launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.workflow.config.ArchitecturalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.jobs.SimExpAnalyzerRootJob;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;

public class SimExpLauncher extends AbstractPCMLaunchConfigurationDelegate<SimExpWorkflowConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(SimExpLauncher.class.getName());

    public SimExpLauncher() {

    }

    @Override
    protected IJob createWorkflowJob(SimExpWorkflowConfiguration config, ILaunch launch) throws CoreException {
        LOGGER.debug("Create SimExp workflow root job");
        
        try {
            URI uri = URI.createURI(config.getExperimentsFile());
            Experiment experiment = loadExperiment(uri);
            LOGGER.debug(String.format("Loaded experiment from '%s'", uri.path()));
            
            SimulationExecutor simulationExecutor = createSimulationExecutor(experiment);
            return new SimExpAnalyzerRootJob(config, simulationExecutor, launch);
        } catch (IOException e) {
            IStatus status = Status.error(e.getMessage(), e);
            throw new CoreException(status);
        }
    }

    @Override
    protected SimExpWorkflowConfiguration deriveConfiguration(ILaunchConfiguration configuration, String mode)
            throws CoreException {
        LOGGER.debug("Derive workflow configuration");
        return buildWorkflowConfiguration(configuration, mode);
    }
    
    private Experiment loadExperiment(URI experimentUri) throws IOException {
    	/*
        KmodelStandaloneSetup.doSetup();
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(kmodelUri, true);
        EList<EObject> contents = resource.getContents();
        Kmodel model = (Kmodel) contents.get(0);
        return model;
        */
    	// TODO
    	return null;
    }
    
    private SimulationExecutor createSimulationExecutor(Experiment experiment) {
//      LoadBalancingSimulationExecutorFactory loadBalancingSimulationExecutorFactory = new LoadBalancingSimulationExecutorFactory();
//      SimulationExecution simulationExecutor = loadBalancingSimulationExecutorFactory.create(kmodel);
      //FaultTolerantLoadBalancingSimulationExecutorFactory ftLoadBalancingSimulationExecutorFactory = new FaultTolerantLoadBalancingSimulationExecutorFactory();
      //SimulationExecution simulationExecutor = ftLoadBalancingSimulationExecutorFactory.create(kmodel);
      //return simulationExecutor;
    	// TODO
    	return null;
  }

    
    private SimExpWorkflowConfiguration buildWorkflowConfiguration(ILaunchConfiguration configuration, String mode) {

        SimExpWorkflowConfiguration workflowConfiguration = null;
        try {
            Map<String, Object> launchConfigurationParams = configuration.getAttributes();

            if (LOGGER.isDebugEnabled()) {
                for (Entry<String, Object> entry : launchConfigurationParams.entrySet()) {
                    LOGGER.debug(
                            String.format("launch configuration param ['%s':'%s']", entry.getKey(), entry.getValue()));
                }
            }

            ArchitecturalModelsWorkflowConfiguration architecturalModels = new ArchitecturalModelsWorkflowConfiguration(
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.USAGE_FILE),
                    Arrays.asList((String) launchConfigurationParams.get(ModelFileTypeConstants.ALLOCATION_FILE)),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.EXPERIMENTS_FILE));

            workflowConfiguration = new SimExpWorkflowConfiguration(architecturalModels);

        } catch (CoreException e) {
            LOGGER.error(
                    "Failed to read workflow configuration from passed launch configuration. Please check the provided launch configuration",
                    e);
        }

        return workflowConfiguration;
    }

    @Override
    protected ArrayList<LoggerAppenderStruct> setupLogging(Level logLevel) throws CoreException {
        // FIXME: during development set debug level hard-coded to DEBUG
        ArrayList<LoggerAppenderStruct> loggerList = super.setupLogging(Level.DEBUG);
        loggerList.add(setupLogger("org.palladiosimulator.simexp", logLevel,
                Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        loggerList.add(setupLogger("org.palladiosimulator.experimentautomation.application", logLevel,
                Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        loggerList.add(setupLogger("org.palladiosimulator.simulizar.reconfiguration.qvto", logLevel,
                Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        return loggerList;
    }

}
