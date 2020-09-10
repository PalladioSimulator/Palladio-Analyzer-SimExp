package org.palladiosimulator.simexp.pcm.compare;

import static org.palladiosimulator.simexp.pcm.util.PcmUtil.stringConcatenation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.compare.Diff;

public abstract class DiffFilter {

	protected final List<Diff> result;

	public DiffFilter() {
		result = new ArrayList<Diff>();
	}

	protected boolean anyMatch() {
		return result.isEmpty() == false;
	}

	public void applyTo(List<Diff> diffsToFilter) {
		List<Diff> result = diffsToFilter.stream().filter(satisfiesFilterCriteria()).collect(Collectors.toList());
		if (result.isEmpty() == false) {
			this.result.addAll(result);
		}
	}

	public Optional<String> getResult() {
		return anyMatch() ? Optional.of(convertToString()) : Optional.empty();
	}

	private String convertToString() {
		return result.stream().map(this::convertToString).reduce(stringConcatenation()).get();
	}

	protected abstract Predicate<Diff> satisfiesFilterCriteria();
	
	protected abstract String convertToString(Diff diff);

}
