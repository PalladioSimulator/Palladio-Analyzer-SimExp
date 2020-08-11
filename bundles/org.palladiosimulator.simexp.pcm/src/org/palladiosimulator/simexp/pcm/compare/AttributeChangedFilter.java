package org.palladiosimulator.simexp.pcm.compare;

import static org.palladiosimulator.simexp.pcm.util.PcmUtil.stringConcatenation;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;

public class AttributeChangedFilter extends DiffFilter {

	public AttributeChangedFilter() {
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
			boolean match = true;
			match &= (diff instanceof AttributeChange);
			match &= (diff.getKind() == DifferenceKind.CHANGE);
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
		String name = ((AttributeChange) diff).getAttribute().getName();
		String value = ((AttributeChange) diff).getValue().toString();
		return String.format("Attribute: %1s, new value: %2s", name, value);
	}

}
