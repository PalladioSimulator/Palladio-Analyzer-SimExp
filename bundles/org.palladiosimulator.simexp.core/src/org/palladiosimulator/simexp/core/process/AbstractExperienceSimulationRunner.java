package org.palladiosimulator.simexp.core.process;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public abstract class AbstractExperienceSimulationRunner<A> implements ExperienceSimulationRunner, Initializable {

    protected static final Logger LOGGER = Logger.getLogger(ExperienceSimulationRunner.class);

    @Override
    public void initialize() {
        LOGGER.info("Initialize experience simulator");
        doInitialize();
        LOGGER.info("Initialized experience simulator");
    }

    @Override
    public final void simulate(State state) {
        LOGGER.info(String.format("Execute pre-simulation hook for state '%s'", state.toString()));
        preSimulate(state);
        LOGGER.info(String.format("Executed pre-simulation hook for state '%s'", state.toString()));

        LOGGER.info(String.format("Do simulation for state '%s'", state.toString()));
        doSimulate(state);
        LOGGER.info(String.format("Done simulation for state '%s'", state.toString()));

        LOGGER.info(String.format("Execute post-simulation hook for state '%s'", state.toString()));
        postSimulate(state);
        LOGGER.info(String.format("Executed post-simulation hook for state '%s'", state.toString()));
    }

    protected void preSimulate(State state) {
        /** default - nothing to do here */
    }

    protected abstract void doSimulate(State state);

    protected void postSimulate(State state) {
        /** default - nothing to do here */
    }

    protected void doInitialize() {
        /**
         * default - nothing to do here
         * 
         * here any behavior should be implemented that is required to initialize the experience
         * simulator
         * 
         */
    }

}
