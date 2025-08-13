package org.palladiosimulator.simexp.dsl.ea.launch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.launch.dispatcher.EAEvolutionStatusReceiverDispatcher;
import org.palladiosimulator.simexp.dsl.ea.launch.log.GenerationCSVWriter;
import org.palladiosimulator.simexp.dsl.ea.launch.log.GenerationLogger;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.smodel.api.ISmodelConstants;
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

        public EASimulationResult(double totalReward, QualityMeasurements qualityMeasurements, String rewardDescription,
                List<String> detailDescription) {
            super(totalReward, qualityMeasurements, rewardDescription);
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
        QualityMeasurements qualityMeasurements = null;
        List<OptimizableValue<?>> bestOptimizableValues = Collections.emptyList();
        List<List<OptimizableValue<?>>> paretoFrontOptimizableValues = Collections.emptyList();
        List<IndividualResult> finalPopulation = Collections.emptyList();
        if (optimizationResult != null) {
            IndividualResult fittest = optimizationResult.getFittest();
            totalReward = fittest.getFitness();
            bestOptimizableValues = fittest.getOptimizableValues();
            paretoFrontOptimizableValues = optimizationResult.getParetoFrontOptimizableValues();
            finalPopulation = optimizationResult.getFinalPopulation();
        }
        String description = String.format("fittest individual of policy %s", getPolicyId());
        List<String> detailDescription = new ArrayList<>();
        detailDescription.add("Optimal values of the fittest individual:");
        detailDescription.addAll(formatOptimizables(bestOptimizableValues));

        detailDescription.add(String.format("Pareto optimal values %d:", paretoFrontOptimizableValues.size()));
        for (ListIterator<List<OptimizableValue<?>>> it = paretoFrontOptimizableValues.listIterator(); it.hasNext();) {
            List<OptimizableValue<?>> optimizables = it.next();
            detailDescription.add(String.format("- #%d", it.previousIndex()));
            detailDescription.addAll(formatOptimizables(optimizables));
        }

        List<IndividualResult> uniquePopulation = finalPopulation.stream()
            .filter(distinctByKey(IndividualResult::getOptimizableValues))
            .toList();

        detailDescription.add(String.format("The final population has %d/%d unique individuals:",
                uniquePopulation.size(), finalPopulation.size()));
        for (IndividualResult individual : uniquePopulation) {
            detailDescription.add(String.format("- fitness %s", individual.getFitness()));
            detailDescription.addAll(formatOptimizables(individual.getOptimizableValues()));
        }

        return new EASimulationResult(totalReward, qualityMeasurements, description, detailDescription);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private List<String> formatOptimizables(List<OptimizableValue<?>> optimizables) {
        List<String> entries = new ArrayList<>();
        for (OptimizableValue<?> ov : optimizables) {
            entries.add(String.format("-- %s: %s", ov.getOptimizable()
                .getName(), ov.getValue()));
        }
        return entries;
    }

    @Override
    public void execute() {
        EAOptimizerFactory optimizerFactory = new EAOptimizerFactory();
        // TODO: get from SModel
        double epsilon = ISmodelConstants.EPSILON;
        IEAConfig eaConfig = new EAConfig(epsilon, configuration.getSeedProvider(), configuration);
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
