package org.palladiosimulator.simexp.pcm.compare;

import static org.palladiosimulator.simexp.pcm.util.PcmUtil.stringConcatenation;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.ReferenceChange;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public class PCMRandomVariableChangeFilter extends DiffFilter {

	public PCMRandomVariableChangeFilter() {
		super();
	}

	@Override
	public void applyTo(List<Diff> diffsToFilter) {
		List<Diff> result = diffsToFilter.stream().filter(diffsWithChangedAttribute()).collect(Collectors.toList());
		if (result.isEmpty() == false) {
			this.result.addAll(result);
		}
	}

	private Predicate<Diff> diffsWithChangedAttribute() {
		return diff -> {
			if (ReferenceChange.class.isInstance(diff) == false) {
				return false;
			}
			boolean match = true;
			match &= (diff.getKind() == DifferenceKind.ADD);
			match &= PCMRandomVariable.class.isInstance(ReferenceChange.class.cast(diff).getValue());
			return match;
		};
	}

	@Override
	public Optional<String> getResult() {
		return anyMatch() ? Optional.of(convertToString()) : Optional.empty();
	}

	private String convertToString() {
		return result.stream().map(this::convertToString).reduce(stringConcatenation()).get();
	}

	private String convertToString(Diff diff) {
		PCMRandomVariable pcmVar = PCMRandomVariable.class.cast(ReferenceChange.class.cast(diff).getValue());
		String name = pcmVar.getVariableCharacterisation_Specification().toString();
		String value = pcmVar.getSpecification();
		return String.format("Reference: %1s, New value: %2s", name, value);
	}

}
