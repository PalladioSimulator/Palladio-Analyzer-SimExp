package org.palladiosimulator.simexp.pcm.action;

import java.util.List;

import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public class QVTOModelTransformationCache implements IQVTOModelTransformationLoader {
    private final IQVTOModelTransformationLoader qvtoModelTransformationLoader;

    private List<QvtoModelTransformation> cachedTransformations;

    public QVTOModelTransformationCache(IQVTOModelTransformationLoader qvtoModelTransformationLoader) {
        this.qvtoModelTransformationLoader = qvtoModelTransformationLoader;
    }

    @Override
    public List<QvtoModelTransformation> loadQVTOReconfigurations() {
        if (cachedTransformations == null) {
            cachedTransformations = qvtoModelTransformationLoader.loadQVTOReconfigurations();
        }
        return cachedTransformations;
    }

    @Override
    public QvtoModelTransformation findQvtoModelTransformation(String transformationName) {
        return qvtoModelTransformationLoader.findQvtoModelTransformation(transformationName);
    }

}
