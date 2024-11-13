package org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PcmProbeValueProvider implements IFieldValueProvider, ProbeValueProviderMeasurementInjector {

    private final Map<MeasuringPoint, Double> currentMeasurementPoints;

    private final IModelsLookup modelsLookup;

    public PcmProbeValueProvider(IModelsLookup modelsLookup) {
        Comparator<MeasuringPoint> comparator = Comparator.comparing(MeasuringPoint::getStringRepresentation);
        this.currentMeasurementPoints = new TreeMap<>(comparator);
        this.modelsLookup = modelsLookup;
    }

    @Override
    public Double getDoubleValue(Field field) {
        Probe probe = (Probe) field;
        MeasuringPoint measuringPoint = modelsLookup.findMeasuringPoint(probe);
        if (measuringPoint == null) {
            return null;
        }
        Double doubleValue = currentMeasurementPoints.get(measuringPoint);
        return doubleValue;
    }

    @Override
    public void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue) {
        if (spec instanceof PcmMeasurementSpecification) {
            PcmMeasurementSpecification pcmSpec = (PcmMeasurementSpecification) spec;
            MeasuringPoint measuringPoint = pcmSpec.getMeasuringPoint();
            currentMeasurementPoints.put(measuringPoint, measurementValue);
        }
    }

    @Override
    public Boolean getBoolValue(Field field) {
        Probe probe = (Probe) field;
        throw new UnsupportedOperationException("Feature incomplete. Contact assistance.");
    }

    @Override
    public Integer getIntegerValue(Field field) {
        throw new UnsupportedOperationException("Feature incomplete. Contact assistance.");
    }

    @Override
    public String getStringValue(Field field) {
        throw new UnsupportedOperationException("Feature incomplete. Contact assistance.");
    }
}
