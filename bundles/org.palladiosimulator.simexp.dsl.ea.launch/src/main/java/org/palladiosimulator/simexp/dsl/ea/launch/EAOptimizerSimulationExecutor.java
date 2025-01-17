package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class EAOptimizerSimulationExecutor implements SimulationExecutor, IEAEvolutionStatusReceiver {
    private static final Logger LOGGER = Logger.getLogger(EAOptimizerSimulationExecutor.class);

    private final Smodel smodel;
    private final IDisposeableEAFitnessEvaluator fitnessEvaluator;

    private Pair<Double, List<OptimizableValue<?>>> fittest;

    public EAOptimizerSimulationExecutor(Smodel smodel, IDisposeableEAFitnessEvaluator fitnessEvaluator) {
        this.smodel = smodel;
        this.fitnessEvaluator = fitnessEvaluator;
    }

    @Override
    public void dispose() {
        fitnessEvaluator.dispose();
    }

    @Override
    public String getPolicyId() {
        return String.format("EA-%s", smodel.getModelName());
    }

    @Override
    public SimulationResult evaluate() {
        double totalReward = 0.0;
        List<OptimizableValue<?>> optimizables = Collections.emptyList();
        if (fittest != null) {
            totalReward = fittest.getLeft();
            optimizables = fittest.getRight();
        }
        String description = String.format("total fittest individual of policy %s (%s)", getPolicyId(),
                asString(optimizables));

        LOGGER.info("***********************************************************************");
        LOGGER.info(String.format("The %s has an reward of %s", description, totalReward));
        LOGGER.info("Optimizable values:");
        for (OptimizableValue<?> ov : optimizables) {
            LOGGER.info(String.format("- %s: %s", ov.getOptimizable()
                .getName(), ov.getValue()));
        }
        LOGGER.info("***********************************************************************");

        return new SimulationResult(totalReward, description);
    }

    @Override
    public void execute() {
        EAOptimizerFactory optimizerFactory = new EAOptimizerFactory();
        IEAConfig eaConfig = new EAConfig();
        IEAOptimizer optimizer = optimizerFactory.create(eaConfig);
        runOptimization(optimizer);
    }

    private void runOptimization(IEAOptimizer optimizer) {
        IOptimizableProvider optimizableProvider = new OptimizableProvider(smodel);
        LOGGER.info("EA optimization running...");
        optimizer.optimize(optimizableProvider, fitnessEvaluator, this);
        LOGGER.info("EA optimization finished...");
    }

    @Override
    public synchronized void reportStatus(List<OptimizableValue<?>> optimizableValues, double fitness) {
        LOGGER.info(String.format("received fitness status for: %s = %s", asString(optimizableValues), fitness));

        if (fittest == null) {
            fittest = new ImmutablePair<>(fitness, optimizableValues);
        }
        if (fitness > fittest.getLeft()) {
            fittest = new ImmutablePair<>(fitness, optimizableValues);
        }
    }

    private String asString(List<OptimizableValue<?>> optimizableValues) {
        List<String> entries = new ArrayList<>();
        for (OptimizableValue<?> ov : optimizableValues) {
            entries.add(String.format("%s: %s", ov.getOptimizable()
                .getName(), ov.getValue()));
        }
        return StringUtils.join(entries, ",");
    }

}
