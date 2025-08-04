package org.palladiosimulator.simexp.pcm.examples.hri;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class CustomRobotCognitionLaunchFactory implements ILaunchFactory {

    @Override
    public int canHandle(IWorkflowConfiguration config) {
        SimulatorType simulatorType = config.getSimulatorType();
        if (simulatorType != SimulatorType.CUSTOM) {
            return 0;
        }
        SimulationEngine simulationEngine = config.getSimulationEngine();
        if (simulationEngine != SimulationEngine.PCM) {
            return 0;
        }
        IPCMWorkflowConfiguration pcmWorkflowConfiguration = (IPCMWorkflowConfiguration) config;
        QualityObjective qualityObjective = pcmWorkflowConfiguration.getQualityObjective();
        if (qualityObjective != QualityObjective.RELIABILITY) {
            return 0;
        }

        return 1;

    }

    @Override
    public SimulationExecutor createSimulationExecutor(IWorkflowConfiguration config, String launcherName,
            SimulatedExperienceStoreDescription description, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        IPCMWorkflowConfiguration workflowConfiguration = (IPCMWorkflowConfiguration) config;
        RobotCognitionSimulationExecutorFactory factory = new RobotCognitionSimulationExecutorFactory(
                workflowConfiguration, modelLoaderFactory, new SimulatedExperienceStore<>(accessor, description),
                seedProvider, accessor, resourcePath);
        return factory.create();
    }

}
