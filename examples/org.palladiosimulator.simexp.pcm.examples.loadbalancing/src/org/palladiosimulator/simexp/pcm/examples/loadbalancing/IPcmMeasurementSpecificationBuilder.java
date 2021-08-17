package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public interface IPcmMeasurementSpecificationBuilder {

    PcmMeasurementSpecification buildResponseTimeSpec(EList<Monitor> monitors);
    
    PcmMeasurementSpecification buildCpuUtilizationSpecOf(EList<Monitor> monitors, String monitorName);
}
