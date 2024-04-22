package org.palladiosimulator.simexp.pcm.action;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public interface IQVToReconfigurationManager {

    QVTOReconfigurator getReconfigurator(IExperimentProvider experimentProvider);

    void resetReconfigurator();

    void addModelsToTransform(Resource eResource);

    List<QVToReconfiguration> loadReconfigurations();

    QvtoModelTransformation findQvtoModelTransformation(String transformationName);

}
