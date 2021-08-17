package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PcmMeasurementSpecProvider implements IPcmMeasurementSpecProvider {

    private Experiment experiment;
    private IPcmMeasurementSpecificationBuilder pcmMeasurementSpecBuilder;

    public PcmMeasurementSpecProvider(Experiment experiment, IPcmMeasurementSpecificationBuilder pcmMeasurementSpecBuilder) {
        this.experiment = experiment;
        this.pcmMeasurementSpecBuilder = pcmMeasurementSpecBuilder;
    }

    @Override
    public Monitor findMonitor(String monitorName) {
        Stream<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors().stream();
        return monitors.filter(m -> m.getEntityName().equals(monitorName))
                       .findFirst()
                       .orElseThrow(() -> new RuntimeException("There is no monitor."));
    }

    @Override
    public List<PcmMeasurementSpecification> getPcmMeasurementSpecs() {
        EList<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors();
        List<PcmMeasurementSpecification> pcmSpecs = Arrays.asList(
                pcmMeasurementSpecBuilder.buildResponseTimeSpec(monitors),
                pcmMeasurementSpecBuilder.buildCpuUtilizationSpecOf(monitors, LoadBalancingPcmMeasurementSpecificationBuilder.CPU_SERVER_1_MONITOR),
                pcmMeasurementSpecBuilder.buildCpuUtilizationSpecOf(monitors, LoadBalancingPcmMeasurementSpecificationBuilder.CPU_SERVER_2_MONITOR));
        return pcmSpecs;
    }
    
    

}
