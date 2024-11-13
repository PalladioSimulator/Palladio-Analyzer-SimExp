package org.palladiosimulator.simexp.pcm.simulator.config;

import java.util.List;

import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;

public interface IPCMConfiguration {
    QualityObjective getQualityObjective();

    List<String> getMonitorNames();

}
