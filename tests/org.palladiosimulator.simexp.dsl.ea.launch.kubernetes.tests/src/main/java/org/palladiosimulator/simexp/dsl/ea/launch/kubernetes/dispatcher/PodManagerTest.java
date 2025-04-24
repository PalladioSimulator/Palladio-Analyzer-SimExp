package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment.IPodRestartListener.Reason;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.ITaskConsumer;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;

public class PodManagerTest {
    private PodManager podManager;

    @Mock
    private ClassLoader classloader;
    @Mock
    private ITaskConsumer taskConsumer;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.podManager = new PodManager(classloader);
        this.podManager.registerTaskConsumer(taskConsumer);
    }

    @Test
    public void testGetPodName() {
        String actualPodName = podManager.getPodName("node02:default.simexp-c6f6d95f4-8b9f8");

        assertEquals("simexp-c6f6d95f4-8b9f8", actualPodName);
    }

    @Test
    public void testOnRestartUnknownPod() {
        podManager.onRestart("n", "p", Reason.OOMKilled, 1);
    }

    @Test
    public void testOnRestartFirst() {
        JobResult result = new JobResult();
        result.executor_id = "n:default.p";
        podManager.taskStarted("t", result);

        podManager.onRestart("n", "p", Reason.OOMKilled, 1);

        verify(taskConsumer).taskAborted(eq("t"), any(JobResult.class));
    }

    @Test
    public void testOnRestartSecond() {
        JobResult result = new JobResult();
        result.executor_id = "n:default.p";
        podManager.taskStarted("t", result);
        podManager.onRestart("n", "p", Reason.OOMKilled, 1);

        podManager.onRestart("n", "p", Reason.OOMKilled, 2);

        verify(taskConsumer, times(1)).taskAborted(eq("t"), any(JobResult.class));
    }
}
