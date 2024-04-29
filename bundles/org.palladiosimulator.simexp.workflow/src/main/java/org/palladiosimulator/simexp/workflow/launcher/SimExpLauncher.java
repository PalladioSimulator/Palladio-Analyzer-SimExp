package org.palladiosimulator.simexp.workflow.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.modelled.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.ArchitecturalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EnvironmentalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.MonitorConfiguration;
import org.palladiosimulator.simexp.workflow.config.PrismConfiguration;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.jobs.SimExpAnalyzerRootJob;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;

public class SimExpLauncher extends AbstractPCMLaunchConfigurationDelegate<SimExpWorkflowConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(SimExpLauncher.class.getName());

    @Override
    protected IJob createWorkflowJob(SimExpWorkflowConfiguration config, ILaunch launch) throws CoreException {
        LOGGER.debug("Create SimExp workflow root job");
        try {
            SimulationParameters simulationParameters = config.getSimulationParameters();
            LaunchDescriptionProvider launchDescriptionProvider = new LaunchDescriptionProvider(simulationParameters);

            SimulatorType simulatorType = config.getSimulatorType();
            SimulationExecutor simulationExecutor = switch (simulatorType) {
            case CUSTOM -> {
                yield createCustomSimulationExecutor(config, launchDescriptionProvider);
            }
            case MODELLED -> {
                yield createModelledSimulationExecutor(config, launchDescriptionProvider);
            }
            default -> throw new IllegalArgumentException("SimulatorType not supported: " + simulatorType);
            };
            String policyId = simulationExecutor.getPolicyId();
            launchDescriptionProvider.setPolicyId(policyId);
            return new SimExpAnalyzerRootJob(config, simulationExecutor, launch);
        } catch (Exception e) {
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

    private SimulationExecutor createCustomSimulationExecutor(IWorkflowConfiguration workflowConfiguration,
            DescriptionProvider descriptionProvider) {
        PcmModelLoader modelLoader = new PcmModelLoader();
        SimulationEngine simulationEngine = workflowConfiguration.getSimulationEngine();
        return switch (simulationEngine) {
        case PCM -> {
            PcmSimulationExecutorFactory factory = new PcmSimulationExecutorFactory();
            yield factory.create((IPCMWorkflowConfiguration) workflowConfiguration, modelLoader, descriptionProvider);
        }
        case PRISM -> {
            PrismSimulationExecutorFactory factory = new PrismSimulationExecutorFactory();
            yield factory.create((IPrismWorkflowConfiguration) workflowConfiguration, modelLoader, descriptionProvider);
        }
        default -> throw new RuntimeException("Unexpected simulation engine " + simulationEngine);
        };
    }

    private SimulationExecutor createModelledSimulationExecutor(IModelledWorkflowConfiguration workflowConfiguration,
            LaunchDescriptionProvider launchDescriptionProvider) {
        ModelledSimulationExecutorFactory factory = new ModelledSimulationExecutorFactory();
        PcmModelLoader modelLoader = new PcmModelLoader();
        return factory.create(workflowConfiguration, modelLoader, launchDescriptionProvider);
    }

    @SuppressWarnings("unchecked")
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

            String simulatorTypeStr = (String) launchConfigurationParams.get(SimulationConstants.SIMULATOR_TYPE);
            SimulatorType simulatorType = SimulatorType.valueOf(simulatorTypeStr);
            String simulationEngineStr = (String) launchConfigurationParams.get(SimulationConstants.SIMULATION_ENGINE);
            SimulationEngine simulationEngine = SimulationEngine.valueOf(simulationEngineStr);

            SimulationParameters simulationParameters = new SimulationParameters(
                    (String) launchConfigurationParams.get(SimulationConstants.SIMULATION_ID),
                    (int) launchConfigurationParams.get(SimulationConstants.NUMBER_OF_RUNS),
                    (int) launchConfigurationParams.get(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN));

            ArchitecturalModelsWorkflowConfiguration architecturalModels = new ArchitecturalModelsWorkflowConfiguration(
                    Arrays.asList((String) launchConfigurationParams.get(ModelFileTypeConstants.ALLOCATION_FILE)),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.USAGE_FILE),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.EXPERIMENTS_FILE),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.SMODEL_FILE));
            EnvironmentalModelsWorkflowConfiguration environmentalModels = new EnvironmentalModelsWorkflowConfiguration(
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.STATIC_MODEL_FILE),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.DYNAMIC_MODEL_FILE));

            /** simulation type = PCM */
            QualityObjective qualityObjective = SimulationConstants.DEFAULT_QUALITY_OBJECTIVE;
            String monitorRepositoryFile = StringUtils.EMPTY;
            List<String> configuredMonitors = new ArrayList<>();
            if (simulationEngine == SimulationEngine.PCM) {
                String qualityObjectiveStr = (String) launchConfigurationParams
                    .get(SimulationConstants.QUALITY_OBJECTIVE);
                qualityObjective = QualityObjective.valueOf(qualityObjectiveStr);

                monitorRepositoryFile = (String) launchConfigurationParams
                    .get(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE);
                configuredMonitors
                    .addAll((List<String>) launchConfigurationParams.get(ModelFileTypeConstants.MONITORS));
            }

            MonitorConfiguration monitors = new MonitorConfiguration(monitorRepositoryFile, configuredMonitors);

            /** simulation type = PRISM */
            List<String> prismProperties = new ArrayList<>();
            List<String> prismModules = new ArrayList<>();
            if (simulationEngine == SimulationEngine.PRISM) {
                List<String> launchConfigPrismProperties = (List<String>) launchConfigurationParams
                    .get(ModelFileTypeConstants.PRISM_PROPERTY_FILE);
                List<String> launchConfigModulesProperties = (List<String>) launchConfigurationParams
                    .get(ModelFileTypeConstants.PRISM_MODULE_FILE);
                prismProperties.addAll(launchConfigPrismProperties);
                prismModules.addAll(launchConfigModulesProperties);
            }
            PrismConfiguration prismConfig = new PrismConfiguration(prismProperties, prismModules);

            /** FIXME: split workflow configuraiton based on simulation type: PCM, PRISM */
            workflowConfiguration = new SimExpWorkflowConfiguration(simulatorType, simulationEngine, qualityObjective,
                    architecturalModels, monitors, prismConfig, environmentalModels, simulationParameters);
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
