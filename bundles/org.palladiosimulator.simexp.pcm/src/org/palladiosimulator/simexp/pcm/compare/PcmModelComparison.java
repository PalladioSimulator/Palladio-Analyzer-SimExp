package org.palladiosimulator.simexp.pcm.compare;

import static org.palladiosimulator.simexp.pcm.util.PcmUtil.stringConcatenation;
import static org.palladiosimulator.simexp.pcm.util.PcmUtil.toIdentifiables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.solver.models.PCMInstance;

public class PcmModelComparison {

	public static class ComparisonResult {

		private final List<Diff> foundDiffs;
		private final List<DiffFilter> diffFilter;

		private ComparisonResult(List<Diff> foundDiffs) {
			this.foundDiffs = foundDiffs;
			this.diffFilter = initDiffFilter();
		}

		private List<DiffFilter> initDiffFilter() {
			return Arrays.asList(new AttributeChangedFilter(), new PCMRandomVariableChangeFilter());
		}

		public static ComparisonResult of(List<Diff> foundDiffs) {
			return new ComparisonResult(foundDiffs);
		}

		private List<String> filterRelevantDiffs() {
			applyFilter();
			if (anyMatches()) {
				return getStringRepresentation();
			}
			return Arrays.asList("");
		}

		private List<String> getStringRepresentation() {
			return diffFilter.stream().filter(each -> each.anyMatch()).map(each -> each.getResult().get())
					.collect(Collectors.toList());
		}

		private boolean anyMatches() {
			return diffFilter.stream().anyMatch(filter -> filter.anyMatch());
		}

		private void applyFilter() {
			diffFilter.forEach(filter -> filter.applyTo(foundDiffs));
		}

		@Override
		public String toString() {
			return filterRelevantDiffs().stream().reduce(stringConcatenation()).orElse("");
		}

	}

	private class ComparisonPreparer {

		List<Pair<Resource, Resource>> resourcesToCompare = new ArrayList<>();

		public Stream<Pair<Resource, Resource>> getResourcesToCompare(PCMInstance other) {
			checkConsistency(pcmModel, other);

			add(pcmModel.getAllocation().eResource(), other.getAllocation().eResource());
			add(pcmModel.getSystem().eResource(), other.getSystem().eResource());
			add(pcmModel.getResourceEnvironment().eResource(), other.getResourceEnvironment().eResource());
			for (int i = 0; i < pcmModel.getRepositories().size(); i++) {
				add(pcmModel.getRepositories().get(i).eResource(), other.getRepositories().get(i).eResource());
			}

			return resourcesToCompare.stream();
		}

		private void checkConsistency(PCMInstance first, PCMInstance second) {
			if (isNotConsistent(first, second)) {
				// TODO exception handling
				throw new RuntimeException("");
			}
		}

		private boolean isNotConsistent(PCMInstance first, PCMInstance second) {
			Pair<PCMInstance, PCMInstance> pair = new Pair<PCMInstance, PCMInstance>(first, second);
			return getConditionsToCheck().allMatch(condition -> condition.test(pair)) == false;
		}

		private Stream<Predicate<Pair<PCMInstance, PCMInstance>>> getConditionsToCheck() {
			List<Predicate<Pair<PCMInstance, PCMInstance>>> conditions = new ArrayList<>();
			conditions.add(pair -> areEqual(pair.getFirst().getAllocation().getId(),
					pair.getSecond().getAllocation().getId()));
			conditions.add(pair -> areEqual(pair.getFirst().getResourceEnvironment().getEntityName(),
					pair.getSecond().getResourceEnvironment().getEntityName()));
			conditions.add(pair -> areEqual(pair.getFirst().getSystem().getId(), pair.getSecond().getSystem().getId()));
			conditions.add(pair -> areEqual(toIdentifiables(pair.getFirst().getRepositories()),
					toIdentifiables(pair.getSecond().getRepositories())));
			return conditions.stream();
		}

		private boolean areEqual(String firstId, String secondId) {
			return firstId.equals(secondId);
		}

		private boolean areEqual(List<String> firstIds, List<String> secondIds) {
			if (firstIds.size() != secondIds.size()) {
				return false;
			}

			for (int i = 0; i < firstIds.size(); i++) {
				if (areEqual(firstIds.get(i), secondIds.get(i)) == false) {
					return false;
				}
			}
			return true;
		}

		private void add(Resource first, Resource second) {
			resourcesToCompare.add(new Pair<Resource, Resource>(first, second));
		}

	}

	private final PCMInstance pcmModel;

	private PcmModelComparison(PCMInstance pcmModel) {
		this.pcmModel = pcmModel;
	}

	public static PcmModelComparison of(PCMInstance pcmModel) {
		return new PcmModelComparison(pcmModel);
	}

	public ComparisonResult compareTo(PCMInstance other) {
		return ComparisonResult.of(determineDifferences(other));
	}

	private List<Diff> determineDifferences(PCMInstance other) {
		return new ComparisonPreparer().getResourcesToCompare(other)
				.flatMap(each -> compare(each).getDifferences().stream()).collect(Collectors.toList());
	}

	private Comparison compare(Pair<Resource, Resource> resourcesToCompare) {
		IComparisonScope scope = new DefaultComparisonScope(resourcesToCompare.getFirst(),
				resourcesToCompare.getSecond(), null);
		return EMFCompare.builder().build().compare(scope);
	}

}
