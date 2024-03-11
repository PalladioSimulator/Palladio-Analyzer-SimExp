package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PcmProbeValueProvider implements ProbeValueProvider, ProbeValueProviderMeasurementInjector {

    private static final Logger LOGGER = Logger.getLogger(PcmProbeValueProvider.class.getName());

    private final Map<Probe, PcmMeasurementSpecification> probeToMeasurementSpec;
    private final HashMap<SimulatedMeasurementSpecification, Double> currentMeasurements;

    public PcmProbeValueProvider(List<Probe> probes, List<PcmMeasurementSpecification> specs) {
        this.probeToMeasurementSpec = new HashMap<>();
        this.currentMeasurements = new HashMap<>();
        init(probes, specs);
    }

    private void init(List<Probe> probes, List<PcmMeasurementSpecification> specs) {
        for (Probe probe : probes) {
            for (PcmMeasurementSpecification spec : specs) {
                MeasuringPoint measuringPoint = spec.getMeasuringPoint();
                String measuringPointAsStr = measuringPoint.getStringRepresentation();
//                String metricDescriptionRemoved = measuringPointAsStr.split("\\_")[0];
                if (probe.getIdentifier()
                    .equals(measuringPointAsStr)) {
                    probeToMeasurementSpec.put(probe, spec);
                }
            }
        }

    }

    @Override
    public Object getValue(Probe probe) {
        PcmMeasurementSpecification currentSpec = probeToMeasurementSpec.get(probe);
        if (currentSpec == null) {
            LOGGER.info(String.format("No corresponding PcmMeasurementSpecification found for probe %s",
                    probe.getIdentifier()));
            return null;
        }
        Double currValue = currentMeasurements.get(currentSpec);
        return currValue;
    }

    @Override
    public void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue) {
        currentMeasurements.put(spec, measurementValue);

    }

}
