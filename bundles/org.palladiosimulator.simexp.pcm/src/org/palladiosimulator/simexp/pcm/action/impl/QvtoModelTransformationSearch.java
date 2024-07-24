package org.palladiosimulator.simexp.pcm.action.impl;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.pcm.action.IQVTOModelTransformationLoader;
import org.palladiosimulator.simexp.pcm.action.IQVTOModelTransformationSearch;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;
import org.palladiosimulator.simulizar.reconfigurationrule.ModelTransformation;

public class QvtoModelTransformationSearch implements IQVTOModelTransformationSearch {
    private final IQVTOModelTransformationLoader qvtoModelTransformationLoader;

    public QvtoModelTransformationSearch(IQVTOModelTransformationLoader qvtoModelTransformationLoader) {
        this.qvtoModelTransformationLoader = qvtoModelTransformationLoader;
    }

    public QvtoModelTransformation findQvtoModelTransformation(String transformationName) {
        List<QvtoModelTransformation> trafos = qvtoModelTransformationLoader.loadQVTOReconfigurations();
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
}
