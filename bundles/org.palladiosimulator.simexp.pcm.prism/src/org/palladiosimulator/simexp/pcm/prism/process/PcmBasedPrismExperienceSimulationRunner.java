package org.palladiosimulator.simexp.pcm.prism.process;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService.PrismResult;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public class PcmBasedPrismExperienceSimulationRunner<A, V> implements ExperienceSimulationRunner {
    private static final Logger LOGGER = Logger.getLogger(PcmBasedPrismExperienceSimulationRunner.class);

    private final PrismService prismService;
    private final PrismGenerator<A, V> prismGenerator;
    private final List<IPrismObserver> prismObservers;

    public PcmBasedPrismExperienceSimulationRunner(PrismGenerator<A, V> prismGenerator, Path prismLogPath) {
        // TODO exception handling
        this.prismService = ServiceRegistry.get()
            .findService(PrismService.class)
            .orElseThrow(() -> new RuntimeException("There is no prism service."));
        this.prismGenerator = prismGenerator;
        this.prismService.initialise(prismLogPath);
        this.prismObservers = new ArrayList<>();
    }

    public void addPrismObserver(IPrismObserver prismObserver) {
        prismObservers.add(prismObserver);
    }

    @Override
    public void simulate(State state) {
        PcmSelfAdaptiveSystemState<A, V> pcmState = PcmSelfAdaptiveSystemState.class.cast(state);
        PrismResult result = modelCheck(pcmState);
        retrieveAndSetStateQuantities(pcmState.getQuantifiedState(), result);
    }

    private PrismResult modelCheck(PcmSelfAdaptiveSystemState<A, V> sasState) {
        PrismResult result = new PrismResult();
        for (PrismSimulatedMeasurementSpec each : filterPrismSpecs(sasState)) {
            PrismContext context = prismGenerator.generate(sasState, each);
            preProcessContext(context, sasState);
            PrismResult resultToMerge = prismService.modelCheck(context);
            result.mergeWith(resultToMerge);
        }
        return result;
    }

    private void preProcessContext(PrismContext context, PcmSelfAdaptiveSystemState<A, V> sasState) {
        for (IPrismObserver prismObserver : prismObservers) {
            prismObserver.onContext(context, sasState.getName());
        }
    }

    private List<PrismSimulatedMeasurementSpec> filterPrismSpecs(PcmSelfAdaptiveSystemState<A, V> sasState) {
        return sasState.getQuantifiedState()
            .getMeasurementSpecs()
            .stream()
            .filter(PrismSimulatedMeasurementSpec.class::isInstance)
            .map(PrismSimulatedMeasurementSpec.class::cast)
            .collect(toList());
    }

    // Assuming that specification name is equal to property name.
    private void retrieveAndSetStateQuantities(StateQuantity quantity, PrismResult result) {
        for (SimulatedMeasurementSpecification each : quantity.getMeasurementSpecs()) {
            PrismSimulatedMeasurementSpec prismEach = (PrismSimulatedMeasurementSpec) each;
            File propertyFile = prismEach.getPropertyFile();
            try {
                String prismProperty = readPrismProperty(propertyFile.toPath());
                Optional<Double> value = result.getResultOf(prismProperty);
//                Optional<Double> value = result.getResultOf(each.getName());
                if (!value.isPresent()) {
                    throw new RuntimeException(String.format("property not found: %s", each.getName()));
                }
                Double measuredValue = value.get();
                LOGGER.debug(String.format("PRISM measurement %s = %s", each.getName(), measuredValue));
                quantity.setMeasurement(measuredValue, each);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private String readPrismProperty(Path propertyFile) throws IOException {
        List<String> lines = Files.readAllLines(propertyFile);
        Predicate<String> empty = String::isEmpty;
        Predicate<String> emptyRev = empty.negate();
        List<String> nonEmptyLines = lines.stream()
            .filter(emptyRev)
            .collect(Collectors.toList());
        return nonEmptyLines.get(0);
    }

}
