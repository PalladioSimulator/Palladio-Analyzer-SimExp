package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.commons.constants.model.RandomSampleType;
import org.palladiosimulator.simexp.dsl.dispatcher.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.IResultIdentificator;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;
import org.palladiosimulator.simexp.dsl.dispatcher.util.PowerUtil;
import org.palladiosimulator.simexp.dsl.dispatcher.util.RewardFormater;
import org.palladiosimulator.simexp.dsl.re.launch.RandomEvaluationResult;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.pcm.config.IRandomEvaluationConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class RandomEvaluationEvaluator {
    private final static Logger LOGGER = Logger.getLogger(RandomEvaluationEvaluator.class);

    private final IRandomEvaluationConfiguration configuration;
    private final Optional<ISeedProvider> seedProvider;

    private static class Result {
        public final List<OptimizableValue<?>> optimizableValues;
        public final Optional<Double> optionalReward;

        public Result(List<OptimizableValue<?>> optimizableValues, Optional<Double> optionalReward) {
            this.optimizableValues = optimizableValues;
            this.optionalReward = optionalReward;
        }
    }

    public RandomEvaluationEvaluator(IRandomEvaluationConfiguration configuration,
            Optional<ISeedProvider> seedProvider) {
        this.configuration = configuration;
        this.seedProvider = seedProvider;
    }

    public RandomEvaluationResult evaluate(IOptimizableProvider optimizableProvider, IRewardEvaluator evaluator,
            RewardFormater rewardUtil) throws IOException, InterruptedException, ExecutionException {
        int availableProcessors = Runtime.getRuntime()
            .availableProcessors();
        LOGGER.info(String.format("available local processors:   %d", availableProcessors));
        LOGGER.info(String.format("available launch parallelism: %d", evaluator.getParallelism()));
        int parallelism = Math.max(availableProcessors, evaluator.getParallelism());
        LOGGER.info(String.format("the evaluator has an parallelism of: %d", parallelism));

        IExpressionCalculator expressionCalculator = optimizableProvider.getExpressionCalculator();
        Collection<Optimizable> optimizables = optimizableProvider.getOptimizables();
        PowerUtil powerUtil = new PowerUtil(expressionCalculator);
        BigInteger problemSpaceSize = powerUtil.calculateComplexity(optimizables);
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        LOGGER.info(String.format("problem space size: %s", df.format(problemSpaceSize)));

        IResultIdentificator resultIdentificator = evaluator.getFitnessResultIdentificator();
        IndividualResult bestConfiguration = null;
        List<IndividualResult> evaluatedConfigurations = new ArrayList<>();

        double errorReward = configuration.getErrorReward();
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
        CompletionService<Result> completed = new ExecutorCompletionService<>(executor);
        try {
            int submitted = submitTasks(optimizables, expressionCalculator, evaluator, completed, problemSpaceSize);
            LOGGER.info(String.format("submitted tasks: %d", submitted));

            for (int i = 0; i < submitted; i++) {
                Future<Result> future = completed.take();
                Result evaluationResult = future.get();

                Optional<Double> optionalReward = evaluationResult.optionalReward;
                double reward = optionalReward.isPresent() ? optionalReward.get() : errorReward;
                reward = rewardUtil.round(reward);

                IndividualResult result = buildIndividualResult(reward, evaluationResult.optimizableValues,
                        resultIdentificator);
                evaluatedConfigurations.add(result);
                if (bestConfiguration == null) {
                    bestConfiguration = result;
                } else {
                    if (reward < bestConfiguration.getFitness()) {
                        bestConfiguration = result;
                    }
                }
            }
            LOGGER.info("all submitted tasks recived");
        } finally {
            executor.shutdown();
        }

        RandomEvaluationResult randomEvaluationResult = new RandomEvaluationResult(problemSpaceSize, bestConfiguration,
                evaluatedConfigurations);
        return randomEvaluationResult;
    }

    private int submitTasks(Collection<Optimizable> optimizables, IExpressionCalculator expressionCalculator,
            IRewardEvaluator evaluator, CompletionService<Result> completed, BigInteger problemSpaceSize) {
        Iterator<List<OptimizableValue<?>>> optimizableValuesIterator = createIterator(optimizables,
                expressionCalculator, problemSpaceSize);
        int submitted = 0;
        while (optimizableValuesIterator.hasNext()) {
            List<OptimizableValue<?>> optimizableValues = optimizableValuesIterator.next();
            completed.submit(new Callable<RandomEvaluationEvaluator.Result>() {

                @Override
                public Result call() throws Exception {
                    Future<Optional<Double>> rewardFuture = evaluator.calcReward(optimizableValues);
                    Optional<Double> optionalReward = rewardFuture.get();
                    return new Result(optimizableValues, optionalReward);
                }
            });
            submitted++;
        }
        return submitted;
    }

    private Iterator<List<OptimizableValue<?>>> createIterator(Collection<Optimizable> optimizables,
            IExpressionCalculator expressionCalculator, BigInteger problemSpaceSize) {
        OptimizableValuesIterator optimizableValuesIterator = new OptimizableValuesIterator(optimizables,
                expressionCalculator);
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);

        RandomSampleType randomSampleType = configuration.getRandomSampleType();
        if (randomSampleType == RandomSampleType.COUNT) {
            int randomSampleCount = configuration.getRandomSampleCount()
                .get();
            LOGGER.info(String.format("random sample count: %s", df.format(randomSampleCount)));
            return new SampleCountIterator<>(seedProvider, optimizableValuesIterator, problemSpaceSize,
                    BigInteger.valueOf(randomSampleCount));
        }
        if (randomSampleType == RandomSampleType.RATIO) {
            double randomSampleRatio = configuration.getRandomSampleRatio()
                .get();
            double randomSamplePercent = randomSampleRatio * 100;
            LOGGER.info(String.format("random sample ratio: %s%%", df.format(randomSamplePercent)));
            return new SampleRatioIterator<>(seedProvider, optimizableValuesIterator, problemSpaceSize,
                    randomSamplePercent);
        }
        throw new RuntimeException("invalid random sample type: " + randomSampleType);
    }

    private IndividualResult buildIndividualResult(double reward, List<OptimizableValue<?>> optimizableValues,
            IResultIdentificator resultIdentificator) {
        Optional<String> identificator = resultIdentificator.getIdentificator(optimizableValues);
        return new IndividualResult(reward, optimizableValues, identificator.orElse("n/a"));
    }
}
