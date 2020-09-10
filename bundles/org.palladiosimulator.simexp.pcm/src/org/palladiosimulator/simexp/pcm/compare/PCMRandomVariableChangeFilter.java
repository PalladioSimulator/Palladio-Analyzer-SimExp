package org.palladiosimulator.simexp.pcm.compare;

import java.util.function.Predicate;

import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.ReferenceChange;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public class PCMRandomVariableChangeFilter extends DiffFilter {

	public PCMRandomVariableChangeFilter() {
		super();
	}

	@Override
	protected Predicate<Diff> satisfiesFilterCriteria() {
		return diff -> {
			if (diff instanceof ReferenceChange) {
				boolean match = true;
				match &= (diff.getKind() == DifferenceKind.ADD);
				match &= PCMRandomVariable.class.isInstance(ReferenceChange.class.cast(diff).getValue());
				return match;
			}
			return false;
		};
	}

	@Override
	protected String convertToString(Diff diff) {
		PCMRandomVariable pcmVar = PCMRandomVariable.class.cast(ReferenceChange.class.cast(diff).getValue());
		String name = pcmVar.getVariableCharacterisation_Specification().toString();
		String value = pcmVar.getSpecification();
		return String.format("Reference: %1s, New value: %2s", name, value);
	}

}
