package org.palladiosimulator.simexp.dsl.bf.launch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.RewardType;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.dsl.bf.launch.impl.BruteForceEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IDisposeableRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;
import org.palladiosimulator.simexp.dsl.dispatcher.util.IRewardFormater;
import org.palladiosimulator.simexp.dsl.dispatcher.util.OptimizableProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.config.IWorkflowConfiguration;

public class BruteForceSimulationExecutor implements SimulationExecutor {
    private static final Logger LOGGER = Logger.getLogger(BruteForceSimulationExecutor.class);

    private final Smodel smodel;
    private final IDisposeableRewardEvaluator fitnessEvaluator;
    private final IWorkflowConfiguration configuration;
    private final Path resourcePath;
    private final IPrecisionProvider precisionProvider;
    private final IRewardFormater rewardFormater;

    private BruteForceResult bruteForceResult;

    public BruteForceSimulationExecutor(Smodel smodel, IDisposeableRewardEvaluator fitnessEvaluator,
            IWorkflowConfiguration configuration, IPrecisionProvider precisionProvider, IRewardFormater rewardFormater,
            Path resourcePath) {
        this.smodel = smodel;
        this.fitnessEvaluator = fitnessEvaluator;
        this.configuration = configuration;
        this.precisionProvider = precisionProvider;
        this.rewardFormater = rewardFormater;
        this.resourcePath = resourcePath;
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getPolicyId() {
        return String.format("BF-%s", smodel.getModelName());
    }

    private static class BruteForceSimulationResult extends SimulationResult {
        private final List<String> detailDescription;

        public BruteForceSimulationResult(double totalReward, RewardType rewardType,
                QualityMeasurements qualityMeasurements, String rewardDescription, List<String> detailDescription) {
            super(totalReward, rewardType, qualityMeasurements, rewardDescription);
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
        // List<IndividualParetoResult> paretoFront = Collections.emptyList();
        List<IndividualResult> finalPopulation = Collections.emptyList();
        List<IndividualResult> initialPopulation = Collections.emptyList();
        /*
         * if (optimizationResult != null) { IndividualResult fittest =
         * optimizationResult.getFittest(); totalReward = fittest.getFitness();
         * bestOptimizableValues = fittest.getOptimizableValues(); paretoFront =
         * optimizationResult.getParetoFront(); initialPopulation =
         * optimizationResult.getInitialPopulation(); finalPopulation =
         * optimizationResult.getFinalPopulation(); }
         */
        String description = String.format("best individual of policy %s", getPolicyId());
        List<String> detailDescription = new ArrayList<>();
        detailDescription.add("Optimal values of the best individual:");
        detailDescription.addAll(formatOptimizables(bestOptimizableValues));

        /*
         * detailDescription.add(String.format("Pareto optimal values %d:", paretoFront.size()));
         * for (ListIterator<IndividualParetoResult> it = paretoFront.listIterator(); it.hasNext();)
         * { IndividualParetoResult individualParetoResult = it.next(); IndividualResult
         * individualResult = individualParetoResult.getIndividualResult();
         * List<OptimizableValue<?>> optimizables = individualResult.getOptimizableValues();
         * detailDescription.add(String.format("- #%d", it.previousIndex()));
         * detailDescription.addAll(formatOptimizables(optimizables)); }
         */

        List<IndividualResult> uniqueFinalPopulation = finalPopulation.stream()
            .filter(distinctByKey(IndividualResult::getOptimizableValues))
            .toList();

        detailDescription.add(String.format("The final population has %d (%d unique) individuals:",
                finalPopulation.size(), uniqueFinalPopulation.size()));
        for (IndividualResult individual : uniqueFinalPopulation) {
            detailDescription.add(String.format("- fitness %s", rewardFormater.asString(individual.getFitness())));
            detailDescription.addAll(formatOptimizables(individual.getOptimizableValues()));
        }

        return new BruteForceSimulationResult(totalReward, configuration.getRewardType(), qualityMeasurements,
                description, detailDescription);
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
        /*
         * EAOptimizerFactory optimizerFactory = new EAOptimizerFactory(); IEAConfig eaConfig = new
         * EAConfig(precisionProvider, configuration.getSeedProvider(), configuration); IEAOptimizer
         * optimizer = optimizerFactory.create(eaConfig);
         */
        runOptimization();
    }

    private void runOptimization() {
        final IOptimizableProvider optimizableProvider = new OptimizableProvider(smodel);
        LOGGER.info("Brute force initialization");
        fitnessEvaluator.evaluate(new IDisposeableRewardEvaluator.EvaluatorClient() {

            @Override
            public void process(IRewardEvaluator evaluator) {
                // try (EAEvolutionStatusReceiverDispatcher eaEvolutionStatusReceiverDispatcher =
                // new EAEvolutionStatusReceiverDispatcher()) {
                try {
                    /*
                     * Path generationsPath = resourcePath.resolve("generations");
                     * Files.createDirectories(generationsPath);
                     * eaEvolutionStatusReceiverDispatcher.addReceiver(new
                     * GenerationLogger(rewardFormater));
                     * eaEvolutionStatusReceiverDispatcher.addReceiver(new
                     * GenerationCSVWriter(resourcePath));
                     * eaEvolutionStatusReceiverDispatcher.addReceiver(new
                     * GenerationJsonWriter(resourcePath));
                     * eaEvolutionStatusReceiverDispatcher.addReceiver(new
                     * GenerationDumper(generationsPath)); IQualityAttributeProvider
                     * qualityAttributeProvider = evaluator.getQualityAttributeProvider();
                     * eaEvolutionStatusReceiverDispatcher.addReceiver(new
                     * GenerationParetoFrontBuilder(generationsPath, qualityAttributeProvider,
                     * precisionProvider));
                     */

                    LOGGER.info("Brute force start");
                    BruteForceEvaluator bruteForceEvaluator = new BruteForceEvaluator();
                    bruteForceResult = bruteForceEvaluator.evaluate(optimizableProvider, evaluator);
                    LOGGER.info("Brute force end");
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        });
    }
}
