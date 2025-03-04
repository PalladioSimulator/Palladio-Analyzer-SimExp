package org.palladiosimulator.simexp.workflow.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.csv.accessor.CsvAccessor;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;
import org.palladiosimulator.simexp.workflow.config.ArchitecturalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EnvironmentalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EvolutionaryAlgorithmConfiguration;
import org.palladiosimulator.simexp.workflow.config.MonitorConfiguration;
import org.palladiosimulator.simexp.workflow.config.PrismConfiguration;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.jobs.SimExpAnalyzerRootJob;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;
import de.uka.ipd.sdq.workflow.logging.console.StreamsProxyAppender;
import tools.mdsd.probdist.api.random.FixedSeedProvider;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class SimExpLauncher extends AbstractPCMLaunchConfigurationDelegate<SimExpWorkflowConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(SimExpLauncher.class.getName());

    @Override
    protected IJob createWorkflowJob(SimExpWorkflowConfiguration config, ILaunch launch) throws CoreException {
        LOGGER.debug("Create SimExp workflow root job");
        try {
            SimulationParameters simulationParameters = config.getSimulationParameters();
            LaunchDescriptionProvider launchDescriptionProvider = new LaunchDescriptionProvider(simulationParameters);
            Optional<ISeedProvider> seedProvider = config.getSeedProvider();

            SimulationExecutorLookup simulationExecutorLookup = new SimulationExecutorLookup();
            String simulationID = simulationParameters.getSimulationID();
            Path resourcePath = getResourcePath(simulationID);
            Files.createDirectories(resourcePath);
            SimulatedExperienceAccessor accessor = new CsvAccessor(resourcePath);
            SimulationExecutor simulationExecutor = simulationExecutorLookup.lookupSimulationExecutor(config,
                    launchDescriptionProvider, seedProvider, accessor, resourcePath);
            if (simulationExecutor == null) {
                throw new IllegalArgumentException("Unable to create simulation executor");
            }
            String policyId = simulationExecutor.getPolicyId();
            launchDescriptionProvider.setPolicyId(policyId);
            return new SimExpAnalyzerRootJob(config, simulationExecutor, launch);
        } catch (Exception e) {
            IStatus status = Status.error(e.getMessage(), e);
            throw new CoreException(status);
        }
    }

    private Path getResourcePath(String strategyId) {
        IPath workspaceBasePath = ResourcesPlugin.getWorkspace()
            .getRoot()
            .getLocation();
        Path outputBasePath = Paths.get(workspaceBasePath.toString());
        Path resourcePath = outputBasePath.resolve("resource");
        return resourcePath.resolve(strategyId);
    }

    @Override
    protected SimExpWorkflowConfiguration deriveConfiguration(ILaunchConfiguration configuration, String mode)
            throws CoreException {
        LOGGER.debug("Derive workflow configuration");
        return buildWorkflowConfiguration(configuration, mode);
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
            Set<String> transformationNames = configuration.getAttribute(SimulationConstants.TRANSFORMATIONS_ACTIVE,
                    Collections.emptySet());
            String modelledOptimizationTypeStr = (String) launchConfigurationParams
                .get(SimulationConstants.MODELLED_OPTIMIZATION_TYPE);
            ModelledOptimizationType modelledOptimizationType = ModelledOptimizationType
                .valueOf(modelledOptimizationTypeStr);

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

            Integer customSeed = (Integer) launchConfigurationParams.get(SimulationConstants.CUSTOM_SEED);
            Optional<ISeedProvider> seedProvider = Optional.empty();
            if (customSeed != null) {
                seedProvider = Optional.of(new FixedSeedProvider(customSeed));
            }

            int populationSize = (Integer) launchConfigurationParams.get(SimulationConstants.POPULATION_SIZE);
            Optional<Integer> maxGenerations = Optional
                .ofNullable((Integer) launchConfigurationParams.get(SimulationConstants.MAX_GENERATIONS));
            Optional<Integer> steadyFitness = Optional
                .ofNullable((Integer) launchConfigurationParams.get(SimulationConstants.STEADY_FITNESS));
            Optional<Double> mutationRate = extractOptionalDouble(launchConfigurationParams,
                    SimulationConstants.MUTATION_RATE);
            Optional<Double> crossoverRate = extractOptionalDouble(launchConfigurationParams,
                    SimulationConstants.CROSSOVER_RATE);
            EvolutionaryAlgorithmConfiguration eaConfig = new EvolutionaryAlgorithmConfiguration(populationSize,
                    maxGenerations, steadyFitness, mutationRate, crossoverRate);

            /** FIXME: split workflow configuraiton based on simulation type: PCM, PRISM */
            workflowConfiguration = new SimExpWorkflowConfiguration(simulatorType, simulationEngine,
                    transformationNames, qualityObjective, architecturalModels, modelledOptimizationType, monitors,
                    prismConfig, environmentalModels, simulationParameters, seedProvider, eaConfig);
        } catch (CoreException e) {
            LOGGER.error(
                    "Failed to read workflow configuration from passed launch configuration. Please check the provided launch configuration",
                    e);
        }

        return workflowConfiguration;
    }

    private Optional<Double> extractOptionalDouble(Map<String, Object> launchConfigurationParams, String key) {
        String stringValue = (String) launchConfigurationParams.get(key);
        if (stringValue == null) {
            return Optional.empty();
        }
        double value = Double.parseDouble(stringValue);
        return Optional.of(value);
    }

    @Override
    protected List<LoggerAppenderStruct> configureLogging(ILaunchConfiguration configuration) throws CoreException {
        Path simulationLogFolder = getSimulationLogFolder();

        try {
            Files.createDirectories(simulationLogFolder);
        } catch (IOException e) {
            IStatus status = new Status(IStatus.ERROR, "org.palladiosimulator.simexp.workflow", 0, e.getMessage(), e);
            throw new CoreException(status);
        }

        Map<String, Object> launchConfigurationParams = configuration.getAttributes();
        String simulationId = (String) launchConfigurationParams.get(SimulationConstants.SIMULATION_ID);

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = dateFormat.format(currentDate);
        String simulationFileName = String.format("%s_%s.log", simulationId, currentDateTime);
        Path simulationLogFile = simulationLogFolder.resolve(simulationFileName);

        FileAppender fa = new FileAppender();
        fa.setName("SimulationLogger");
        fa.setFile(simulationLogFile.toString());
        fa.setLayout(new PatternLayout("%d %-5p [%-10t] [%F:%L]: %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.activateOptions();
        // Level logLevel = getLogLevel(configuration);
        Level logLevel = Level.INFO;
        List<LoggerAppenderStruct> appenders = setupLogging(logLevel);
        for (LoggerAppenderStruct entry : appenders) {
            Logger entryLogger = entry.getLogger();
            entryLogger.addAppender(fa);
            StreamsProxyAppender appender = entry.getAppender();
            appender.setThreshold(Level.INFO);
        }
        return appenders;
    }

    private Path getSimulationLogFolder() {
        IPath workspaceBasePath = ResourcesPlugin.getWorkspace()
            .getRoot()
            .getLocation();
        Path outputBasePath = Paths.get(workspaceBasePath.toString());
        Path resourcePath = outputBasePath.resolve("log");
        return resourcePath;
    }

    @Override
    protected ArrayList<LoggerAppenderStruct> setupLogging(Level logLevel) throws CoreException {
        // String layout = Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN;
        String layout = "%d{ABSOLUTE} %-5p [%-10t] [%F:%L]: %m%n";
        // ArrayList<LoggerAppenderStruct> loggerList = super.setupLogging(Level.DEBUG);
        ArrayList<LoggerAppenderStruct> loggerList = new ArrayList<>();

        loggerList.add(setupLogger("de.uka.ipd.sdq.workflow", logLevel, layout));
        loggerList.add(setupLogger("de.uka.ipd.sdq.workflow.ExecutionTimeLoggingProgressMonitor", Level.WARN, layout));
        loggerList.add(setupLogger("org.palladiosimulator.experimentautomation.application.jobs.CopyPartitionJob",
                Level.WARN, layout));
        loggerList.add(setupLogger("org.openarchitectureware", logLevel, layout));

        loggerList.add(setupLogger("org.palladiosimulator.simexp", logLevel, layout));

        loggerList.add(setupLogger("org.palladiosimulator.experimentautomation.application", logLevel, layout));
        loggerList
            .add(setupLogger("org.palladiosimulator.experimentautomation.application.jobs.LogExperimentInformationJob",
                    Level.WARN, layout));
        loggerList.add(setupLogger("org.palladiosimulator.simulizar.reconfiguration.qvto", logLevel, layout));
        loggerList.add(setupLogger("org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator",
                Level.WARN, layout));
        loggerList.add(setupLogger("de.fzi.srp.simulatedexperience.prism.wrapper.service", logLevel, layout));
        return loggerList;
    }
}
