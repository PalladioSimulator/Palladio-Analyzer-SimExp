package org.palladiosimulator.simexp.state.failure;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.failuremodel.failuretype.Failure;
import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;

public class NodeFailureTypeCreatorTest {

    private NodeFailureTypeCreator creator;

    @Before
    public void setUp() throws Exception {
        creator = new NodeFailureTypeCreator();
    }

    @Test
    public void testCreateFailureTypeRepoWithCrashHwFailure() {
        FailureTypeRepository failureRepo = creator.create();
        assertEquals("Failed to create repo with single CrashFaiure of type HW crash", 1, failureRepo.getFailuretypes().size());
        Failure hwCrashFailure = failureRepo.getFailuretypes().get(0);
        assertEquals("HWCrashFailure", hwCrashFailure.getEntityName());
    }
    

}
