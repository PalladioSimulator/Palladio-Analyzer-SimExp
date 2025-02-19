package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTBaseEnvironemtalDynamics.isSNRTemplate;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTBaseEnvironemtalDynamics.toInputs;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

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

    public static void requirePcmSelfAdaptiveSystemState(State source) {
        if (PcmSelfAdaptiveSystemState.class.isInstance(source) == false) {
            throw new RuntimeException("The self-adaptive system state is not a PCM-based.");
        }
    }

    public static Map<AssemblyContext, Map<LinkingResource, Double>> filterMotesWithWirelessLinks(
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, PcmSelfAdaptiveSystemState state) {
        Comparator<AssemblyContext> comparator = Comparator.comparing(AssemblyContext::getId);
        Map<AssemblyContext, Map<LinkingResource, Double>> result = new TreeMap<>(comparator);
        Function<Map.Entry<LinkingResource, Double>, AssemblyContext> classifier = equalSourceMote(modelAccess, state);
        Map<LinkingResource, Double> linksWithSNRMap = filterLinksWithSNR(state);
        for (Map.Entry<LinkingResource, Double> entry : linksWithSNRMap.entrySet()) {
            AssemblyContext context = classifier.apply(entry);
            Map<LinkingResource, Double> linkResourceMap = result.get(context);
            if (linkResourceMap == null) {
                Comparator<LinkingResource> lrComparator = Comparator.comparing(LinkingResource::getId);
                linkResourceMap = new TreeMap<>(lrComparator);
            }
            linkResourceMap.put(entry.getKey(), entry.getValue());
            result.put(context, linkResourceMap);
        }

        return result;
    }

    private static Map<LinkingResource, Double> filterLinksWithSNR(PcmSelfAdaptiveSystemState state) {
        List<InputValue<CategoricalValue>> inputList = toInputs(state.getPerceivedEnvironmentalState()
            .getValue()
            .getValue());
        Comparator<LinkingResource> comparator = Comparator.comparing(LinkingResource::getId);
        Map<LinkingResource, Double> result = new TreeMap<>(comparator);
        for (InputValue<CategoricalValue> inputValue : inputList) {
            if (!isSNRTemplate(inputValue.getVariable())) {
                continue;
            }
            LinkingResource lr = (LinkingResource) inputValue.getVariable()
                .getAppliedObjects()
                .get(0);
            result.put(lr, getSNR(inputValue));
        }
        return result;
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
