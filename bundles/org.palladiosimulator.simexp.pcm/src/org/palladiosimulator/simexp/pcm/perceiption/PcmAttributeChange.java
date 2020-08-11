package org.palladiosimulator.simexp.pcm.perceiption;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;

public class PcmAttributeChange implements PcmModelChange {

	private final Function<ExperimentRunner, EObject> retrieveTargetHandler;
	private final String attributeName;
	
	public PcmAttributeChange(Function<ExperimentRunner, EObject> retrieveTargetHandler, String attributeName) {
		this.retrieveTargetHandler = retrieveTargetHandler;
		this.attributeName = attributeName;
	}
	
	@Override
	public void apply(PerceivedValue<?> change) {
		Optional<?> newValue = change.getElement(attributeName);
		if (newValue.isPresent()) {
			applyChange(retrieveTarget(), newValue.get());
		} else {
			//TODO logging or exception...
		}
	}

	private EObject retrieveTarget() {
		return retrieveTargetHandler.apply(ExperimentProvider.get().getExperimentRunner());
	}
	
	private void applyChange(EObject target, Object newValue) {
		EStructuralFeature feature = target.eClass().getEStructuralFeature(attributeName);
		target.eSet(feature, newValue.toString());
	}

}
