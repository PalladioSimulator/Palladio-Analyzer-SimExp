package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.ISimExpPcmConfiguration;
import org.palladiosimulator.simexp.pcm.examples.ISimExpSimulationConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ExperimentLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.DistributionTypeModelUtil;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class LoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(LoadBalancingSimulationExecutor.class.getName());
    
	public final static double UPPER_THRESHOLD_RT = 2.0;
	public final static double LOWER_THRESHOLD_RT = 1.0;
	
	private IPcmMeasurementSpecProvider pcmMeasurementSpecsProvider;
	private IPcmMeasurementSpecificationBuilder pcmMeasurementSpecsBuilder;
	private ISimExpPcmConfiguration simExpPcmConfiguration;
	private ISimExpSimulationConfiguration simExpSimulationConfiguration;
	
    private IPcmExperienceSimulatorBuilder experienceSimBuilder;
    private IEnvironmentalDynamicsModelProvider dbnProvider;

	
	@Override
	protected void init() {
	    this.simExpPcmConfiguration = new LoadBalancingSimexpPcmConfiguration(
	            LoadBalancingSimexpPcmConfiguration.EXPERIMENT_FILE
	            , LoadBalancingSimexpPcmConfiguration.ENVIRONMENTAL_STATICS_MODEL_FILE
	            , LoadBalancingSimexpPcmConfiguration.ENVIRONMENTAL_DYNAMICS_MODEL_FILE);
	    this.experiment = new ExperimentLoader().loadExperiment(simExpPcmConfiguration.getExperimentFile());
	    ExperimentProvider.create(this.experiment);

	    this.pcmMeasurementSpecsBuilder = new LoadBalancingPcmMeasurementSpecificationBuilder();
	    this.pcmMeasurementSpecsProvider = new PcmMeasurementSpecProvider(this.experiment, pcmMeasurementSpecsBuilder);
	    this.simExpSimulationConfiguration = new SimExpSimulationConfig(pcmMeasurementSpecsProvider.getPcmMeasurementSpecs());
	    
	    this.experienceSimBuilder = new LoadBalancingSimulationExperienceBuilder();
	    this.dbnProvider = new PcmEnvironmentalDynamicsModelProvider();
	    
	    DistributionTypeModelUtil.get(BasicDistributionTypesLoader.loadRepository());
	    ProbabilityDistributionFactory.get().register(new MultinomialDistributionSupplier());
	    
        QVToReconfigurationManager.create(getReconfigurationRulesLocation());
	}
	

	@Override
	protected String getExperimentFile() {
		return simExpPcmConfiguration.getExperimentFile();
	}

	@Override
	public void evaluate() {
	    String reconfigurationPolicyId = simExpSimulationConfiguration.getReconfigurationPolicy().getId();
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simExpSimulationConfiguration.getSimulationId(), reconfigurationPolicyId);
		SimulatedExperienceEvaluator evaluator = SimulatedExperienceEvaluator.of(simExpSimulationConfiguration.getSimulationId(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfigurationPolicyId, evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}
	
	@Override
	protected ExperienceSimulator createSimulator() {
        Resource usageModel = experiment.getInitialModel().getUsageModel().eResource();
	    DynamicBayesianNetwork dbn = dbnProvider.getEnvironmentalDynamicsModel(simExpPcmConfiguration, usageModel);
	    return experienceSimBuilder.createSimulator(simExpSimulationConfiguration, experiment, dbn);
	}

}
