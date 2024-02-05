package org.palladiosimulator.simexp.pcm.perceiption;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedElement;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class PcmAttributeChange<V> implements PcmModelChange<V> {

    private final Function<ExperimentRunner, EObject> retrieveTargetHandler;
    private final String attributeName;
    private final IExperimentProvider experimentProvider;
    private final PerceivedValueConverter<V> perceivedValueConverter;

    public PcmAttributeChange(Function<ExperimentRunner, EObject> retrieveTargetHandler, String attributeName,
            IExperimentProvider experimentProvider, PerceivedValueConverter<V> perceivedValueConverter) {
        this.retrieveTargetHandler = retrieveTargetHandler;
        this.attributeName = attributeName;
        this.experimentProvider = experimentProvider;
        this.perceivedValueConverter = perceivedValueConverter;
    }

    @Override
    public void apply(PerceivedValue<V> change) {
        PerceivedElement<V> pe = (PerceivedElement<V>) change;
        Optional<?> newValue = pe.getElement(attributeName);
        if (newValue.isPresent()) {
            CategoricalValue changedValue = perceivedValueConverter.convertElement(change, attributeName);
            applyChange(retrieveTarget(experimentProvider), changedValue);
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
