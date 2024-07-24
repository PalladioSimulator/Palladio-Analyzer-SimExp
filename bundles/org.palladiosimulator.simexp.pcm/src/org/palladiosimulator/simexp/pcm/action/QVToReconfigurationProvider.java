package org.palladiosimulator.simexp.pcm.action;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public class QVToReconfigurationProvider implements IQVToReconfigurationProvider {
    private final IQVToReconfigurationManager qvtoReconfigurationManager;
    private final IQVTOModelTransformationLoader qvtoModelTransformationLoader;

    public QVToReconfigurationProvider(IQVToReconfigurationManager qvtoReconfigurationManager,
            IQVTOModelTransformationLoader qvtoModelTransformationLoader) {
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
        this.qvtoModelTransformationLoader = qvtoModelTransformationLoader;
    }

    @Override
    public Set<QVToReconfiguration> getReconfigurations() {
        List<QvtoModelTransformation> transformations = qvtoModelTransformationLoader.loadQVTOReconfigurations();
        Set<QVToReconfiguration> qvtoReconfigurations = transformations.stream()
            .filter(each -> each instanceof QvtoModelTransformation)
            .map(each -> SingleQVToReconfiguration.of(each, qvtoReconfigurationManager))
            .collect(Collectors.toSet());
        return qvtoReconfigurations;
    }
}
