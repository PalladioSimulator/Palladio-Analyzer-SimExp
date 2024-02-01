package org.palladiosimulator.simexp.pcm.perceiption;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class PcmAttributeChange<V> implements PcmModelChange<V> {

    private final Function<ExperimentRunner, EObject> retrieveTargetHandler;
    private final String attributeName;
    private final IExperimentProvider experimentProvider;

    public PcmAttributeChange(Function<ExperimentRunner, EObject> retrieveTargetHandler, String attributeName,
            IExperimentProvider experimentProvider) {
        this.retrieveTargetHandler = retrieveTargetHandler;
        this.attributeName = attributeName;
        this.experimentProvider = experimentProvider;
    }

    @Override
    public void apply(PerceivedValue<V> change) {
        Optional<?> newValue = change.getElement(attributeName);
        if (newValue.isPresent()) {
            applyChange(retrieveTarget(experimentProvider), newValue.get());
        } else {
            // TODO logging or exception...
        }
    }

    private EObject retrieveTarget(IExperimentProvider experimentProvider) {
        return retrieveTargetHandler.apply(experimentProvider.getExperimentRunner());
    }

    private void applyChange(EObject target, Object newValue) {
        EStructuralFeature feature = target.eClass()
            .getEStructuralFeature(attributeName);
        target.eSet(feature, newValue.toString());
    }

}
