package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.filterMotesWithWirelessLinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTSampleLogger implements SampleDumper {
    private static final Logger LOGGER = Logger.getLogger(DeltaIoTSampleLogger.class);

    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;

    public DeltaIoTSampleLogger(DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        this.modelAccess = modelAccess;
    }

    @Override
    public void dump(State source) {
        onRunStart();

        PcmSelfAdaptiveSystemState<QVTOReconfigurator, List<InputValue<CategoricalValue>>> state = PcmSelfAdaptiveSystemState.class
            .cast(source);
        Map<AssemblyContext, Map<LinkingResource, Double>> motesToLinks = filterMotesWithWirelessLinks(modelAccess,
                state);
        List<WirelessLink> links = new ArrayList<>();
        for (AssemblyContext each : motesToLinks.keySet()) {
            MoteContext moteContext = new MoteContext(modelAccess, each, motesToLinks.get(each));
            for (WirelessLink eachLink : moteContext.links) {
                links.add(eachLink);
            }
        }

        List<WirelessLink> sortedLinks = links.stream()
            .sorted((l1, l2) -> l1.pcmLink.getEntityName()
                .compareTo(l2.pcmLink.getEntityName()))
            .collect(Collectors.toList());

        Integer maxName = links.stream()
            .mapToInt(v -> v.pcmLink.getEntityName()
                .length())
            .max()
            .orElseThrow(NoSuchElementException::new);

        for (WirelessLink eachLink : sortedLinks) {
            onEntry(eachLink, maxName);
        }
        onRunFinish();
    }

    private void onRunStart() {
        LOGGER.info("******** Network configuration *******");
    }

    private void onRunFinish() {
        LOGGER.info("******** END *******");
    }

    private void onEntry(WirelessLink link, int maxName) {
        LOGGER.info(String.format("Link %-" + String.format("%d", maxName) + "s Power: %2s, SNR: % 22.18f, Dist.: %4s",
                link.pcmLink.getEntityName(), link.transmissionPower, link.SNR, link.distributionFactor));
    }
}
