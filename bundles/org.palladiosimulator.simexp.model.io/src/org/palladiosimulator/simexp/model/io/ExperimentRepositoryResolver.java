package org.palladiosimulator.simexp.model.io;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;

public class ExperimentRepositoryResolver {
    
    public Experiment resolveExperiment(ExperimentRepository experimentRepository) {
        EList<Experiment> experiments = experimentRepository.getExperiments();
        
        if (experiments.isEmpty()) {
            return null;
        }
        Experiment exp = (Experiment) experiments.get(0);
        EcoreUtil.resolveAll(exp);
        return exp;
    }

}
