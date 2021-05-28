package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenarioRepository;
import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;
import org.palladiosimulator.failuremodel.failuretype.HWCrashFailure;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.state.failure.NodeFailureStateCreator;
import org.palladiosimulator.simexp.state.failure.NodeFailureTypeCreator;

import tools.mdsd.probdist.api.entity.CategoricalValue;

/**
 * 
 * Maps server node failures rates as defined in the environmental dynamics model to the pcm
 * architectural model
 * 
 */
public class ResourceContainerPcmModelChange extends AbstractPcmModelChange {
    
    private static final Logger LOGGER = Logger.getLogger(ResourceContainerPcmModelChange.class);

    private NodeFailureTypeCreator failureTypeCeator;
    private NodeFailureStateCreator failureStateCreator;

    public ResourceContainerPcmModelChange(String attributeName) {
        super(attributeName);

        failureTypeCeator = new NodeFailureTypeCreator();
        failureStateCreator = new NodeFailureStateCreator();
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
            LOGGER.info("Unavailable server node detected ...");

            // create failure model
            FailureTypeRepository failureTypeRepo = failureTypeCeator.create();
            HWCrashFailure hwCrashFailureType = (HWCrashFailure) failureTypeRepo.getFailuretypes()
                .get(0);

            PCMResourceSetPartition pcm = ExperimentProvider.get()
                .getExperimentRunner()
                .getWorkingPartition();
            EList<ResourceContainer> resourceContainers = pcm.getResourceEnvironment()
                .getResourceContainer_ResourceEnvironment();
            FailureScenarioRepository failureScenarioRepo = failureStateCreator.create(resourceContainers, hwCrashFailureType);

            // inject failure model into black board partition
            try {
                ExperimentProvider.get().getExperimentRunner().injectFailureScenario(failureScenarioRepo, failureTypeRepo);
            } catch (IOException e) {
                LOGGER.error("Failed to inject failurescenario models into blackboard partition", e);
            }
        }
    }
    
    
}
