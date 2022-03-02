package org.palladiosimulator.simexp.core.mape.impl;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.mape.IStateManager;

 class Execute implements IExecute {
     
     
     private static final Logger LOGGER = Logger.getLogger(Execute.class.getName());

    public Execute() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void execute(IAction action, IStateManager sm) {
        LOGGER.info("Start EXECUTE phase");
        
        
        action.action(sm);
        
        LOGGER.info("Done.  EXECUTE phase");
    }
}
