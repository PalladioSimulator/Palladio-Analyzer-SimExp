package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult.Status;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class KubernetesFitnessResultIdentificatorTest {
    private KubernetesFitnessResultIdentificator identificator;

    @Before
    public void setUp() throws Exception {
        identificator = new KubernetesFitnessResultIdentificator();
    }

    @Test
    public void testUnknownTask() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();

        Optional<String> actualIdentificator = identificator.getIdentificator(optimizableValues);

        assertThat(actualIdentificator).isNull();
    }

    @Test
    public void testCompletedTask() {
        JobResult jobResult = createJobResult("t1", Status.COMPLETE);
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        identificator.process(optimizableValues, jobResult);

        Optional<String> actualIdentificator = identificator.getIdentificator(optimizableValues);

        assertThat(actualIdentificator).hasValue("t1");
    }

    @Test
    public void testAbortedTask() {
        JobResult jobResult = createJobResult("t1", Status.ABORT);
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        identificator.process(optimizableValues, jobResult);

        Optional<String> actualIdentificator = identificator.getIdentificator(optimizableValues);

        assertThat(actualIdentificator).hasValue("t1");
    }

    private JobResult createJobResult(String id, Status status) {
        JobResult jobResult = new JobResult();
        jobResult.id = id;
        jobResult.status = status;
        return jobResult;
    }

}
