package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class EAOptimizerSimulationExecutor implements SimulationExecutor, IEAEvolutionStatusReceiver {
    protected static final Logger LOGGER = Logger.getLogger(EAOptimizerSimulationExecutor.class);

    private final Smodel smodel;

    public EAOptimizerSimulationExecutor(Smodel smodel) {
        this.smodel = smodel;
    }

    @Override
    public String getPolicyId() {
        return String.format("EA-%s", smodel.getModelName());
    }

    @Override
    public void evaluate() {
        // TODO:
        double totalReward = 0.0;
        LOGGER.info("***********************************************************************");
        LOGGER.info(
                String.format("The fittest individual of policy %1s has an reward of %2s", getPolicyId(), totalReward));
        // TODO: dump optimization values
        LOGGER.info("***********************************************************************");
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
        IEAFitnessEvaluator fitnessEvaluator = new EAFitnessEvaluator();

        try {
            LOGGER.info("EA optimization running...");
            optimizer.optimize(optimizableProvider, fitnessEvaluator, this);
            LOGGER.info("EA optimization finished...");
        } catch (Exception e) {
            LOGGER.info("Failure during EA optimization", e);
        }
    }

    @Override
    public void reportStatus(List<OptimizableValue<?>> optimizableValues, double fitness) {
        // TODO Auto-generated method stub

    }

}