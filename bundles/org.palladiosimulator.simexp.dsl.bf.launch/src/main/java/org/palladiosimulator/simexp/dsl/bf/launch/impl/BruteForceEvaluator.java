package org.palladiosimulator.simexp.dsl.bf.launch.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.bf.launch.BruteForceResult;
import org.palladiosimulator.simexp.dsl.dispatcher.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.IResultIdentificator;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IndividualResult;
import org.palladiosimulator.simexp.dsl.dispatcher.util.PowerUtil;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public class BruteForceEvaluator {
    private final static Logger LOGGER = Logger.getLogger(BruteForceEvaluator.class);

    public BruteForceResult evaluate(IOptimizableProvider optimizableProvider, IRewardEvaluator evaluator)
            throws IOException, InterruptedException, ExecutionException {
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
        LOGGER.info(String.format("problem space: %s", df.format(overallPower)));

        OptimizableValuesIterator optimizableValuesIterator = new OptimizableValuesIterator(optimizables,
                expressionCalculator);
        List<IndividualResult> evaluatedIndividuals = new ArrayList<>();
        while (optimizableValuesIterator.hasNext()) {
            List<OptimizableValue<?>> optimizableValues = optimizableValuesIterator.next();
            Future<Optional<Double>> rewardFuture = evaluator.calcReward(optimizableValues);
            Optional<Double> optionalReward = rewardFuture.get();

            IResultIdentificator resultIdentificator = evaluator.getFitnessResultIdentificator();
            IndividualResult result = buildIndividualResult(optionalReward.get(), optimizableValues,
                    resultIdentificator);
            evaluatedIndividuals.add(result);
        }

        // TODO:
        IndividualResult bestIndividual = evaluatedIndividuals.get(0);
        BruteForceResult bruteForceResult = new BruteForceResult(bestIndividual, evaluatedIndividuals);
        return bruteForceResult;
    }

    private IndividualResult buildIndividualResult(double reward, List<OptimizableValue<?>> optimizableValues,
            IResultIdentificator resultIdentificator) {
        Optional<String> identificator = resultIdentificator.getIdentificator(optimizableValues);
        return new IndividualResult(reward, optimizableValues, identificator.orElse("n/a"));
    }
}
