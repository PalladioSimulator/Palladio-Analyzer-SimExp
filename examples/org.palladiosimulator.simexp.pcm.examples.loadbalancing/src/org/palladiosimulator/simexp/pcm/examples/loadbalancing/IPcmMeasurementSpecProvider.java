package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;

import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public interface IPcmMeasurementSpecProvider {
    
    Monitor findMonitor(String monitorName);
    
    
    List<PcmMeasurementSpecification> getPcmMeasurementSpecs();

}
