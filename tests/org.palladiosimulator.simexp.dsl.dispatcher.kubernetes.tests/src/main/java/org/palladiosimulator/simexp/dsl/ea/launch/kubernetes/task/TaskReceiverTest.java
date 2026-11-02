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
                  "id": "Task 1",
                  "executor_id": "node02:default.simexp-85df8d9cdb-46292",
                  "tag": 1,
                  "redelivered": false,
                  "delivery_count": 0,
                  "status": "COMPLETE",
                  "reward": 2.538916360578,
                  "quality_measurements": {
                    "runs": [
                      {
                        "quality_attributes": {
                          "PacketLoss.props": [
                            0.174242424242,
                            0.127863170907
                          ],
                          "EnergyConsumption.props": [
                            34.3419196,
                            30.6465861964
                          ]
                        }
                      }
                    ]
                  },
                  "error": null,
                  "return_code": 0
                }
                """;

        JobResult actualResult = taskReceiver.deserializeMessage(message);

        JobResult expectedResult = new JobResult();
        expectedResult.tag = 1;
        expectedResult.executor_id = "node02:default.simexp-85df8d9cdb-46292";
        expectedResult.id = "Task 1";
        expectedResult.redelivered = false;
        expectedResult.delivery_count = 0;
        expectedResult.status = Status.COMPLETE;
        expectedResult.reward = 2.538916360578;
        expectedResult.return_code = 0;
        Map<String, List<Double>> expectedQualityAttributes = new HashMap<>();
        expectedQualityAttributes.put("PacketLoss.props", Arrays.asList(0.174242424242, 0.127863170907));
        expectedQualityAttributes.put("EnergyConsumption.props", Arrays.asList(34.3419196, 30.6465861964));
        Run expectedRun = new Run(expectedQualityAttributes);
        expectedResult.qualityMeasurements = new QualityMeasurements(Collections.singletonList(expectedRun));
        assertThat(actualResult) //
            .usingRecursiveComparison()
            .isEqualTo(expectedResult);
    }

}
