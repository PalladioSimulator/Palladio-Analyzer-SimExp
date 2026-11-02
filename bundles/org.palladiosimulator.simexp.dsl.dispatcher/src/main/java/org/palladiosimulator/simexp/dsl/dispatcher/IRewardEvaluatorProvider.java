package org.palladiosimulator.simexp.dsl.dispatcher;

import java.nio.file.Path;
import java.util.Optional;

import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.dsl.dispatcher.util.IRewardFormater;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IRewardEvaluatorProvider {
    String getName();

    IDisposeableRewardEvaluator createEvaluator(IWorkflowConfiguration config, String launcherName,
            SimulatedExperienceStoreDescription description, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, IRewardFormater rewardFormater, Path resourcePath);
}
