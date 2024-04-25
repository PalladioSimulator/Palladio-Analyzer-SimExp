package org.palladiosimulator.simexp.pcm.config;

import java.util.List;

import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;

public interface IPCMConfiguration {
    QualityObjective getQualityObjective();

    List<String> getMonitorNames();

}
