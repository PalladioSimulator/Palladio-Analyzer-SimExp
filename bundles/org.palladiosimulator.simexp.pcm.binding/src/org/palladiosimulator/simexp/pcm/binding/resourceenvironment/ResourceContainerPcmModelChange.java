package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenarioRepository;
import org.palladiosimulator.failuremodel.failurescenario.FailurescenarioPackage;
import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;
import org.palladiosimulator.failuremodel.failuretype.FailuretypePackage;
import org.palladiosimulator.failuremodel.failuretype.HWCrashFailure;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.simexp.pcm.perceiption.PerceivedValueConverter;
import org.palladiosimulator.simexp.pcm.state.failure.NodeFailureStateCreator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.entity.CategoricalValue;

/**
 * 
 * Maps server node failures rates as defined in the environmental dynamics model to the pcm
 * architectural model
 * 
 */
public class ResourceContainerPcmModelChange<V> extends AbstractPcmModelChange<V> {

    private static final Logger LOGGER = Logger.getLogger(ResourceContainerPcmModelChange.class);

    private NodeFailureStateCreator failureStateCreator;
    private final IExperimentProvider experimentProvider;

    public ResourceContainerPcmModelChange(String attributeName, PerceivedValueConverter perceivedValueConverter,
            IExperimentProvider experimentProvider) {
        super(attributeName, perceivedValueConverter);
        failureStateCreator = new NodeFailureStateCreator();
        this.experimentProvider = experimentProvider;
    }

    @Override
    void applyChange(CategoricalValue value) {
        /**
         * note: The current implementation assumes that, for each trajectory, a new experiment
         * runner is created, i.e. if a new experiment is started, the pcm model must be resetted to
         * the original state; thus we can not pass the experiment runner as constructor param,
         * because it will be continuously updated during the various experiment runs; if we would
         * pass is as constructor param, we would not be able to get the latest updated state of the
         * pcm model: Instead we would always work on a stale state of the pcm model
         */

        // node unavailable
        if (StringUtils.equals("unavailable", value.get())) {
            PCMResourceSetPartition pcm = experimentProvider.getExperimentRunner()
                .getWorkingPartition();
            EList<ResourceContainer> resourceContainers = pcm.getResourceEnvironment()
                .getResourceContainer_ResourceEnvironment();
            List<ResourceContainer> failedResourceContainers = filterFailedResourceContainer(resourceContainers,
                    getPcmAttributeName());
            LOGGER.info(String.format("Unavailable server node detected: %s",
                    debugMessageFailedServerNodes(failedResourceContainers)));

            // lookup failure model from blackboard partition
            ResourceSetPartition plainPartition = experimentProvider.getExperimentRunner()
                .getPlainWorkingPartition();
            FailureScenarioRepository failureScenarioRepo = (FailureScenarioRepository) plainPartition
                .getElement(FailurescenarioPackage.eINSTANCE.getFailureScenarioRepository())
                .get(0);
            ;
            FailureTypeRepository failureTypeRepo = (FailureTypeRepository) plainPartition
                .getElement(FailuretypePackage.eINSTANCE.getFailureTypeRepository())
                .get(0);
            HWCrashFailure hwCrashFailureType = (HWCrashFailure) failureTypeRepo.getFailuretypes()
                .get(0);

            // add new failure scenario and update failure model in blackboard partition
            failureStateCreator.addScenario(failureScenarioRepo, failedResourceContainers, hwCrashFailureType);
            try {
                experimentProvider.getExperimentRunner()
                    .updateFailureScenario(failureScenarioRepo);
            } catch (IOException e) {
                LOGGER.error("Failed to inject updated failurescenario model into blackboard partition", e);
            }
        }
    }

    private List<ResourceContainer> filterFailedResourceContainer(EList<ResourceContainer> resourceContainers,
            String filterFailedResourceContainerName) {
        List<ResourceContainer> filteredFailedResourceContainters = resourceContainers.stream()
            .filter(rc -> rc.getEntityName()
                .equals(filterFailedResourceContainerName))
            .collect(Collectors.toList());
        return filteredFailedResourceContainters;
    }

    private String debugMessageFailedServerNodes(List<ResourceContainer> resourceContainers) {
        StringBuilder sb = new StringBuilder();
        sb.append("Failed resource container(s) [");

        for (ResourceContainer resourceContainer : resourceContainers) {
            sb.append("(");
            sb.append(resourceContainer.getId());
            sb.append(",");
            sb.append(resourceContainer.getEntityName());
            sb.append("),");
        }
        sb.append("]");
        return sb.toString();
    }

}
