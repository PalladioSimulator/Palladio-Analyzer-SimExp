package org.palladiosimulator.simexp.pcm.util;

import static org.palladiosimulator.simexp.pcm.util.InitialPcmPartitionLoader.loadInitialBlackboard;

import java.util.ArrayList;
import java.util.List;
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
import org.palladiosimulator.experimentautomation.abstractsimulation.AbstractSimulationConfiguration;
import org.palladiosimulator.experimentautomation.application.VariationFactorTuple;
import org.palladiosimulator.experimentautomation.application.jobs.ComputeVariantsAndAddExperimentJob;
import org.palladiosimulator.experimentautomation.application.jobs.RunExperimentForEachToolJob;
import org.palladiosimulator.experimentautomation.application.tooladapter.abstractsimulation.AbstractSimulationConfigFactory;
import org.palladiosimulator.experimentautomation.application.tooladapter.simulizar.model.SimuLizarConfiguration;
import org.palladiosimulator.experimentautomation.application.tooladapter.simulizar.model.SimulizartooladapterPackage;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ToolConfiguration;
import org.palladiosimulator.solver.models.PCMInstance;

import de.uka.ipd.sdq.simulation.AbstractSimulationConfig;
import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class ExperimentRunner {

	private static final String SIMULATOR_ID_SIMULIZAR = "de.uka.ipd.sdq.codegen.simucontroller.simucom";
	
	private class SimulationContext {
		
		private final MDSDBlackboard blackboard;
		private final BlackboardBasedWorkflow<MDSDBlackboard> workflow;
		
		public SimulationContext() {
			this.blackboard = loadInitialBlackboard(experiment);
			this.workflow = extractWorkflow(experiment);
		}

		public BlackboardBasedWorkflow<MDSDBlackboard> extractWorkflow(Experiment experiment) {
			return new BlackboardBasedWorkflow<MDSDBlackboard>(new RunExperimentForEachToolJob(experiment), blackboard);
		}
		
		public void runSimulation() {
			if (wasExecuted) {
				resetSimulation();
			}
			workflow.run();
		}
		
		private void resetSimulation() {
			workflow.clear();
			workflow.addAll(loadSimulationJobs());
		}

		private List<IJob> loadSimulationJobs() {
			List<IJob> jobs = new ArrayList<>();
			for (ToolConfiguration each : experiment.getToolConfiguration()) {
				if (each instanceof AbstractSimulationConfiguration) {
					jobs.add(new ComputeVariantsAndAddExperimentJob(experiment, (AbstractSimulationConfiguration) each));
				}
			}
			
			if (jobs.isEmpty()) {
				//TODO exception handling
				throw new RuntimeException("");
			}
			return jobs;
		}
		
		public MDSDBlackboard getBlackboard() {
			return blackboard;
		}
		
	}
	
	private final Experiment experiment;
	private final SimulationContext simulationContext;
	
	private boolean wasExecuted;
	
	public ExperimentRunner(Experiment initial) {
		this.experiment = initial;
		this.simulationContext = new SimulationContext();
		this.wasExecuted = false;
	}
	
	private ExperimentSetting findExperimentSetting(ExperimentGroup expGroup, String experimentSettingDescription) {
		for (final ExperimentSetting expSetting : expGroup.getExperimentSettings()) {
            if (expSetting.getDescription().equals(experimentSettingDescription)) {
                return expSetting;
            }
        }

        throw new IllegalArgumentException("Could not find experiment setting for variation \"" + experimentSettingDescription + "\"");
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
		return AbstractSimulationConfigFactory.createConfigMap(experiment, toolConfig, SIMULATOR_ID_SIMULIZAR, new ArrayList<VariationFactorTuple>());
	}

	private SimuLizarConfiguration findSimulationConfig(Stream<ToolConfiguration> toolConfigs) {
		Optional<ToolConfiguration> result = toolConfigs.filter(each -> SimulizartooladapterPackage.eINSTANCE.getSimuLizarConfiguration().isInstance(each))
														.findFirst();
		return (SimuLizarConfiguration) result.orElseThrow(() -> new RuntimeException(""));
	}
	
	public ExperimentSetting extractExperimentSetting() {
		SimuLizarConfiguration toolConfig = findSimulationConfig(experiment.getToolConfiguration().stream());
		Map<String, Object> configMap = reproduceConfigMap(toolConfig);
		Repository repository = RepositoryManager.getRepositoryFromUUID(toolConfig.getDatasource().getId());
		ExperimentGroup expGroup = findExperimentGroup(repository, (String) configMap.get(AbstractSimulationConfig.EXPERIMENT_RUN));
		return findExperimentSetting(expGroup, (String) configMap.get(AbstractSimulationConfig.VARIATION_ID));
	}
	
	public ExperimentRun getCurrentExperimentRun() {
		ExperimentSetting expSetting = extractExperimentSetting();
		int last = expSetting.getExperimentRuns().size() - 1;
		return expSetting.getExperimentRuns().get(last);
	}
	
	public void runExperiment() {
		simulationContext.runSimulation();
		markAsExecuted();
//		if (wasExecuted == false) {
//			normalizePartitionIds();
//			markAsExecuted();
//		}
	}
	
	private void markAsExecuted() {
		wasExecuted = true;
	}

//	private void normalizePartitionIds() {
//		loadPcmPartition(LoadSimuLizarModelsIntoBlackboardJob.PCM_MODELS_ANALYZED_PARTITION_ID).ifPresent(this::normalizePartitionIds);
//	}
//	
//	// This is rather a simulizar normalization; check whether this also applies to other simulation engines...
//	private void normalizePartitionIds(PCMResourceSetPartition target) {
//		simulationContext.getBlackboard().removePartition(LoadSimuLizarModelsIntoBlackboardJob.PCM_MODELS_ANALYZED_PARTITION_ID);
//		simulationContext.getBlackboard().addPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID, target);
//	}
	
	public PCMInstance makeSnapshotOfPCM() {
		return new PCMInstance(copyPCMPartition());
	}

	private PCMResourceSetPartition copyPCMPartition()  {
		//TODO exception handling
		PCMResourceSetPartition oldPartition = findAnalyzedPcmPartition().orElseThrow(() -> new RuntimeException(""));
		return PcmUtil.copyPCMPartition(oldPartition);
    }
	
	private Optional<PCMResourceSetPartition> loadPcmPartition(String id) {
		return Optional.ofNullable((PCMResourceSetPartition) simulationContext.getBlackboard().getPartition(id));
	}
	
	public MDSDBlackboard getBlackboard() {
		return simulationContext.getBlackboard();
	}
	
//	public boolean wasExecuted() {
//		return wasExecuted;
//	}
	
	public Optional<PCMResourceSetPartition> findAnalyzedPcmPartition() {
		return loadPcmPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID);
	}
	
}
