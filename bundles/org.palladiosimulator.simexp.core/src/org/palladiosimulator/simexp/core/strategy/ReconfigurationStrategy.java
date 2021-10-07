package org.palladiosimulator.simexp.core.strategy;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

// This class is not yet integrated in the simulated experience process. This is rather a reminder for future work. 
// The idea is to introduce a dedicated concept for reconfiguration strategies following the MAPE-K principles.
public abstract class ReconfigurationStrategy<T> implements Policy<Reconfiguration<T>> {
    
    protected static final Logger LOGGER = Logger.getLogger(ReconfigurationStrategy.class);
    
    private final SharedKnowledge knowledge;
    
    public ReconfigurationStrategy() {
        this.knowledge = new SharedKnowledge();
    }
    

	@Override
	public Reconfiguration<T> select(State source, Set<Reconfiguration<T>> options) {
	    Reconfiguration<T> reconfiguration = emptyReconfiguration();
	    
	    LOGGER.info("Run MAPE-K loop ...");
		monitor(knowledge);
		LOGGER.info("Executed 'MONITOR' step");
		boolean isAnalyzable = analyse(knowledge);
		LOGGER.info("Executed 'ANALYZE' step");
		if (isAnalyzable) {
			reconfiguration = plan(knowledge);
			LOGGER.info("Executed 'PLANING' step");
		}
		LOGGER.info(String.format("Execute 'EXECUTION' step applying reconfiguration = %s", reconfiguration.toString()));
		return reconfiguration;
	}

	protected abstract void monitor(SharedKnowledge knowledge);

	protected abstract boolean analyse(SharedKnowledge knowledge);

	protected abstract Reconfiguration<T> plan(SharedKnowledge knowledge);
	
	protected abstract Reconfiguration<T> emptyReconfiguration();
}
