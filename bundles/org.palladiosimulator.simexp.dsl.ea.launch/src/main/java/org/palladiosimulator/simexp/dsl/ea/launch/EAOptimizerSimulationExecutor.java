package org.palladiosimulator.simexp.dsl.ea.launch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.launch.dispatcher.EAEvolutionStatusReceiverDispatcher;
import org.palladiosimulator.simexp.dsl.ea.launch.log.GenerationCSVWriter;
import org.palladiosimulator.simexp.dsl.ea.launch.log.GenerationLogger;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.config.IEvolutionaryAlgorithmWorkflowConfiguration;

public class EAOptimizerSimulationExecutor implements SimulationExecutor {
    private static final Logger LOGGER = Logger.getLogger(EAOptimizerSimulationExecutor.class);

    private final Smodel smodel;
    private final IDisposeableEAFitnessEvaluator fitnessEvaluator;
    private final IEvolutionaryAlgorithmWorkflowConfiguration configuration;
    private final EAEvolutionStatusReceiverDispatcher eaEvolutionStatusReceiverDispatcher;

    private EAResult optimizationResult;

    public EAOptimizerSimulationExecutor(Smodel smodel, IDisposeableEAFitnessEvaluator fitnessEvaluator,
            IEvolutionaryAlgorithmWorkflowConfiguration configuration, Path resourcePath) {
        this.smodel = smodel;
        this.fitnessEvaluator = fitnessEvaluator;
        this.configuration = configuration;
        this.eaEvolutionStatusReceiverDispatcher = new EAEvolutionStatusReceiverDispatcher();
        eaEvolutionStatusReceiverDispatcher.addReceiver(new GenerationLogger());
        eaEvolutionStatusReceiverDispatcher.addReceiver(new GenerationCSVWriter(resourcePath));
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getPolicyId() {
        return String.format("EA-%s", smodel.getModelName());
    }

    private static class EASimulationResult extends SimulationResult {
        private final List<String> detailDescription;

        public EASimulationResult(double totalReward, List<Map<String, List<Double>>> qos, String rewardDescription,
                List<String> detailDescription) {
            super(totalReward, qos, rewardDescription);
            this.detailDescription = detailDescription;
        }

        @Override
        public List<String> getDetailDescription() {
            return detailDescription;
        }
    }

    @Override
    public ISimulationResult evaluate() {
        double totalReward = 0.0;
        List<Map<String, List<Double>>> qos = Collections.emptyList();
        List<List<OptimizableValue<?>>> optimizablesList = Collections.emptyList();
        if (optimizationResult != null) {
            totalReward = optimizationResult.getFitness();
            optimizablesList = optimizationResult.getOptimizableValuesList();
        }
        String description = String.format("fittest individual of policy %s", getPolicyId());
        List<String> detailDescription = new ArrayList<>();
        detailDescription.add(String.format("Pareto optimal values %d:", optimizablesList.size()));
        for (final ListIterator<List<OptimizableValue<?>>> it = optimizablesList.listIterator(); it.hasNext();) {
            List<OptimizableValue<?>> optimizables = it.next();
            detailDescription.add(String.format("- #%d", it.previousIndex()));
            for (OptimizableValue<?> ov : optimizables) {
                detailDescription.add(String.format("-- %s: %s", ov.getOptimizable()
                    .getName(), ov.getValue()));
            }
        }
        return new EASimulationResult(totalReward, qos, description, detailDescription);
    }

    @Override
    public void execute() {
        EAOptimizerFactory optimizerFactory = new EAOptimizerFactory();
        IEAConfig eaConfig = new EAConfig(configuration.getSeedProvider(), configuration);
        IEAOptimizer optimizer = optimizerFactory.create(eaConfig);
        runOptimization(optimizer);
    }

    private void runOptimization(IEAOptimizer optimizer) {
        final IOptimizableProvider optimizableProvider = new OptimizableProvider(smodel);
        LOGGER.info("EA optimization initialization");
        fitnessEvaluator.evaluate(new IDisposeableEAFitnessEvaluator.EvaluatorClient() {

            @Override
            public void process(IEAFitnessEvaluator evaluator) {
                LOGGER.info("EA optimization start");
                optimizationResult = optimizer.optimize(optimizableProvider, evaluator,
                        eaEvolutionStatusReceiverDispatcher);
                LOGGER.info("EA optimization end");
            }
        });
    }
}
