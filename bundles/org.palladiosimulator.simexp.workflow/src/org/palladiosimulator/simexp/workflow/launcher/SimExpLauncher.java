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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;
import org.palladiosimulator.core.simulation.SimulationExecution;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.dsl.kmodel.KmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutor.FaultTolerantLoadBalancingSimulationExecutorFactory;
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
            URI uri = URI.createURI(config.getKmodelFile());
            Kmodel kmodel = loadModel(uri);
            LOGGER.debug(String.format("Loaded kmodel from '%s'", uri.path()));
            SimulationExecution simulationExecutor = createSimulationExecutor(kmodel);
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
    
    private Kmodel loadModel(URI kmodelUri) throws IOException {
        KmodelStandaloneSetup.doSetup();
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(kmodelUri, true);
        EList<EObject> contents = resource.getContents();
        Kmodel model = (Kmodel) contents.get(0);
        return model;
    }

    
    private SimulationExecution createSimulationExecutor(Kmodel kmodel) {
//        LoadBalancingSimulationExecutorFactory loadBalancingSimulationExecutorFactory = new LoadBalancingSimulationExecutorFactory();
//        LoadBalancingSimulationExecutor simulationExecutor = loadBalancingSimulationExecutorFactory.create();
        FaultTolerantLoadBalancingSimulationExecutorFactory ftLoadBalancingSimulationExecutorFactory = new FaultTolerantLoadBalancingSimulationExecutorFactory();
        SimulationExecution simulationExecutor = ftLoadBalancingSimulationExecutorFactory.create(kmodel);
        return simulationExecutor;
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
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE)
                    , (String) launchConfigurationParams.get(ModelFileTypeConstants.KMODEL_FILE));

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
