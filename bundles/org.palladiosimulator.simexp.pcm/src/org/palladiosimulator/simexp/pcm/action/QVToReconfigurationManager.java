package org.palladiosimulator.simexp.pcm.action;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.jobs.LoadPCMModelsIntoBlackboardJob;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;
import org.palladiosimulator.simexp.pcm.action.impl.QVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.impl.QvtoModelTransformationSearch;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.runconfig.SimuLizarWorkflowConfiguration;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

//TODO Refactor to QVTOTransformationJob
public class QVToReconfigurationManager implements IQVToReconfigurationManager {

    private QVTOReconfigurator reconfigurator;

    private final IQVTOModelTransformationLoader qvtoModelTransformationLoader;
    private final QVToReconfigurationProvider qvtoReconfigurationProvider;
    private final List<Resource> additonalModelsToTransform = Lists.newArrayList();

    public QVToReconfigurationManager(String qvtoFilePath) {
        this.reconfigurator = new QVTOReconfigurator(null, null);
        this.qvtoModelTransformationLoader = new QVTOModelTransformationCache(
                new QVTOModelTransformationLoader(qvtoFilePath));
        this.qvtoReconfigurationProvider = new QVToReconfigurationProvider(this, qvtoModelTransformationLoader);
    }

    @Override
    public IQVToReconfigurationProvider getQVToReconfigurationProvider() {
        return qvtoReconfigurationProvider;
    }

    @Override
    public IQVTOModelTransformationSearch getQVTOModelTransformationSearch() {
        return new QvtoModelTransformationSearch(qvtoModelTransformationLoader);
    }

    @Override
    public QVTOReconfigurator getReconfigurator(IExperimentProvider experimentProvider) {
        reconfigurator.setPCMPartitionManager(getPartitionManager(experimentProvider));
        return reconfigurator;
    }

    private PCMPartitionManager getPartitionManager(IExperimentProvider experimentProvider) {
        PCMResourceSetPartition partition = experimentProvider.getExperimentRunner()
            .getWorkingPartition();
        for (Resource each : additonalModelsToTransform) {
            partition.getResourceSet()
                .getResources()
                .add(each);
        }

        MDSDBlackboard blackboard = new MDSDBlackboard();
        blackboard.addPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID, partition);
        return new PCMPartitionManager(blackboard, createNewConfig(experimentProvider));
    }

    private SimuLizarWorkflowConfiguration createNewConfig(IExperimentProvider experimentProvider) {
        Optional<MonitorRepository> monitorRepo = Optional.empty();
        List<EObject> result = experimentProvider.getExperimentRunner()
            .getWorkingPartition()
            .getElement(MonitorRepositoryPackage.eINSTANCE.getMonitorRepository());
        if (result.isEmpty() == false) {
            monitorRepo = Optional.of(MonitorRepository.class.cast(result.get(0)));
        }

        SimuLizarWorkflowConfiguration config = new SimuLizarWorkflowConfiguration(Maps.newHashMap());
        if (monitorRepo.isPresent()) {
            String monitorRepoFile = monitorRepo.get()
                .eResource()
                .getURI()
                .toFileString();
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
