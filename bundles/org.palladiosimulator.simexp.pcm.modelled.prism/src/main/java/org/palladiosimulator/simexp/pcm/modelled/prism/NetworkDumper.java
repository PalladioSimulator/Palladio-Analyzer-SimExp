package org.palladiosimulator.simexp.pcm.modelled.prism;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.filterMotesWithWirelessLinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class NetworkDumper implements Monitor {
    private static final Logger LOGGER = Logger.getLogger(NetworkDumper.class);

    private final Monitor delegate;
    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;

    public NetworkDumper(Monitor delegate, DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        this.delegate = delegate;
        this.modelAccess = modelAccess;
    }

    @Override
    public void monitor(State source, SharedKnowledge knowledge) {
        delegate.monitor(source, knowledge);
        dump(source, knowledge);
    }

    private void dump(State source, SharedKnowledge knowledge) {
        onRunStart();

        PcmSelfAdaptiveSystemState<QVTOReconfigurator, List<InputValue<CategoricalValue>>> state = PcmSelfAdaptiveSystemState.class
            .cast(source);
        Map<AssemblyContext, Map<LinkingResource, Double>> motesToLinks = filterMotesWithWirelessLinks(modelAccess,
                state);
        List<MoteContext> moteContexts = new ArrayList<>();
        for (AssemblyContext each : motesToLinks.keySet()) {
            var moteContext = new MoteContext(modelAccess, each, motesToLinks.get(each));
            moteContexts.add(moteContext);
        }

        for (MoteContext eachMote : moteContexts) {
            for (WirelessLink eachLink : eachMote.links) {
                onEntry(eachLink);
            }
        }
        onRunFinish();
    }

    private void onRunStart() {
        LOGGER.info("******** Network configuration *******");
    }

    private void onRunFinish() {
        LOGGER.info("******** END *******");
    }

    private void onEntry(WirelessLink link) {
        LOGGER.info(String.format("Link: %1s, Power: %2s, SNR:  %3s, Dist.: %4s", link.pcmLink.getEntityName(),
                link.transmissionPower, link.SNR, link.distributionFactor));
    }
}
