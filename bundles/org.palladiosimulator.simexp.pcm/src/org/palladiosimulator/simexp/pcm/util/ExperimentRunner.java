package org.palladiosimulator.simexp.pcm.util;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.simexp.pcm.util.InitialPcmPartitionLoader.loadInitialBlackboard;
import static org.palladiosimulator.simexp.pcm.util.PcmSimulatedExperienceConstants.PCM_ANALYSIS_PARTITION;
import static org.palladiosimulator.simexp.pcm.util.PcmSimulatedExperienceConstants.PCM_WORKING_PARTITION;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.analyzer.workflow.core.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.experimentautomation.abstractsimulation.AbstractSimulationConfiguration;
import org.palladiosimulator.experimentautomation.application.VariationFactorTuple;
import org.palladiosimulator.experimentautomation.application.jobs.CopyPartitionJob;
import org.palladiosimulator.experimentautomation.application.jobs.RunExperimentForEachToolJob;
import org.palladiosimulator.experimentautomation.application.tooladapter.abstractsimulation.AbstractSimulationConfigFactory;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ToolConfiguration;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenarioRepository;
import org.palladiosimulator.failuremodel.failurescenario.FailurescenarioPackage;
import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;
import org.palladiosimulator.simulizar.core.launcher.jobs.SimuLizarPartitionIds;
import org.palladiosimulator.solver.core.models.PCMInstance;

import com.google.common.collect.Lists;

