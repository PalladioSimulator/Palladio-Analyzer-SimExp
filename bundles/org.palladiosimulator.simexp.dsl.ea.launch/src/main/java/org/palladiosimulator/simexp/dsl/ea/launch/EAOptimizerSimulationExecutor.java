package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
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

    private EAResult optimizationResult;

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

    private static class EASimulationResult extends SimulationResult {
        private final List<String> detailDescription;

        public EASimulationResult(double totalReward, String rewardDescription, List<String> detailDescription) {
            super(totalReward, rewardDescription);
            this.detailDescription = detailDescription;
        }

        @Override
        public List<String> getDetailDescription() {
            return detailDescription;
        }
    }

    @Override
    public SimulationResult evaluate() {
        double totalReward = 0.0;
        List<OptimizableValue<?>> optimizables = Collections.emptyList();
        if (optimizationResult != null) {
            totalReward = optimizationResult.getFitness();
            optimizables = optimizationResult.getOptimizableValues();
        }
        String description = String.format("fittest individual of policy %s", getPolicyId());
        List<String> detailDescription = new ArrayList<>();
        detailDescription.add("Optimizable values:");
        for (OptimizableValue<?> ov : optimizables) {
            detailDescription.add(String.format("- %s: %s", ov.getOptimizable()
                .getName(), ov.getValue()));
        }
        return new EASimulationResult(totalReward, description, detailDescription);
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
        optimizationResult = optimizer.optimize(optimizableProvider, fitnessEvaluator, this);
        LOGGER.info("EA optimization finished...");
    }

    @Override
    public synchronized void reportStatus(long generation, List<OptimizableValue<?>> optimizableValues,
            double fitness) {
        LOGGER.info(String.format("fitness status in generation %d for: %s = %s", generation,
                asString(optimizableValues), fitness));
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
