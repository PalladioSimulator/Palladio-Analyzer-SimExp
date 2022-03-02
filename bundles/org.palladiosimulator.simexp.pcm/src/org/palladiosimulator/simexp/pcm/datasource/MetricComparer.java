package org.palladiosimulator.simexp.pcm.datasource;

import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class MetricComparer {
    public boolean sameMetric(Measurement measurement, PcmMeasurementSpecification spec) {
        MetricDescription first = spec.getMetricDescription();
        MetricDescription second = measurement.getMeasuringType().getMetric();
        if (second instanceof MetricSetDescription) {
            return containsMetric(first, (MetricSetDescription) second);
        }

        return sameMetric(first, second);
    }

    private boolean containsMetric(MetricDescription first, MetricSetDescription second) {
        return second.getSubsumedMetrics().stream().anyMatch(m -> sameMetric(first, m));
    }

    private boolean sameMetric(MetricDescription first, MetricDescription second) {
        return first.getId().equals(second.getId());
    }
}
