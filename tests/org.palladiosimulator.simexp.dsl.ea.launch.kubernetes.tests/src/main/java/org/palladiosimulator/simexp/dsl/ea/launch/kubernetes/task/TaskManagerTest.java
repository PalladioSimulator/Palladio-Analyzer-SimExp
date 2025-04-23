package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import static org.junit.Assert.assertEquals;
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
    public void testGetPodName() {
        String actualPodName = taskManager.getPodName("node02:default.simexp-c6f6d95f4-8b9f8");

        assertEquals("simexp-c6f6d95f4-8b9f8", actualPodName);
    }

}
