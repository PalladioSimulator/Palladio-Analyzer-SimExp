package org.palladiosimulator.simexp.pcm.prism.process;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService.PrismResult;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public class PcmBasedPrismExperienceSimulationRunner implements ExperienceSimulationRunner {

	private final static String LOG_FILE_EXT = ".txt";
	private final static String LOG_FILE_NAME = "PrismLog";
	private final static String LOG_FILE = LOG_FILE_NAME + LOG_FILE_EXT;

	private final PrismService prismService;
	private final PrismGenerator prismGenerator;

	public PcmBasedPrismExperienceSimulationRunner(PrismGenerator prismGenerator, File prismFolder) {
		// TODO exception handling
		this.prismService = ServiceRegistry.get().findService(PrismService.class)
				.orElseThrow(() -> new RuntimeException("There is no prism service."));
		this.prismGenerator = prismGenerator;

		this.prismService.initialise(createLogFile(prismFolder));
	}

	private File createLogFile(File prismFolder) {
		File prismLog = Paths.get(prismFolder.toString(), LOG_FILE).toFile();
		if (prismLog.exists() == false) {
			try {
				prismLog.createNewFile();
			} catch (IOException e) {
				// TODO Exception handling
				throw new RuntimeException("Prism log file cannot be created.", e);
			}
		}
		return prismLog;
	}

	@Override
	public void initSimulationRun() {
		ExperimentProvider.get().initializeExperimentRunner();
		QVToReconfigurationManager.get().resetReconfigurator();
	}

	@Override
	public void simulate(SelfAdaptiveSystemState<?> sasState) {
		if (PcmSelfAdaptiveSystemState.class.isInstance(sasState) == false) {
			// TODO Exception handling
			throw new RuntimeException();
		}

		PrismResult result = modelCheck(PcmSelfAdaptiveSystemState.class.cast(sasState));
		retrieveAndSetStateQuantities(sasState.getQuantifiedState(), result);
	}

	private PrismResult modelCheck(PcmSelfAdaptiveSystemState sasState) {
		PrismResult result = new PrismResult();
		for (PrismSimulatedMeasurementSpec each : filterPrismSpecs(sasState)) {
			PrismResult resultToMerge = prismService.modelCheck(prismGenerator.generate(sasState, each));
			result.mergeWith(resultToMerge);
		}
		return result;
	}

	private List<PrismSimulatedMeasurementSpec> filterPrismSpecs(PcmSelfAdaptiveSystemState sasState) {
		return sasState.getQuantifiedState().getMeasurementSpecs().stream()
				.filter(PrismSimulatedMeasurementSpec.class::isInstance).map(PrismSimulatedMeasurementSpec.class::cast)
				.collect(toList());
	}

	// Assuming that specification name is equal to property name.
	private void retrieveAndSetStateQuantities(StateQuantity quantity, PrismResult result) {
		for (SimulatedMeasurementSpecification each : quantity.getMeasurementSpecs()) {
			Optional<Double> value = result.getResultOf(each.getName());
			if (value.isPresent()) {
				quantity.setMeasurement(value.get(), each);
			} else {
				// TODO logging
			}
		}
	}

}
