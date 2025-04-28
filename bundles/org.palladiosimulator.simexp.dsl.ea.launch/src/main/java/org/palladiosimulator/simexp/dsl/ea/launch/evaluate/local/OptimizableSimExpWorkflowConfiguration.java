package org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.pcm.modelled.config.IOptimizedConfiguration;
import org.palladiosimulator.simexp.workflow.api.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.ArchitecturalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EnvironmentalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EvolutionaryAlgorithmConfiguration;
import org.palladiosimulator.simexp.workflow.config.MonitorConfiguration;
import org.palladiosimulator.simexp.workflow.config.PrismConfiguration;

public class OptimizableSimExpWorkflowConfiguration extends SimExpWorkflowConfiguration
        implements IOptimizedConfiguration {
    private final List<OptimizableValue<?>> optimizableValues;

    public OptimizableSimExpWorkflowConfiguration(SimExpWorkflowConfiguration configuration,
            List<OptimizableValue<?>> optimizableValues) {
        super(configuration.getSimulatorType(), configuration.getSimulationEngine(), configuration.getRewardType(),
                configuration.getTransformationNames(), configuration.getQualityObjective(),
                new ArchitecturalModelsWorkflowConfiguration(configuration.getAllocationFiles(),
                        configuration.getUsageModelFile(), configuration.getExperimentsURI()
                            .toString(),
                        configuration.getSmodelURI()
                            .toString()),
                configuration.getOptimizationType(), new MonitorConfiguration(configuration.getMonitorRepositoryURI()
                    .toString(), configuration.getMonitorNames()),
                new PrismConfiguration(configuration.getPropertyFiles()
                    .stream()
                    .map(URI::toString)
                    .toList(),
                        configuration.getModuleFiles()
                            .stream()
                            .map(URI::toString)
                            .toList()),
                new EnvironmentalModelsWorkflowConfiguration(configuration.getStaticModelURI()
                    .toString(),
                        configuration.getDynamicModelURI()
                            .toString()),
                configuration.getSimulationParameters(), configuration.getSeedProvider(),
                new EvolutionaryAlgorithmConfiguration(configuration.getPopulationSize(),
                        configuration.getErrorReward(), configuration.getMaxGenerations(),
                        configuration.getSteadyFitness(), configuration.getMutationRate(),
                        configuration.getCrossoverRate(), configuration.getMemoryUsage()),
                configuration.getOptimizedValues());
        this.optimizableValues = optimizableValues;
    }

    @Override
    public List<OptimizableValue<?>> getOptimizableValues() {
        return optimizableValues;
    }
}