import de.uka.ipd.sdq.simulation.core.AbstractSimulationConfig;
import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class ExperimentRunner {

    private static final Logger LOGGER = Logger.getLogger(ExperimentRunner.class);

    private static final String FAILURE_SCENARIO_MODEL_URI = "http://palladiosimulator.org/failurescenario/1.0";
    private static final String FAILURE_TYPE_MODEL_URI = "http://palladiosimulator.org/failuretype/1.0";

    private class ExperimentRunExtractor {

        private class ToolContext {

            public AbstractSimulationConfiguration toolConfig;
            public Map<String, Object> configMap;

            public ToolContext(AbstractSimulationConfiguration toolConfig, Map<String, Object> configMap) {
                this.toolConfig = toolConfig;
                this.configMap = configMap;
            }

            public String getExperimentRunId() {
                return (String) configMap.get(AbstractSimulationConfig.EXPERIMENT_RUN);
            }

            public String getVariationId() {
                return (String) configMap.get(AbstractSimulationConfig.VARIATION_ID);
            }
        }

        private final List<ToolContext> toolContexts = Lists.newArrayList();

        private void cacheToolContext(ToolContext context) {
            toolContexts.add(context);
        }

        private ToolContext getOrCreateToolContext(AbstractSimulationConfiguration toolConfig) {
            Optional<ToolContext> result = findToolContext(toolConfig);
            if (result.isPresent()) {
                return result.get();
            }

            ToolContext context = new ToolContext(toolConfig, reproduceConfigMap(toolConfig));
            cacheToolContext(context);
            return context;
        }

        private Optional<ToolContext> findToolContext(AbstractSimulationConfiguration toolConfig) {
            return toolContexts.stream()
                .filter(c -> c.toolConfig.getName()
                    .equals(toolConfig.getName()))
                .findFirst();
        }

        @SuppressWarnings("rawtypes")
        private Map<String, Object> reproduceConfigMap(ToolConfiguration toolConfig) {
            return AbstractSimulationConfigFactory.createConfigMap(experiment,
                    (AbstractSimulationConfiguration) toolConfig, "", new ArrayList<VariationFactorTuple>());
        }

        public List<ExperimentRun> extractCurrentExperimentRuns() {
            List<ExperimentRun> runs = Lists.newArrayList();
            for (AbstractSimulationConfiguration each : filterSimulationConfigs()) {
                ExperimentGroup group = findExperimentGroup(each);
                ExperimentSetting setting = findExperimentSetting(group, getVariationId(each));
                ExperimentRun run = getCurrentRunFrom(setting);

                runs.add(run);
            }
            return runs;
        }

        private ExperimentRun getCurrentRunFrom(ExperimentSetting setting) {
            int last = setting.getExperimentRuns()
                .size() - 1;
            return setting.getExperimentRuns()
                .get(last);
        }

        private List<AbstractSimulationConfiguration> filterSimulationConfigs() {
            return experiment.getToolConfiguration()
                .stream()
                .filter(AbstractSimulationConfiguration.class::isInstance)
                .map(AbstractSimulationConfiguration.class::cast)
                .collect(toList());
        }

        private ExperimentGroup findExperimentGroup(AbstractSimulationConfiguration toolConfig) {
            Repository repository = RepositoryManager.getRepositoryFromUUID(toolConfig.getDatasource()
                .getId());
            for (final ExperimentGroup experimentGroup : repository.getExperimentGroups()) {
                String purpose = getExperimentRunId(toolConfig);
                if (experimentGroup.getPurpose()
                    .equals(purpose)) {
                    return experimentGroup;
                }
            }

            throw new IllegalArgumentException("Could not find experiment group.");
        }

        private ExperimentSetting findExperimentSetting(ExperimentGroup expGroup, String expSettingDesc) {
            for (final ExperimentSetting expSetting : expGroup.getExperimentSettings()) {
                if (expSetting.getDescription()
                    .equals(expSettingDesc)) {
                    return expSetting;
                }
            }

            throw new IllegalArgumentException("Could not find experiment setting for variation.");
        }

        private String getExperimentRunId(AbstractSimulationConfiguration toolConfig) {
            return getOrCreateToolContext(toolConfig).getExperimentRunId();
        }

        private String getVariationId(AbstractSimulationConfiguration toolConfig) {
            return getOrCreateToolContext(toolConfig).getVariationId();
        }

    }

    private class SimulationContext {

        private final MDSDBlackboard blackboard;

        public SimulationContext() {
            this.blackboard = loadInitialBlackboard(experiment);
        }

        @SuppressWarnings("unchecked")
        public BlackboardBasedWorkflow<MDSDBlackboard> initWorkflow(Experiment experiment) {
            SequentialJob simulationJob = new SequentialJob();
            simulationJob.add(new CopyUriPreservingPartitionJob(PCM_WORKING_PARTITION, PCM_ANALYSIS_PARTITION));
            // TODO check whether this is necessary.
            simulationJob.add(new CopyPartitionJob(PCM_WORKING_PARTITION,
                    SimuLizarPartitionIds.PCM_MODELS_ANALYZED_PARTITION_ID));
            simulationJob.add(new RunExperimentForEachToolJob(experiment));
            simulationJob.forEach(job -> ((IBlackboardInteractingJob<MDSDBlackboard>) job).setBlackboard(blackboard));

            return new BlackboardBasedWorkflow<>(simulationJob, blackboard);
        }

        public void runSimulation() {
            initWorkflow(experiment).run();
        }

        public MDSDBlackboard getBlackboard() {
            return blackboard;
        }

        public void injectFailureScenario(FailureScenarioRepository failureScenarioRepo,
                FailureTypeRepository failureTypeRepo) throws IOException {
            ResourceSetPartition partition = simulationContext.blackboard.getPartition(PCM_WORKING_PARTITION);
            URI modelURIFailureTypes = URI.createURI(FAILURE_TYPE_MODEL_URI);
            URI modelURIFailureScenario = URI.createURI(FAILURE_SCENARIO_MODEL_URI);

            ResourceSet resourceSet = partition.getResourceSet();
            // add in-memory models to blackboard partition
            addResourceToPartition(resourceSet, modelURIFailureTypes, failureTypeRepo);
            addResourceToPartition(resourceSet, modelURIFailureScenario, failureScenarioRepo);
        }

        public void updateFailureScenario(FailureScenarioRepository failureScenarioRepo) throws IOException {
            ResourceSetPartition partition = simulationContext.blackboard.getPartition(PCM_WORKING_PARTITION);
            URI modelURIFailureScenario = URI.createURI(FAILURE_SCENARIO_MODEL_URI);

            ResourceSet resourceSet = partition.getResourceSet();
            // update in-memory models to blackboard partition
            addResourceToPartition(resourceSet, modelURIFailureScenario, failureScenarioRepo);

        }

        private void addResourceToPartition(ResourceSet resourceSet, URI modelURI, EObject modelRoot) {
            Resource resource = resourceSet.getResource(modelURI, false);
            // check if resSet contains resource
            if (resource != null) {
                resourceSet.getResources()
                    .remove(resource);
            }
            try {
                Resource initalResource = create(resourceSet, modelURI, modelRoot);
                addResource(resourceSet, initalResource);
//              keep for debugging purposes
//                printResourceToFileSystem(initalResource);

            } catch (IOException e) {
                LOGGER.error(String.format("Failed to add model %s to resource set", modelURI), e);
            }
        }

        private void printResourceToFileSystem(Resource resource) throws FileNotFoundException, IOException {
            Path pathBaseDir = FileSystems.getDefault()
                .getPath("C:\\tmp\\SIMEXP\\");
            URI oldUri = resource.getURI();
            try {
                URI tmpUri = URI.createFileURI(
                        pathBaseDir.toString() + "\\" + oldUri.segment(0) + "_" + Long.toString(System.nanoTime()));
                resource.setURI(tmpUri);
                Map<?, ?> options = new HashMap<>();
                resource.save(options);
            } catch (IOException e) {
                LOGGER.error(String.format("Failed to persist resource URI %s to filesytem", "bla"), e);

            } finally {
                resource.setURI(oldUri);
            }

        }

        private Resource create(ResourceSet resourceSet, URI modelURI, EObject modelRoot) throws IOException {
            Resource newResource = resourceSet.createResource(modelURI);
            newResource.getContents()
                .clear();
            newResource.getContents()
                .add(modelRoot);
            resourceSet.getResources()
                .add(newResource);
            return newResource;
        }

        private void addResource(ResourceSet resourceSet, Resource newResource) {
            resourceSet.getResources()
                .add(newResource);
        }

        public void clearFailureScenarios(IExperimentProvider experimentProvider) {
            ResourceSetPartition plainPartition = experimentProvider.getExperimentRunner()
                .getPlainWorkingPartition();
            URI modelURIFailureScenario = URI.createURI(FAILURE_SCENARIO_MODEL_URI);
            FailureScenarioRepository failureScenarioRepo = (FailureScenarioRepository) plainPartition
                .getElement(FailurescenarioPackage.eINSTANCE.getFailureScenarioRepository())
                .get(0);
            failureScenarioRepo.getFailurescenarios()
                .clear();
            LOGGER.info(String.format("Cleared FailureScenario model at URI '%s'", modelURIFailureScenario));
        }

    }

    private final Experiment experiment;
    private final SimulationContext simulationContext;
    private final ExperimentRunExtractor expRunExtractor;

    public ExperimentRunner(Experiment initial) {
        this.experiment = initial;
        this.simulationContext = new SimulationContext();
        this.expRunExtractor = new ExperimentRunExtractor();
    }

    public void runExperiment() {
        simulationContext.runSimulation();
    }

    public List<ExperimentRun> getCurrentExperimentRuns() {
        return expRunExtractor.extractCurrentExperimentRuns();
    }

    public PCMInstance makeSnapshotOfPCM() {
        return new PCMInstance(copyPCMPartition());
    }

    private PCMResourceSetPartition copyPCMPartition() {
        return PcmUtil.copyPCMPartition(getWorkingPartition());
    }

    public PCMResourceSetPartition getWorkingPartition() {
        return (PCMResourceSetPartition) simulationContext.getBlackboard()
            .getPartition(PCM_WORKING_PARTITION);
    }

    public ResourceSetPartition getPlainWorkingPartition() {
        return simulationContext.getBlackboard()
            .getPartition(PCM_WORKING_PARTITION);
    }

    public void injectFailureScenario(FailureScenarioRepository failureScenarioRepo,
            FailureTypeRepository failureTypeRepo) throws IOException {
        simulationContext.injectFailureScenario(failureScenarioRepo, failureTypeRepo);
    }

    public void updateFailureScenario(FailureScenarioRepository failureScenarioRepo) throws IOException {
        simulationContext.updateFailureScenario(failureScenarioRepo);
    }

    public void clearFailureScenarios(IExperimentProvider experimentProvider) {
        simulationContext.clearFailureScenarios(experimentProvider);
    }

}
