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
import org.mockito.Captor;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.TaskManager.TaskState;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class TaskManagerTest {
    private TaskManager taskManager;

    @Mock
    private IResultHandler resultLogger;
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
    public void testTaskStart() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();

        TaskState actualTaskStatus = taskManager.onTaskStarted("t", result);

        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(1);
        assertThat(actualTaskStatus.completed).isEqualTo(0);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskStartAfterComplete() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);
        result.reward = 1.0;
        taskManager.onTaskCompleted("t", result);

        JobResult startResult = new JobResult();
        TaskState actualTaskStatus = taskManager.onTaskStarted("t", startResult);

        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(1);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskCompleted() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);

        JobResult completedResult = new JobResult();
        completedResult.reward = 1.0;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", completedResult);

        verify(task).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).hasValue(completedResult.reward);
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(1);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskCompletedWithoutCreate() {
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);

        JobResult completedResult = new JobResult();
        completedResult.reward = 1.0;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", completedResult);

        verify(task, never()).setResult(captor.capture());
        assertThat(actualTaskStatus.created).isEqualTo(0);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(0);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskCompletedWithoutStart() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult completedResult = new JobResult();
        completedResult.reward = 1.0;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", completedResult);

        verify(task).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).hasValue(completedResult.reward);
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(1);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskCompletedAfterAbort() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);
        taskManager.taskAborted("t", result);

        JobResult completeResult = new JobResult();
        completeResult.reward = 1.0;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", completeResult);

        verify(task, times(1)).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).isEmpty();
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(0);
        assertThat(actualTaskStatus.aborted).isEqualTo(1);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskCompletedAfterFailed() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);
        taskManager.onTaskCompleted("t", result);

        JobResult completeResult = new JobResult();
        completeResult.reward = 1.0;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", completeResult);

        verify(task, times(1)).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).isEmpty();
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(0);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(1);
    }

    @Test
    public void testTaskFailed() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);

        JobResult failedResult = new JobResult();
        failedResult.reward = null;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", failedResult);

        verify(task, times(1)).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).isEmpty();
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(0);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(1);
    }

    @Test
    public void testTaskFailedAfterComplete() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);
        result.reward = 1.0;
        taskManager.onTaskCompleted("t", result);

        JobResult failedResult = new JobResult();
        failedResult.reward = null;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", failedResult);

        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(1);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskAbortedFirst() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);

        TaskState actualTaskStatus = taskManager.onTaskAborted("t", result);

        verify(task, times(1)).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).isEmpty();
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(0);
        assertThat(actualTaskStatus.aborted).isEqualTo(1);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskAbortedPermanent() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);
        taskManager.taskAborted("t", result);
        taskManager.taskAborted("t", result);

        TaskState actualTaskStatus = taskManager.onTaskAborted("t", result);

        verify(task, times(1)).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).isEmpty();
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(0);
        assertThat(actualTaskStatus.aborted).isEqualTo(1);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskAbortedAfterComplete() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result = new JobResult();
        taskManager.taskStarted("t", result);
        result.reward = 1.0;
        taskManager.onTaskCompleted("t", result);
        taskManager.taskStarted("t", result);

        JobResult abortResult = new JobResult();
        TaskState actualTaskStatus = taskManager.onTaskAborted("t", abortResult);

        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(1);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }

    @Test
    public void testTaskCompletedAfterQueueTimeout() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        taskManager.newTask("t", task, optimizableValues);
        JobResult result1 = new JobResult();
        result1.redelivered = false;
        result1.delivery_count = 0;
        taskManager.taskStarted("t", result1);
        JobResult result2 = new JobResult();
        result2.redelivered = true;
        result2.delivery_count = 1;
        taskManager.taskStarted("t", result2);
        JobResult result3 = new JobResult();
        result3.reward = 1.0;
        taskManager.taskCompleted("t", result3);

        JobResult result4 = new JobResult();
        result4.reward = 2.0;
        TaskState actualTaskStatus = taskManager.onTaskCompleted("t", result4);

        verify(task).setResult(captor.capture());
        Optional<Double> capturedArgument = captor.getValue();
        assertThat(capturedArgument).hasValue(result3.reward);
        verify(resultLogger, times(1)).process(optimizableValues, result3);
        assertThat(actualTaskStatus.created).isEqualTo(1);
        assertThat(actualTaskStatus.started).isEqualTo(0);
        assertThat(actualTaskStatus.completed).isEqualTo(1);
        assertThat(actualTaskStatus.aborted).isEqualTo(0);
        assertThat(actualTaskStatus.failed).isEqualTo(0);
    }
}
