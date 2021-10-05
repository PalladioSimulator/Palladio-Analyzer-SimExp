package org.palladiosimulator.simexp.core.process;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

public abstract class AbstractExperienceSimulationRunner implements ExperienceSimulationRunner, Initializable {
    
    protected static final Logger LOGGER = Logger.getLogger(ExperienceSimulationRunner.class);

    @Override
    public void initialize() {
        LOGGER.info("==== Initialize experience simulator");
        doInitialize();
        LOGGER.info("==== Done. Initialize experience simulator");
    }
    
    @Override
    public final void simulate(SelfAdaptiveSystemState<?> sasState) {
        LOGGER.info(String.format("==== Run pre-simulation hook for state '%s'", sasState.toString()));
        preSimulate(sasState);
        LOGGER.info(String.format("==== Done. Pre-simulation hook for state '%s'", sasState.toString()));
        
        LOGGER.info(String.format("==== Run simulation for state '%s'", sasState.toString()));
        doSimulate(sasState);
        LOGGER.info(String.format("==== Done. Simulation for state '%s'", sasState.toString()));
        
        LOGGER.info(String.format("==== Run post-simulation hook for state '%s'", sasState.toString()));
        postSimulate(sasState);
        LOGGER.info(String.format("==== Done. Post-simulation hook for state '%s'", sasState.toString()));
    }

    protected void preSimulate(SelfAdaptiveSystemState<?> sasState) {
        /** default - nothing to do here */
    }
    
    protected abstract void doSimulate(SelfAdaptiveSystemState<?> sasState);
    
    protected void postSimulate(SelfAdaptiveSystemState<?> sasState) {
        /** default - nothing to do here */
    }
    
    protected void doInitialize() {
        /** default - nothing to do here
         * 
         *   here any behavior should be implemented that is required to initialize the experience simulator
         *   
         *   */
    }
    

}


