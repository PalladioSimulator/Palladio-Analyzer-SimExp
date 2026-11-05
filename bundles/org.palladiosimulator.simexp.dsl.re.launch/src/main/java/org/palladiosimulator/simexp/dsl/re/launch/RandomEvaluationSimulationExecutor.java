package org.palladiosimulator.simexp.dsl.re.launch;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.commons.constants.model.RewardType;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;
import org.palladiosimulator.simexp.dsl.dispatcher.IDisposeableRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;
import org.palladiosimulator.simexp.dsl.dispatcher.io.JsonResultWriter;
import org.palladiosimulator.simexp.dsl.dispatcher.util.OptimizableProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.util.RewardFormater;
import org.palladiosimulator.simexp.dsl.re.launch.impl.RandomEvaluationEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.config.IRandomEvaluationWorkflowConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class RandomEvaluationSimulationExecutor implements SimulationExecutor {
    private static final Logger LOGGER = Logger.getLogger(RandomEvaluationSimulationExecutor.class);

    private final Smodel smodel;
    private final IDisposeableRewardEvaluator fitnessEvaluator;
    private final IRandomEvaluationWorkflowConfiguration configuration;
    private final RewardFormater rewardFormater;
    private final Optional<ISeedProvider> seedProvider;
    private final Path resourcePath;

    private RandomEvaluationResult randomEvaluationResult;

    public RandomEvaluationSimulationExecutor(Smodel smodel, IDisposeableRewardEvaluator fitnessEvaluator,
            IRandomEvaluationWorkflowConfiguration configuration, RewardFormater rewardFormater,
            Optional<ISeedProvider> seedProvider, Path resourcePath) {
        this.smodel = smodel;
        this.fitnessEvaluator = fitnessEvaluator;
        this.configuration = configuration;
        this.rewardFormater = rewardFormater;
        this.seedProvider = seedProvider;
        this.resourcePath = resourcePath;
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getPolicyId() {
        return String.format("RE-%s", smodel.getModelName());
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
        BigInteger problemSpaceSize = BigInteger.ZERO;
        QualityMeasurements qualityMeasurements = null;
        List<OptimizableValue<?>> bestOptimizableValues = Collections.emptyList();
        List<IndividualResult> evaluatedConfigurations = Collections.emptyList();
        if (randomEvaluationResult != null) {
            IndividualResult best = randomEvaluationResult.getBestConfiguration();
            totalReward = best.getFitness();
            bestOptimizableValues = best.getOptimizableValues();
            problemSpaceSize = randomEvaluationResult.getProblemSpaceSize();
            evaluatedConfigurations = randomEvaluationResult.getEvaluatedConfigurations();
        }

        String description = String.format("best configuration of policy %s", getPolicyId());
        List<String> detailDescription = new ArrayList<>();
        detailDescription.add("Values of the best configuration:");
        detailDescription.addAll(formatOptimizables(bestOptimizableValues));

        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        final float fraction;
        if (BigInteger.ZERO.equals(problemSpaceSize)) {
            fraction = 0;
        } else {
            BigDecimal dividend = BigDecimal.valueOf(evaluatedConfigurations.size())
                .multiply(BigDecimal.valueOf(100));

            BigDecimal divisor = new BigDecimal(problemSpaceSize);
            fraction = dividend.divide(divisor, 4, RoundingMode.HALF_UP)
                .floatValue();
        }
        detailDescription.add("-".repeat(71));
        detailDescription.add(String.format("Evaluated %d of %s (%.2f%%)", evaluatedConfigurations.size(),
                df.format(problemSpaceSize), fraction));

        JsonResultWriter jsonParetoWriter = new JsonResultWriter();
        Path evaluatedConfigurationsFile = resourcePath.resolve("evaluated_configurations.json");
        jsonParetoWriter.storeIndividualResults(evaluatedConfigurationsFile, evaluatedConfigurations);

        return new BruteForceSimulationResult(totalReward, configuration.getRewardType(), qualityMeasurements,
                description, detailDescription);
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
        final IOptimizableProvider optimizableProvider = new OptimizableProvider(smodel);
        LOGGER.info("Random evaluation initialization");
        fitnessEvaluator.evaluate(new IDisposeableRewardEvaluator.EvaluatorClient() {

            @Override
            public void process(IRewardEvaluator evaluator) {
                try {
                    LOGGER.info("Random evaluation start");
                    RandomEvaluationEvaluator bruteForceEvaluator = new RandomEvaluationEvaluator(configuration,
                            seedProvider);
                    randomEvaluationResult = bruteForceEvaluator.evaluate(optimizableProvider, evaluator,
                            rewardFormater);
                    LOGGER.info("Random evaluation end");
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        });
    }
}
