package org.palladiosimulator.simexp.pcm.util;

import static org.palladiosimulator.simexp.pcm.util.InitialPcmPartitionLoader.loadInitialPcmPartition;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.commons.emfutils.EMFCopyHelper;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.experimentautomation.experiments.InitialModel;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class ExperimentProvider {

	private final static int DEFAULT_REPETITIONS = 1;

	private class ExperimentCopier {

		public Experiment makeCopy() {
			Experiment copy = retrieveExperiment(copyExperimentRepository());
			addPcmModels(copy.getInitialModel());
			return copy;
		}

		private Experiment retrieveExperiment(ExperimentRepository experimentRepo) {
			// TODO exception handling
			return findElement(experimentRepo.getExperiments(), exp -> exp.getId().equals(initialExperiment.getId()))
					.orElseThrow(() -> new RuntimeException(""));
		}

		private ExperimentRepository copyExperimentRepository() {
			ResourceSet originalRs = new ResourceSetImpl();
			originalRs.getResources().add(initialExperiment.eResource());

			Resource rCopy = new ResourceSetImpl().createResource(URI.createFileURI("/temp"));
			rCopy.getContents().add(EMFCopyHelper.deepCopyToEObjectList(originalRs).get(0));

			return (ExperimentRepository) rCopy.getContents().get(0);
		}
	
		private void addPcmModels(InitialModel initCopy) {
			addImmutableModels(initCopy);
			addMutableModels(initCopy);
		}

		private void addImmutableModels(InitialModel initCopy) {
			Optional.ofNullable(initialExperiment.getInitialModel().getEventMiddleWareRepository())
					.ifPresent(v -> initCopy.setEventMiddleWareRepository(v));
			Optional.ofNullable(initialExperiment.getInitialModel().getMiddlewareRepository())
					.ifPresent(v -> initCopy.setMiddlewareRepository(v));
			Optional.ofNullable(initialExperiment.getInitialModel().getReconfigurationRules())
					.ifPresent(v -> initCopy.setReconfigurationRules(v));
			Optional.ofNullable(initialExperiment.getInitialModel().getServiceLevelObjectives())
					.ifPresent(v -> initCopy.setServiceLevelObjectives(v));
			Optional.ofNullable(initialExperiment.getInitialModel().getMonitorRepository())
					.ifPresent(v -> initCopy.setMonitorRepository(v));
		}

		private void addMutableModels(InitialModel initCopy) {
			PCMResourceSetPartition pcmCopy = PcmUtil.copyPCMPartition(initialPartition);
			
			initCopy.setAllocation(pcmCopy.getAllocation());
			initCopy.setRepository(retrieveRepository(pcmCopy.getRepositories()));
			initCopy.setResourceEnvironment(pcmCopy.getResourceEnvironment());
			initCopy.setSystem(pcmCopy.getSystem());

			if (initialExperiment.getToolConfiguration().isEmpty()) {
				getUsageModel(pcmCopy).ifPresent(initCopy::setUsageModel);
			} else {
				initCopy.setUsageModel(pcmCopy.getUsageModel());
			}
		}

		private Optional<UsageModel> getUsageModel(PCMResourceSetPartition pcmCopy) {
			try {
				return Optional.of(pcmCopy.getUsageModel());
			} catch (IndexOutOfBoundsException e) {
				return Optional.empty();
			}
		}
		
		private Repository retrieveRepository(List<Repository> repositories) {
			// TODO exception handling
			return findElement(repositories,
					r -> r.getId().equals(initialExperiment.getInitialModel().getRepository().getId()))
							.orElseThrow(() -> new RuntimeException(""));
		}

		private <T> Optional<T> findElement(Collection<T> elements, Predicate<T> criterion) {
			return elements.stream().filter(criterion).findFirst();
		}

	}

	private ExperimentRunner currentRunner;

	private final Experiment initialExperiment;
	private final PCMResourceSetPartition initialPartition;

	private static ExperimentProvider providerInstance;

	private ExperimentProvider(Experiment initialExperiment) {
		this.initialExperiment = normalizeExperiment(initialExperiment);
		this.initialPartition = loadInitialPcmPartition(initialExperiment);
		this.currentRunner = new ExperimentRunner(getInitialExperiment());
	}

	public static void create(Experiment initialExperiment) {
		if (providerInstance == null) {
			providerInstance = new ExperimentProvider(initialExperiment);
		}
	}

	public static ExperimentProvider get() {
		return Objects.requireNonNull(providerInstance, "Blackboard has not been initialized yet.");
	}

	public void initializeExperimentRunner() {
		currentRunner = new ExperimentRunner(getInitialExperiment());
	}

	public MDSDBlackboard getCurrentBlackboard() {
		return currentRunner.getBlackboard();
	}

	public ExperimentRunner getExperimentRunner() {
		return currentRunner;
	}

	private Experiment getInitialExperiment() {
		return new ExperimentCopier().makeCopy();
	}

	private Experiment normalizeExperiment(Experiment experiment) {
		experiment.setRepetitions(DEFAULT_REPETITIONS);
		experiment.getInitialModel().setUsageEvolution(null);
		return experiment;
	}

}
