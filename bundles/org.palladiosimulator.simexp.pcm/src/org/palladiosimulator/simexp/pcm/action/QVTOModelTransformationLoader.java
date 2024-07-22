package org.palladiosimulator.simexp.pcm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.commons.eclipseutils.FileHelper;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;
import org.palladiosimulator.simulizar.reconfiguration.qvto.util.ModelTransformationCache;
import org.palladiosimulator.simulizar.reconfigurationrule.ModelTransformation;

public class QVTOModelTransformationLoader implements IQVTOModelTransformationLoader {
    private static final String SUPPORTED_TRANSFORMATION_FILE_EXTENSION = ".qvto";

    private final String qvtoFilePath;

    public QVTOModelTransformationLoader(String qvtoFilePath) {
        this.qvtoFilePath = qvtoFilePath;
    }

    @Override
    public QvtoModelTransformation findQvtoModelTransformation(String transformationName) {
        List<QvtoModelTransformation> trafos = loadQVTOReconfigurations();
        Stream<QvtoModelTransformation> stream = trafos.stream();
        ModelTransformation<?> result = stream.filter(new TransformationNamePredicate(transformationName))
            .findFirst()
            .orElse(null);
        return (QvtoModelTransformation) result;
    }

    private static class TransformationNamePredicate implements Predicate<ModelTransformation<?>> {
        private final String name;

        public TransformationNamePredicate(String name) {
            this.name = name;
        }

        @Override
        public boolean test(ModelTransformation<?> each) {
            if (!(each instanceof QvtoModelTransformation)) {
                return false;
            }
            QvtoModelTransformation qvtoModelTransformation = (QvtoModelTransformation) each;
            if (!name.equals(qvtoModelTransformation.getTransformationName())) {
                return false;
            }
            return true;
        }
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
