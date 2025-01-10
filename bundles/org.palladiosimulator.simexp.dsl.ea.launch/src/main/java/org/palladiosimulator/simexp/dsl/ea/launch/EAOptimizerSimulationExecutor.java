package org.palladiosimulator.simexp.dsl.ea.launch;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class EAOptimizerSimulationExecutor implements SimulationExecutor {
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
        // TODO Auto-generated method stub

    }

}
