package org.palladiosimulator.simexp.dsl.bf.launch.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import org.palladiosimulator.simexp.dsl.bf.launch.BruteForceResult;
import org.palladiosimulator.simexp.dsl.dispatcher.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.IResultIdentificator;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;
import org.palladiosimulator.simexp.dsl.dispatcher.util.PowerUtil;
import org.palladiosimulator.simexp.dsl.dispatcher.util.RewardFormater;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public class BruteForceEvaluator {
    private final static Logger LOGGER = Logger.getLogger(BruteForceEvaluator.class);

    private final double penaltyForInvalids;

    private static class Result {
        public final List<OptimizableValue<?>> optimizableValues;
        public final Optional<Double> optionalReward;

        public Result(List<OptimizableValue<?>> optimizableValues, Optional<Double> optionalReward) {
            this.optimizableValues = optimizableValues;
            this.optionalReward = optionalReward;
        }
    }

    public BruteForceEvaluator(double penaltyForInvalids) {
        this.penaltyForInvalids = penaltyForInvalids;
    }

    public BruteForceResult evaluate(IOptimizableProvider optimizableProvider, IRewardEvaluator evaluator,
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
        BigInteger overallPower = powerUtil.calculateComplexity(optimizables);
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        LOGGER.info(String.format("problem space size: %s", df.format(overallPower)));

        IResultIdentificator resultIdentificator = evaluator.getFitnessResultIdentificator();
        IndividualResult bestIndividual = null;
        List<IndividualResult> evaluatedIndividuals = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
        CompletionService<Result> completed = new ExecutorCompletionService<>(executor);
        try {
            OptimizableValuesIterator optimizableValuesIterator = new OptimizableValuesIterator(optimizables,
                    expressionCalculator);
            int submitted = 0;
            while (optimizableValuesIterator.hasNext()) {
                List<OptimizableValue<?>> optimizableValues = optimizableValuesIterator.next();
                completed.submit(new Callable<BruteForceEvaluator.Result>() {

                    @Override
                    public Result call() throws Exception {
                        Future<Optional<Double>> rewardFuture = evaluator.calcReward(optimizableValues);
                        Optional<Double> optionalReward = rewardFuture.get();
                        return new Result(optimizableValues, optionalReward);
                    }
                });
                submitted++;
            }
            LOGGER.info(String.format("submitted tasks: %d", submitted));

            for (int i = 0; i < submitted; i++) {
                Future<Result> future = completed.take();
                Result evaluationResult = future.get();

                Optional<Double> optionalReward = evaluationResult.optionalReward;
                double reward = optionalReward.isPresent() ? optionalReward.get() : penaltyForInvalids;
                reward = rewardUtil.round(reward);

                LOGGER.info(String.format("got task result: reward: %f", reward));
                IndividualResult result = buildIndividualResult(reward, evaluationResult.optimizableValues,
                        resultIdentificator);
                evaluatedIndividuals.add(result);
                if (bestIndividual == null) {
                    bestIndividual = result;
                } else {
                    if (reward < bestIndividual.getFitness()) {
                        bestIndividual = result;
                    }
                }
            }
            LOGGER.info("all submitted tasks recived");
        } finally {
            executor.shutdown();
        }

        BruteForceResult bruteForceResult = new BruteForceResult(overallPower, bestIndividual, evaluatedIndividuals);
        return bruteForceResult;
    }

    private IndividualResult buildIndividualResult(double reward, List<OptimizableValue<?>> optimizableValues,
            IResultIdentificator resultIdentificator) {
        Optional<String> identificator = resultIdentificator.getIdentificator(optimizableValues);
        return new IndividualResult(reward, optimizableValues, identificator.orElse("n/a"));
    }
}
