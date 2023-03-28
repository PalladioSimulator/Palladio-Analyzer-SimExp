package org.palladiosimulator.simexp.workflow.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.pcm.examples.executor.DynamicBehaviourExtensionLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.ExperimentRepositoryLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.ExperimentRepositoryResolver;
import org.palladiosimulator.simexp.pcm.examples.executor.GroundProbabilisticNetworkLoader;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutor.FaultTolerantLoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;
import org.palladiosimulator.simexp.workflow.config.ArchitecturalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EnvironmentalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.jobs.SimExpAnalyzerRootJob;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.apache.util.ProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.api.parser.DefaultParameterParser;
import tools.mdsd.probdist.api.parser.ParameterParser;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionRepository;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class SimExpLauncher extends AbstractPCMLaunchConfigurationDelegate<SimExpWorkflowConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(SimExpLauncher.class.getName());

    public SimExpLauncher() {

    }

    @Override
    protected IJob createWorkflowJob(SimExpWorkflowConfiguration config, ILaunch launch) throws CoreException {
        LOGGER.debug("Create SimExp workflow root job");
        
        try {
        	ResourceSet rs = new ResourceSetImpl();
        	
            URI experimentsFileURI = config.getExperimentsURI();
            ExperimentRepositoryLoader expLoader = new ExperimentRepositoryLoader();
            LOGGER.debug(String.format("Loading experiment from: '%s'", experimentsFileURI));
            ExperimentRepository experimentRepository = expLoader.load(rs, experimentsFileURI);
            
            ExperimentRepositoryResolver expRepoResolver = new ExperimentRepositoryResolver();
            Experiment experiment = expRepoResolver.resolveExperiment(experimentRepository);
            
            URI staticModelURI = config.getStaticModelURI();
            GroundProbabilisticNetworkLoader gpnLoader = new GroundProbabilisticNetworkLoader();
            LOGGER.debug(String.format("Loading static model from: '%s'", staticModelURI));
            GroundProbabilisticNetwork gpn = gpnLoader.load(rs, staticModelURI);
            
            URI dynamicModelURI = config.getDynamicModelURI();
            DynamicBehaviourExtensionLoader dbeLoader = new DynamicBehaviourExtensionLoader();
            LOGGER.debug(String.format("Loading dynamic model from: '%s'", dynamicModelURI));
            DynamicBehaviourExtension dbe = dbeLoader.load(rs, dynamicModelURI);
            

            ParameterParser parameterParser = new DefaultParameterParser();
            ProbabilityDistributionFactory defaultProbabilityDistributionFactory = new ProbabilityDistributionFactory();
            IProbabilityDistributionRegistry probabilityDistributionRegistry = defaultProbabilityDistributionFactory;
            IProbabilityDistributionFactory probabilityDistributionFactory = defaultProbabilityDistributionFactory;
            
            ProbabilityDistributionRepository probabilityDistributionRepository = BasicDistributionTypesLoader.loadRepository();
            IProbabilityDistributionRepositoryLookup probDistRepoLookup = new ProbabilityDistributionRepositoryLookup(probabilityDistributionRepository);

            BayesianNetwork bn = new BayesianNetwork(null, gpn, probabilityDistributionFactory);
            DynamicBayesianNetwork dbn = new DynamicBayesianNetwork(null, bn, dbe, probabilityDistributionFactory);

            SimulationExecutor simulationExecutor = createSimulationExecutor(experiment, dbn, probabilityDistributionRegistry, 
            		probabilityDistributionFactory, parameterParser, probDistRepoLookup, config.getSimulationParameters());
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
    
    private SimulationExecutor createSimulationExecutor(Experiment experiment, DynamicBayesianNetwork dbn, 
    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters) {
//      LoadBalancingSimulationExecutorFactory loadBalancingSimulationExecutorFactory = new LoadBalancingSimulationExecutorFactory();
//      SimulationExecution simulationExecutor = loadBalancingSimulationExecutorFactory.create(kmodel);
        FaultTolerantLoadBalancingSimulationExecutorFactory factory = new FaultTolerantLoadBalancingSimulationExecutorFactory();
//    	DeltaIoTSimulationExecutorFactory factory = new DeltaIoTSimulationExecutorFactory();
        return factory.create(experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, 
        		probDistRepoLookup, simulationParameters);
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
            
            @SuppressWarnings("unchecked")
			ArchitecturalModelsWorkflowConfiguration architecturalModels = new ArchitecturalModelsWorkflowConfiguration(
            		Arrays.asList((String) launchConfigurationParams.get(ModelFileTypeConstants.ALLOCATION_FILE)),
            		(String) launchConfigurationParams.get(ModelFileTypeConstants.USAGE_FILE),
            		(String) launchConfigurationParams.get(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE),
            		(String) launchConfigurationParams.get(ModelFileTypeConstants.EXPERIMENTS_FILE),
            		(List<String>) launchConfigurationParams.get(ModelFileTypeConstants.MONITORS));
            
            EnvironmentalModelsWorkflowConfiguration environmentalModels = new EnvironmentalModelsWorkflowConfiguration(
            		(String) launchConfigurationParams.get(ModelFileTypeConstants.STATIC_MODEL_FILE),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.DYNAMIC_MODEL_FILE));
            
            SimulationParameterConfiguration simulationParameters = new SimulationParameterConfiguration(
            		(String) launchConfigurationParams.get(SimulationConstants.SIMULATION_ID),
            		(int) launchConfigurationParams.get(SimulationConstants.NUMBER_OF_RUNS),
            		(int) launchConfigurationParams.get(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN));
            		

            workflowConfiguration = new SimExpWorkflowConfiguration(architecturalModels, environmentalModels, simulationParameters);

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
