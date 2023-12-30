package org.palladiosimulator.simexp.core.process;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

public abstract class AbstractExperienceSimulationRunner<S, A>
        implements ExperienceSimulationRunner<S, A>, Initializable {

    protected static final Logger LOGGER = Logger.getLogger(ExperienceSimulationRunner.class);

    @Override
    public void initialize() {
        LOGGER.info("Initialize experience simulator");
        doInitialize();
        LOGGER.info("Initialized experience simulator");
    }

    @Override
    public final void simulate(SelfAdaptiveSystemState<S, A> sasState) {
        LOGGER.info(String.format("Execute pre-simulation hook for state '%s'", sasState.toString()));
        preSimulate(sasState);
        LOGGER.info(String.format("Executed pre-simulation hook for state '%s'", sasState.toString()));

        LOGGER.info(String.format("Do simulation for state '%s'", sasState.toString()));
        doSimulate(sasState);
        LOGGER.info(String.format("Done simulation for state '%s'", sasState.toString()));

        LOGGER.info(String.format("Execute post-simulation hook for state '%s'", sasState.toString()));
        postSimulate(sasState);
        LOGGER.info(String.format("Executed post-simulation hook for state '%s'", sasState.toString()));
    }

    protected void preSimulate(SelfAdaptiveSystemState<S, A> sasState) {
        /** default - nothing to do here */
    }

    protected abstract void doSimulate(SelfAdaptiveSystemState<S, A> sasState);

    protected void postSimulate(SelfAdaptiveSystemState<S, A> sasState) {
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
