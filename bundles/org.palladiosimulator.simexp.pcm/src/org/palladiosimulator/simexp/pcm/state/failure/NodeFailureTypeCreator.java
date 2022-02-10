package org.palladiosimulator.simexp.pcm.state.failure;

import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;
import org.palladiosimulator.failuremodel.failuretype.FailuretypeFactory;
import org.palladiosimulator.failuremodel.failuretype.HWCrashFailure;

public class NodeFailureTypeCreator {
    
    
    
    public FailureTypeRepository create() {
        FailuretypeFactory factory = FailuretypeFactory.eINSTANCE;
        FailureTypeRepository repo = factory.createFailureTypeRepository();
        HWCrashFailure crashFailure = factory.createHWCrashFailure();
        crashFailure.setEntityName("HWCrashFailure");
        repo.getFailuretypes().add(crashFailure);
        return repo;
    }
    
}
