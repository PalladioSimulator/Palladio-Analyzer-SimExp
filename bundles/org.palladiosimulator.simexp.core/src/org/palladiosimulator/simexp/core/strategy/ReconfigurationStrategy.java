package org.palladiosimulator.simexp.core.strategy;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

// This class is not yet integrated in the simulated experience process. This is rather a reminder for future work. 
// The idea is to introduce a dedicated concept for reconfiguration strategies following the MAPE-K principles.
public abstract class ReconfigurationStrategy<T extends Reconfiguration<?>> implements Policy<T> {
    
    protected static final Logger LOGGER = Logger.getLogger(ReconfigurationStrategy.class);
    
    private final SharedKnowledge knowledge;
    
    public ReconfigurationStrategy() {
        this.knowledge = new SharedKnowledge();
    }
    

	@Override
	public T select(State source, Set<T> options) {
	    T reconfiguration = emptyReconfiguration();
	    
	    LOGGER.info("Run MAPE-K loop ...");
		monitor(source, knowledge);
		LOGGER.info("Executed 'MONITOR' step");
		boolean isAnalyzable = analyse(source, knowledge);
		LOGGER.info(String.format("Executed 'ANALYZE' step. Found constraint violations: '%s'", isAnalyzable));
		if (isAnalyzable) {
			reconfiguration = plan(source, options, knowledge);
			LOGGER.info("Executed 'PLANING' step");
		}
		LOGGER.info(String.format("Execute 'EXECUTION' step applying reconfiguration = %s", reconfiguration.toString()));
		return reconfiguration;
	}

	protected abstract void monitor(State source, SharedKnowledge knowledge);

	protected abstract boolean analyse(State source, SharedKnowledge knowledge);

	protected abstract T plan(State source, Set<T> options, SharedKnowledge knowledge);
	
	protected abstract T emptyReconfiguration();
}
