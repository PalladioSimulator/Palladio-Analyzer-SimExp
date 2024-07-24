package org.palladiosimulator.simexp.pcm.action;

import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

public interface IQVToReconfigurationManager {

    QVTOReconfigurator getReconfigurator(IExperimentProvider experimentProvider);

    void resetReconfigurator();

    void addModelsToTransform(Resource eResource);

    IQVTOModelTransformationSearch getQVTOModelTransformationSearch();

}
