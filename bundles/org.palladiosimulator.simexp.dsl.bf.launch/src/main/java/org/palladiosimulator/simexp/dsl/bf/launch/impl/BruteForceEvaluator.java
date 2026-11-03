package org.palladiosimulator.simexp.dsl.bf.launch.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.bf.launch.BruteForceResult;
import org.palladiosimulator.simexp.dsl.dispatcher.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public class BruteForceEvaluator {
    private final static Logger LOGGER = Logger.getLogger(BruteForceEvaluator.class);

    public BruteForceResult evaluate(IOptimizableProvider optimizableProvider, IRewardEvaluator evaluator) {
        int availableProcessors = Runtime.getRuntime()
            .availableProcessors();
        LOGGER.info(String.format("available local processors:   %d", availableProcessors));
        LOGGER.info(String.format("available launch parallelism: %d", evaluator.getParallelism()));
        int parallelism = Math.max(availableProcessors, evaluator.getParallelism());
        LOGGER.info(String.format("the evaluator has an parallelism of: %d", parallelism));

        IExpressionCalculator expressionCalculator = optimizableProvider.getExpressionCalculator();
        Collection<Optimizable> optimizables = optimizableProvider.getOptimizables();
        /*
         * PowerUtil powerUtil = new PowerUtil(expressionCalculator); BigInteger overallPower =
         * powerUtil.calculateComplexity(optimizables); DecimalFormat df = new DecimalFormat();
         * df.setGroupingUsed(true); LOGGER.info(String.format("optimizeable search space: %s",
         * df.format(overallPower)));
         */

        // TODO Auto-generated method stub
        return null;
    }

}
