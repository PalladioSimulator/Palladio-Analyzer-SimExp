package org.palladiosimulator.simexp.pcm.compare;

import java.util.function.Predicate;

import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;

public class AttributeChangedFilter extends DiffFilter {

	public AttributeChangedFilter() {
		super();
	}

	@Override
	protected Predicate<Diff> satisfiesFilterCriteria() {
		return diff -> {
			boolean match = true;
			match &= (diff instanceof AttributeChange);
			match &= (diff.getKind() == DifferenceKind.CHANGE);
			return match;
		};
	}

	@Override
	protected String convertToString(Diff diff) {
		String name = ((AttributeChange) diff).getAttribute().getName();
		String value = ((AttributeChange) diff).getValue().toString();
		return String.format("Attribute: %1s, new value: %2s", name, value);
	}

}
