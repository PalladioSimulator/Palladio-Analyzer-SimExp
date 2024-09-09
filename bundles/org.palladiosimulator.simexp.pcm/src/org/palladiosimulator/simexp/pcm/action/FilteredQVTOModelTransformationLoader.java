package org.palladiosimulator.simexp.pcm.action;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public class FilteredQVTOModelTransformationLoader extends QVTOModelTransformationLoader {
    private final Set<String> transformationNames;

    public FilteredQVTOModelTransformationLoader(String qvtoFilePath, Set<String> transformationNames) {
        super(qvtoFilePath);
        this.transformationNames = transformationNames;
    }

    @Override
    public List<QvtoModelTransformation> loadQVTOReconfigurations() {
        List<QvtoModelTransformation> allQVTOReconfigurations = super.loadQVTOReconfigurations();
        List<QvtoModelTransformation> filteredQVTOReconfigurations = allQVTOReconfigurations.stream()
            .filter(c -> transformationNames.contains(c.getTransformationName()))
            .collect(Collectors.toList());
        return filteredQVTOReconfigurations;
    }
}
