package org.palladiosimulator.simexp.pcm.action;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.jobs.LoadPCMModelsIntoBlackboardJob;
import org.palladiosimulator.commons.eclipseutils.FileHelper;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;
import org.palladiosimulator.simulizar.reconfiguration.qvto.util.ModelTransformationCache;
import org.palladiosimulator.simulizar.reconfigurationrule.ModelTransformation;
import org.palladiosimulator.simulizar.runconfig.SimuLizarWorkflowConfiguration;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

//TODO Refactor to QVTOTransformationJob
public class QVToReconfigurationManager implements IQVToReconfigurationManager {

	private static final String SUPPORTED_TRANSFORMATION_FILE_EXTENSION = ".qvto";

	private QVTOReconfigurator reconfigurator;

	private final List<ModelTransformation<? extends Object>> transformations = Lists.newArrayList();
	private final List<Resource> additonalModelsToTransform = Lists.newArrayList();

	public static QVToReconfigurationManager managerInstance = null;

	public static QVToReconfigurationManager create(String qvtoFilePath) {
		if (managerInstance == null) {
			managerInstance = new QVToReconfigurationManager(qvtoFilePath);
		} else {
			// TODO logging
		}
		return managerInstance;
	}

	private QVToReconfigurationManager(String qvtoFilePath) {
		this.reconfigurator = new QVTOReconfigurator(null, null);
		this.loadTransformations(qvtoFilePath);
	}

	private void loadTransformations(String qvtoFilePath) {
		URI[] qvtoFiles = FileHelper.getURIs(qvtoFilePath, SUPPORTED_TRANSFORMATION_FILE_EXTENSION);
		if (qvtoFiles.length == 0) {
			// TODO exception handling
			throw new RuntimeException("There are no qvto transformation specified.");
		}
		ModelTransformationCache transformationCache = new ModelTransformationCache(qvtoFiles);
		transformationCache.getAll().forEach(t -> this.transformations.add(t));
	}

	@Override
	public List<QVToReconfiguration> loadReconfigurations() {
		return transformations.stream().filter(each -> each instanceof QvtoModelTransformation)
				.map(each -> QVToReconfiguration.of((QvtoModelTransformation) each)).collect(Collectors.toList());
	}

	@Override
	public QVTOReconfigurator getReconfigurator(IExperimentProvider experimentProvider) {
		reconfigurator.setPCMPartitionManager(getPartitionManager(experimentProvider));
		return reconfigurator;
	}

	private PCMPartitionManager getPartitionManager(IExperimentProvider experimentProvider) {
		PCMResourceSetPartition partition = experimentProvider.getExperimentRunner().getWorkingPartition();
		for (Resource each : additonalModelsToTransform) {
			partition.getResourceSet().getResources().add(each);
		}
		
		MDSDBlackboard blackboard = new MDSDBlackboard();
		blackboard.addPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID, partition);
		return new PCMPartitionManager(blackboard, createNewConfig(experimentProvider));
	}

	private SimuLizarWorkflowConfiguration createNewConfig(IExperimentProvider experimentProvider) {
		Optional<MonitorRepository> monitorRepo = Optional.empty();
		List<EObject> result = experimentProvider.getExperimentRunner().getWorkingPartition()
				.getElement(MonitorRepositoryPackage.eINSTANCE.getMonitorRepository());
		if (result.isEmpty() == false) {
			monitorRepo = Optional.of(MonitorRepository.class.cast(result.get(0)));
		}

		SimuLizarWorkflowConfiguration config = new SimuLizarWorkflowConfiguration(Maps.newHashMap());
		if (monitorRepo.isPresent()) {
			String monitorRepoFile = monitorRepo.get().eResource().getURI().toFileString();
			config.setMonitorRepositoryFile(monitorRepoFile);
		} else {
			config.setMonitorRepositoryFile(null);
		}
		return config;
	}

	@Override
	public void resetReconfigurator() {
		reconfigurator = new QVTOReconfigurator(null, null);
	}

	@Override
	public void addModelsToTransform(Resource modelsToTransform) {
		additonalModelsToTransform.add(modelsToTransform);
	}

}
