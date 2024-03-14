package org.palladiosimulator.simexp.dsl.kmodel.interpreter.util;

import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class SimulatedMeasurementSpecCreatorHelper {

    public PcmMeasurementSpecification createPcmMeasurmentSpec(Monitor monitor) {
        PcmMeasurementSpecification rtSpec = PcmMeasurementSpecification.newBuilder()
            .withName(monitor.getEntityName())
            .measuredAt(monitor.getMeasuringPoint())
            .withMetric(monitor.getMeasurementSpecifications()
                .get(0)
                .getMetricDescription())
            .useDefaultMeasurementAggregation()
            .withOptionalSteadyStateEvaluator(Threshold.lessThan(0.1))
            .build();

        return rtSpec;
    }

}
