package org.palladiosimulator.simexp.pcm.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.commons.eclipseutils.FileHelper;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;
import org.palladiosimulator.simulizar.reconfiguration.qvto.util.ModelTransformationCache;

public class QVTOModelTransformationLoader implements IQVTOModelTransformationLoader {
    private static final String SUPPORTED_TRANSFORMATION_FILE_EXTENSION = ".qvto";

    private final String qvtoFilePath;

    public QVTOModelTransformationLoader(String qvtoFilePath) {
        this.qvtoFilePath = qvtoFilePath;
    }

    @Override
    public List<QvtoModelTransformation> loadQVTOReconfigurations() {
        URI[] qvtoFiles = FileHelper.getURIs(qvtoFilePath, SUPPORTED_TRANSFORMATION_FILE_EXTENSION);
        if (qvtoFiles.length == 0) {
            // TODO exception handling
            throw new RuntimeException("There are no qvto transformation specified.");
        }
        ModelTransformationCache transformationCache = new ModelTransformationCache(qvtoFiles);
        List<QvtoModelTransformation> trafos = new ArrayList<>();
        transformationCache.getAll()
            .forEach(trafos::add);
        return trafos;
    }

}
