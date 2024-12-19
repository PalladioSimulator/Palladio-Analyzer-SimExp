package org.palladiosimulator.simexp.pcm.datasource;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

import com.google.common.collect.Maps;

public class StateMeasurementFilter<A, V> {

    private final InitialPcmStateCreator<A, V> initialStateCreator;

    public StateMeasurementFilter(InitialPcmStateCreator<A, V> initialStateCreator) {
        this.initialStateCreator = initialStateCreator;
    }

    public Map<PcmMeasurementSpecification, Measurement> filterStateMeasurements(List<ExperimentRun> experimentRuns) {
        Set<SimulatedMeasurementSpecification> initialMeasurementSpecs = initialStateCreator.getMeasurementSpecs();
        return filterStateMeasurements(experimentRuns, initialMeasurementSpecs);
    }

    private Map<PcmMeasurementSpecification, Measurement> filterStateMeasurements(List<ExperimentRun> experimentRuns,
            Set<SimulatedMeasurementSpecification> initialMeasurementSpecs) {
        Map<PcmMeasurementSpecification, Measurement> measurments = Maps.newHashMap();
        for (ExperimentRun each : experimentRuns) {
            Map<PcmMeasurementSpecification, Measurement> stateMeasurments = filterStateMeasurements(each,
                    initialMeasurementSpecs);
            // TODO exception handling
            if (stateMeasurments.isEmpty()) {
                throw new RuntimeException("");
            }
            measurments.putAll(stateMeasurments);
        }
        return measurments;
    }

    private Map<PcmMeasurementSpecification, Measurement> filterStateMeasurements(ExperimentRun experimentRun,
            Set<SimulatedMeasurementSpecification> initialMeasurementSpecs) {
        Map<PcmMeasurementSpecification, Measurement> specToMeas = new HashMap<>();
        MetricComparer metricComparer = new MetricComparer();
        EList<Measurement> measurements = experimentRun.getMeasurement();
        for (Measurement each : measurements) {
            List<PcmMeasurementSpecification> specifications = findSpecifications(each, initialMeasurementSpecs);
            for (PcmMeasurementSpecification pcmSpec : specifications) {
                if (metricComparer.sameMetric(each, pcmSpec)) {
                    specToMeas.put(pcmSpec, each);
                }
            }
        }
        return specToMeas;
    }

    private List<PcmMeasurementSpecification> findSpecifications(Measurement measurement,
            Set<SimulatedMeasurementSpecification> initialMeasurementSpecs) {
        MeasuringPoint measuringPoint = measurement.getMeasuringType()
            .getMeasuringPoint();
        Set<PcmMeasurementSpecification> measurementSpecs = getMeasurementSpecs(initialMeasurementSpecs);
        return measurementSpecs.stream()
            .filter(spec -> spec.hasMeasuringPoint(measuringPoint))
            .collect(Collectors.toList());
    }

    private Set<PcmMeasurementSpecification> getMeasurementSpecs(
            Set<SimulatedMeasurementSpecification> measurementSpecs) {
        return measurementSpecs.stream()
            .filter(PcmMeasurementSpecification.class::isInstance)
            .map(PcmMeasurementSpecification.class::cast)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
