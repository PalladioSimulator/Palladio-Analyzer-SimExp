package org.palladiosimulator.simexp.core.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.mape.Analyzer;
import org.palladiosimulator.simexp.core.strategy.mape.Executer;
import org.palladiosimulator.simexp.core.strategy.mape.Monitor;
import org.palladiosimulator.simexp.core.strategy.mape.Planner;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

// This class is not yet integrated in the simulated experience process. This is rather a reminder for future work. 
// The idea is to introduce a dedicated concept for reconfiguration strategies following the MAPE-K principles.
public abstract class ReconfigurationStrategy<T extends Reconfiguration<?>> implements Policy<T> {
    
    protected static final Logger LOGGER = Logger.getLogger(ReconfigurationStrategy.class);
    
    private final Monitor monitor;
    private final SharedKnowledge knowledge;
    private final Analyzer analyzer;
    private final Planner planner;
    private final Executer executer;

    
    public ReconfigurationStrategy(Monitor monitor, Analyzer analyzer, Planner planner, Executer executer) {
        this.monitor = monitor;
        this.analyzer = analyzer;
        this.planner = planner;
        this.executer = executer;
        this.knowledge = new SharedKnowledge();
    }
    

	@Override
	public T select(State source, Set<T> options) {
	    LOGGER.info("==== Start MAPE-K loop ====");
	    T reconfiguration = emptyReconfiguration();
	    
	    // TODO: retrieve current measurement values
	    LOGGER.info("'MONITOR' step start");
//	    monitor.monitor();
		LOGGER.info("'MONITOR' step done");
		LOGGER.info("'ANALYZE' step start");
	    boolean isAnalyzable = analyzer.analyze();
		LOGGER.info("'ANALYZE' step done");
	    
	    if(isAnalyzable) {
	        LOGGER.info(String.format("'ANALYZE' found constraint violations: '%s'", isAnalyzable));
		    LOGGER.info("'PLANNING' step start");
	        List<ResolvedAction> actions = planner.plan();
	        reconfiguration = findReconfiguration(options, actions);
            LOGGER.info("'PLANNING' step done");
	    }
	    
//	    LOGGER.info("'MONITOR' step start");
//		monitor(source, knowledge);
//        LOGGER.debug(String.format("'MONITOR' knowledge snapshot: %s", knowledge.toString()));
//		LOGGER.info("'MONITOR' step done");
//		
//		LOGGER.info("'ANALYZE' step start");
//		boolean isAnalyzable = analyse(source, knowledge);
//		LOGGER.info(String.format("'ANALYZE' found constraint violations: '%s'", isAnalyzable));
//		LOGGER.info("'ANALYZE' step done");
//		if (isAnalyzable) {
//		    LOGGER.info("'PLANNING' step start");
//			reconfiguration = plan(source, options, knowledge);
//            LOGGER.info(String.format("'PLANNING' selected action '%s'", reconfiguration.getStringRepresentation()));
//            LOGGER.info("'PLANNING' step done");
//		}

	    LOGGER.info("'EXECUTE' step start");
		return reconfiguration;
	}
	

	/**
	 * 
	 * implements lookup between QVToReconfiguration and actions retrieved from planning phase
	 * 
	 * */
	private T findReconfiguration(Set<T> options, List<ResolvedAction> actions) {
	    Map<String, T> reconfigurationMap = new HashMap<>();
	    for(T option: options) {
	        reconfigurationMap.put(option.getStringRepresentation(), option);
	    }
	    String resolvedActionName = actions.get(0).getAction().getName();
	    T reconfiguration = reconfigurationMap.get(resolvedActionName);
        LOGGER.info(String.format("'PLANNING' selected action '%s'", reconfiguration.getStringRepresentation()));
	    return reconfiguration;
	    
	}

	protected abstract void monitor(State source, SharedKnowledge knowledge);

	protected abstract boolean analyse(State source, SharedKnowledge knowledge);

	protected abstract T plan(State source, Set<T> options, SharedKnowledge knowledge);
	
	protected abstract T emptyReconfiguration();
}
