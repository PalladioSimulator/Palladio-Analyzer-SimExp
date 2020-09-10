package org.palladiosimulator.simexp.pcm.compare;

import java.util.function.Predicate;

import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.ReferenceChange;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

public class ComponentInstantiationChangeFilter extends DiffFilter {

	private final static String REFERENCE_NAME = "encapsulatedComponent__AssemblyContext";
	
	@Override
	protected Predicate<Diff> satisfiesFilterCriteria() {
		return diff -> {
			if (diff instanceof ReferenceChange) {
				return hasCorrectReferenceName((ReferenceChange) diff);
			}
			return false;
		};
	}

	@Override
	protected String convertToString(Diff diff) {
		RepositoryComponent component = (RepositoryComponent) ReferenceChange.class.cast(diff).getValue();
		return String.format("Changed component: %s", component.getEntityName());
	}
	
	private boolean hasCorrectReferenceName(ReferenceChange diff) {
		return diff.getReference().getName().equals(REFERENCE_NAME);
	}

}
