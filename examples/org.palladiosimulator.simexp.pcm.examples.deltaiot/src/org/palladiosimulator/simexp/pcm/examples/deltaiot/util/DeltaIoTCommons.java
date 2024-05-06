package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTBaseEnvironemtalDynamics.toInputs;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.environment.DeltaIoTEnvironemtalDynamics.isSNRTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTCommons {

    public final static String STATE_KEY = "PCM_STATE";
    public final static String OPTIONS_KEY = "OPTIONS";
    public final static String PACKET_LOSS_KEY = "PacketLoss";
    public final static String ENERGY_CONSUMPTION_KEY = "EnergyConsumption";
    public final static double UPPER_BOUND_PACKET_LOSS = 0.2;
    public final static double LOWER_BOUND_PACKET_LOSS = 0.025;
    public final static double BOUND_PACKET_LOSS = 0.1;
    public final static Threshold LOWER_PACKET_LOSS = Threshold.lessThan(BOUND_PACKET_LOSS);
    public final static double UPPER_BOUND_ENERGY_CONSUMPTION = 34.5;
    public final static double AVG_BOUND_ENERGY_CONSUMPTION = 32;
    public final static double LOWER_BOUND_ENERGY_CONSUMPTION = 30.5;
    public final static Threshold LOWER_ENERGY_CONSUMPTION = Threshold.lessThan(AVG_BOUND_ENERGY_CONSUMPTION);
    public final static double DISTRIBUTION_FACTOR_INCREMENT = 0.1;
    public final static int TRANSMISSION_POWER_INCREMENT = 1;
    public final static String PRISM_PACKET_LOSS_PROPERTY = "P=? [ F \"Packetloss\" ]";
    public final static String PRISM_ENERGY_CONSUMPTION_PROPERTY = "Rmax=? [ F \"EnergyConsumption\" ]";

    public static PcmSelfAdaptiveSystemState findPcmState(SharedKnowledge knowledge) {
        return (PcmSelfAdaptiveSystemState) knowledge.getValue(STATE_KEY)
            .orElseThrow();
    }

    public static Set<QVToReconfiguration> findOptions(SharedKnowledge knowledge) {
        Set<?> options = (Set<?>) knowledge.getValue(OPTIONS_KEY)
            .orElseThrow();
        return options.stream()
            .filter(QVToReconfiguration.class::isInstance)
            .map(QVToReconfiguration.class::cast)
            .collect(Collectors.toSet());
    }

    public static DistributionFactorReconfiguration retrieveDistributionFactorReconfiguration(
            SharedKnowledge knowledge) {
        return retrieveReconfiguration(DistributionFactorReconfiguration.class, findOptions(knowledge))
            .orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
    }

    public static TransmissionPowerReconfiguration retrieveTransmissionPowerReconfiguration(SharedKnowledge knowledge) {
        return retrieveReconfiguration(TransmissionPowerReconfiguration.class, findOptions(knowledge))
            .orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
    }

    public static DeltaIoTNetworkReconfiguration retrieveDeltaIoTNetworkReconfiguration(SharedKnowledge knowledge) {
        return retrieveReconfiguration(DeltaIoTNetworkReconfiguration.class, findOptions(knowledge))
            .orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
    }

    public static DeltaIoTNetworkReconfiguration retrieveDeltaIoTNetworkReconfiguration(
            Set<QVToReconfiguration> options) {
        return retrieveReconfiguration(DeltaIoTNetworkReconfiguration.class, options)
            .orElseThrow(() -> new RuntimeException("There is no distribution factor reconfiguration registered."));
    }

    private static <T extends QVToReconfiguration> Optional<T> retrieveReconfiguration(Class<T> reconfClass,
            Set<QVToReconfiguration> options) {
        return options.stream()
            .filter(reconfClass::isInstance)
            .map(reconfClass::cast)
            .findFirst();
    }

    public static void requirePcmSelfAdaptiveSystemState(State source) {
        if (PcmSelfAdaptiveSystemState.class.isInstance(source) == false) {
            throw new RuntimeException("The self-adaptive system state is not a PCM-based.");
        }
    }

    public static Map<AssemblyContext, Map<LinkingResource, Double>> filterMotesWithWirelessLinks(
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, PcmSelfAdaptiveSystemState state) {
        return filterLinksWithSNR(state).entrySet()
            .stream()
            .collect(groupingBy(equalSourceMote(modelAccess, state),
                    mapping(Function.identity(), toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    private static Map<LinkingResource, Double> filterLinksWithSNR(PcmSelfAdaptiveSystemState state) {
        return toInputs(state.getPerceivedEnvironmentalState()
            .getValue()
            .getValue()).stream()
                .filter(each -> isSNRTemplate(each.getVariable()))
                .collect(toMap(k -> (LinkingResource) k.getVariable()
                    .getAppliedObjects()
                    .get(0), v -> getSNR(v)));
    }

    private static Double getSNR(InputValue input) {
        String value = CategoricalValue.class.cast(input.getValue())
            .get();
        return Double.valueOf(value);
    }

    private static Function<Map.Entry<LinkingResource, Double>, AssemblyContext> equalSourceMote(
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, PcmSelfAdaptiveSystemState state) {
        return entry -> modelAccess.findSourceMote(entry.getKey(), state.getArchitecturalConfiguration());
    }

}
