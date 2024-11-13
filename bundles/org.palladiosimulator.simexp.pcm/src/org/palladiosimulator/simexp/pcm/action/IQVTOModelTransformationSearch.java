package org.palladiosimulator.simexp.pcm.action;

import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public interface IQVTOModelTransformationSearch {
    QvtoModelTransformation findQvtoModelTransformation(String transformationName);
}
