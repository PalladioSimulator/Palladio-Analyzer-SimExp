package org.palladiosimulator.simexp.pcm.action;

import java.util.List;

import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public interface IQVTOModelTransformationLoader {
    List<QvtoModelTransformation> loadQVTOReconfigurations();
}
