package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class TaskManagerTest {
    private TaskManager taskManager;

    @Mock
    private IResultLogger resultLogger;
    @Mock
    private SettableFutureTask<Optional<Double>> task;

    @Captor
    private ArgumentCaptor<Optional<Double>> captor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        this.taskManager = new TaskManager(resultLogger);
    }

    @Test
    public void testTaskCompletedReward() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        result.reward = 1.0;

        taskManager.taskCompleted("t", result);

        verify(task).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).hasValue(result.reward);
    }

    @Test
    public void testTaskCompletedNoReward() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        result.reward = null;

        taskManager.taskCompleted("t", result);

        verify(task).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).isEmpty();
    }

    @Test
    public void testTaskAbortedFirst() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();

        taskManager.taskAborted("t", result);

        verify(task, never()).setResult(ArgumentMatchers.<Optional<Double>> any());
    }

    @Test
    public void testTaskAbortedPermanent() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskAborted("t", result);
        taskManager.taskAborted("t", result);

        taskManager.taskAborted("t", result);

        verify(task, times(1)).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).isEmpty();
    }
}
