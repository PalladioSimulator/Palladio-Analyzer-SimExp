package org.palladiosimulator.simexp.pcm.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.compare.Diff;

public abstract class DiffFilter {
	
	protected final List<Diff> result;
	
	public DiffFilter() {
		result = new ArrayList<Diff>();
	}
	
	protected boolean anyMatch() {
		return result.isEmpty() == false;
	}
	
	public abstract void applyTo(List<Diff> diffsToFilter);
	
	public abstract Optional<String> getResult();
	
}
