package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;


/**
 * 
 * This policy aims to provide a strategy to compensate server node failures
 * 
 * */
public class PerformabilityStrategy implements Policy<Action<?>> {
    
    private static final Logger LOGGER = Logger.getLogger(PerformabilityStrategy.class.getName());
    
    private final PcmMeasurementSpecification pcmSpec;

    public PerformabilityStrategy(PcmMeasurementSpecification pcmSpec) {
        this.pcmSpec = pcmSpec;
    }

    @Override
    public String getId() {
        return PerformabilityStrategy.class.getName();
    }

    @Override
    public Action<?> select(State source, Set<Action<?>> options) {
        LOGGER.info("************* PerformabilityStrategy.select ****************");
        
        /**
         * This method must be implemented for each system model under study, i.e.
         * the actual reconfiguration files (qvto transformations) are defined
         * as part of the system model
         * 
         * In order to compensate for server node failures, dedicated triggers and
         * selected actions (from the Set options) to execute for each case must be implemented here
         * */

        LOGGER.info("************* No action was currently selected ****************");
        return QVToReconfiguration.empty();
    }

}
