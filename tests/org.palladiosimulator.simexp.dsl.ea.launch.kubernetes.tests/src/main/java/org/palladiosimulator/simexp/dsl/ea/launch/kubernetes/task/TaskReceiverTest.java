package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.Run;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult.Status;

import com.rabbitmq.client.Channel;

public class TaskReceiverTest {
    private TaskReceiver taskReceiver;

    @Mock
    private Channel channel;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        taskReceiver = new TaskReceiver(channel, "", getClass().getClassLoader());
    }

    @Test
    public void testDeserializeMessage() {
        String message = """
                {
                    "tag": 1,
                    "executor_id": "node02:default.simexp-6cf84d6945-6tm2t",
                    "id": "Task 1",
                    "redelivered": false,
                    "delivery_count": 0,
                    "status": "COMPLETE",
                    "reward": 1.5422303920479277,
                    "quality_measurements": {
                        "runs": [
                            {
                                "quality_attributes": {
                                    "PacketLoss.props": [
                                        0.318942222222,
                                        0.0774108561996
                                    ],
                                    "EnergyConsumption.props": [
                                        34.5221044,
                                        33.2258566279
                                    ]
                                }
                            }
                        ]
                    }
                }
                """;

        JobResult actualResult = taskReceiver.deserializeMessage(message);

        JobResult expectedResult = new JobResult();
        expectedResult.tag = 1;
        expectedResult.executor_id = "node02:default.simexp-6cf84d6945-6tm2t";
        expectedResult.id = "Task 1";
        expectedResult.redelivered = false;
        expectedResult.delivery_count = 0;
        expectedResult.status = Status.COMPLETE;
        expectedResult.reward = 1.5422303920479277;

        Map<String, List<Double>> expectedQualityAttributes = new HashMap<>();
        expectedQualityAttributes.put("PacketLoss.props", Arrays.asList(0.318942222222, 0.0774108561996));
        expectedQualityAttributes.put("EnergyConsumption.props", Arrays.asList(34.5221044, 33.2258566279));
        Run expectedRun = new Run(expectedQualityAttributes);
        expectedResult.qualityMeasurements = new QualityMeasurements(Collections.singletonList(expectedRun));
        assertThat(actualResult) //
            .usingRecursiveComparison()
            .isEqualTo(expectedResult);
    }

}
