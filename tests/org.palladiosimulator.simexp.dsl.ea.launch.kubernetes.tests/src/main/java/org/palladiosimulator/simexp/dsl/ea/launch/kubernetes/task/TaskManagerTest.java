package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class TaskManagerTest {
    private TaskManager taskManager;

    @Mock
    private IResultLogger resultLogger;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        this.taskManager = new TaskManager(resultLogger);
    }

    @Test
    public void dummy() {

    }
}
