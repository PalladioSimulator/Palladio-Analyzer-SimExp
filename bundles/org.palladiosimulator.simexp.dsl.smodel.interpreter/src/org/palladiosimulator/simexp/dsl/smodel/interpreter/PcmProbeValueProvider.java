package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PcmProbeValueProvider implements ProbeValueProvider, ProbeValueProviderMeasurementInjector {

    private final Map<MeasuringPoint, Double> currentMeasurementPoints;

    private final IModelsLookup modelsLookup;

    public PcmProbeValueProvider(IModelsLookup modelsLookup) {
        Comparator<MeasuringPoint> comparator = Comparator.comparing(MeasuringPoint::getStringRepresentation);
        this.currentMeasurementPoints = new TreeMap<>(comparator);
        this.modelsLookup = modelsLookup;
    }

    @Override
    public double getDoubleValue(Probe probe) {
        MeasuringPoint measuringPoint = modelsLookup.findMeasuringPoint(probe);
        if (measuringPoint == null) {
            throw new RuntimeException(
                    String.format("no MeasuringPoint found for probe: %s:%S", probe.getKind(), probe.getIdentifier()));
        }
        Double doubleValue = currentMeasurementPoints.get(measuringPoint);
        return doubleValue;
    }

    @Override
    public void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue) {
        PcmMeasurementSpecification pcmSpec = (PcmMeasurementSpecification) spec;
        MeasuringPoint measuringPoint = pcmSpec.getMeasuringPoint();
        currentMeasurementPoints.put(measuringPoint, measurementValue);
    }

    @Override
    public boolean getBooleanValue(Probe probe) {
        throw new UnsupportedOperationException("Feature incomplete. Contact assistance.");
    }

}
