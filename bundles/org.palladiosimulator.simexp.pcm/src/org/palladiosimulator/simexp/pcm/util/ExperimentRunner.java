package org.palladiosimulator.simexp.pcm.util;

import static org.palladiosimulator.simexp.pcm.util.InitialPcmPartitionLoader.loadInitialBlackboard;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.jobs.LoadPCMModelsIntoBlackboardJob;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.experimentautomation.application.VariationFactorTuple;
import org.palladiosimulator.experimentautomation.application.jobs.RunExperimentForEachToolJob;
import org.palladiosimulator.experimentautomation.application.tooladapter.abstractsimulation.AbstractSimulationConfigFactory;
import org.palladiosimulator.experimentautomation.application.tooladapter.simulizar.model.SimuLizarConfiguration;
import org.palladiosimulator.experimentautomation.application.tooladapter.simulizar.model.SimulizartooladapterPackage;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ToolConfiguration;
import org.palladiosimulator.solver.models.PCMInstance;

import de.uka.ipd.sdq.simulation.AbstractSimulationConfig;
import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class ExperimentRunner {

	private static final String SIMULATOR_ID_SIMULIZAR = "de.uka.ipd.sdq.codegen.simucontroller.simucom";

	private class SimulationContext {

		private final MDSDBlackboard blackboard;

		public SimulationContext() {
			this.blackboard = loadInitialBlackboard(experiment);
		}

		public BlackboardBasedWorkflow<MDSDBlackboard> initWorkflow(Experiment experiment) {
			return new BlackboardBasedWorkflow<MDSDBlackboard>(new RunExperimentForEachToolJob(experiment), blackboard);
		}

		public void runSimulation() {
			initWorkflow(experiment).run();
		}

		public MDSDBlackboard getBlackboard() {
			return blackboard;
		}

	}

	private final Experiment experiment;
	private final SimulationContext simulationContext;

	public ExperimentRunner(Experiment initial) {
		this.experiment = initial;
		this.simulationContext = new SimulationContext();
	}

	private ExperimentSetting findExperimentSetting(ExperimentGroup expGroup, String experimentSettingDescription) {
		for (final ExperimentSetting expSetting : expGroup.getExperimentSettings()) {
			if (expSetting.getDescription().equals(experimentSettingDescription)) {
				return expSetting;
			}
		}

		throw new IllegalArgumentException(
				"Could not find experiment setting for variation \"" + experimentSettingDescription + "\"");
	}

	private ExperimentGroup findExperimentGroup(Repository repository, String purpose) {
		for (final ExperimentGroup experimentGroup : repository.getExperimentGroups()) {
			if (experimentGroup.getPurpose().equals(purpose)) {
				return experimentGroup;
			}
		}

		throw new IllegalArgumentException("Could not find experiment group with purpose \"" + purpose + "\"");
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> reproduceConfigMap(SimuLizarConfiguration toolConfig) {
		return AbstractSimulationConfigFactory.createConfigMap(experiment, toolConfig, SIMULATOR_ID_SIMULIZAR,
				new ArrayList<VariationFactorTuple>());
	}

	private SimuLizarConfiguration findSimulationConfig(Stream<ToolConfiguration> toolConfigs) {
		Optional<ToolConfiguration> result = toolConfigs
				.filter(SimulizartooladapterPackage.eINSTANCE.getSimuLizarConfiguration()::isInstance).findFirst();
		return (SimuLizarConfiguration) result.orElseThrow(() -> new RuntimeException(""));
	}

	private ExperimentSetting extractExperimentSetting() {
		SimuLizarConfiguration toolConfig = findSimulationConfig(experiment.getToolConfiguration().stream());
		Map<String, Object> configMap = reproduceConfigMap(toolConfig);
		Repository repository = RepositoryManager.getRepositoryFromUUID(toolConfig.getDatasource().getId());
		ExperimentGroup expGroup = findExperimentGroup(repository,
				(String) configMap.get(AbstractSimulationConfig.EXPERIMENT_RUN));
		return findExperimentSetting(expGroup, (String) configMap.get(AbstractSimulationConfig.VARIATION_ID));
	}

	public ExperimentRun getCurrentExperimentRun() {
		ExperimentSetting expSetting = extractExperimentSetting();
		int last = expSetting.getExperimentRuns().size() - 1;
		return expSetting.getExperimentRuns().get(last);
	}

	public void runExperiment() {
		simulationContext.runSimulation();
	}

	public PCMInstance makeSnapshotOfPCM() {
		return new PCMInstance(copyPCMPartition());
	}

	private PCMResourceSetPartition copyPCMPartition() {
		// TODO exception handling
		PCMResourceSetPartition oldPartition = findAnalyzedPcmPartition().orElseThrow(() -> new RuntimeException(""));
		return PcmUtil.copyPCMPartition(oldPartition);
	}

	private Optional<PCMResourceSetPartition> loadPcmPartition(String id) {
		return Optional.ofNullable((PCMResourceSetPartition) simulationContext.getBlackboard().getPartition(id));
	}

	public MDSDBlackboard getBlackboard() {
		return simulationContext.getBlackboard();
	}

	public Optional<PCMResourceSetPartition> findAnalyzedPcmPartition() {
		return loadPcmPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID);
	}

}
