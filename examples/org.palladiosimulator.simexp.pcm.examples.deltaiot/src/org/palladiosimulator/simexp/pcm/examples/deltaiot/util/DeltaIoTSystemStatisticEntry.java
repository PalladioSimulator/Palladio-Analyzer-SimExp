package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public class DeltaIoTSystemStatisticEntry {

    private final WirelessLink link;
    private final int simulationRun;

    public DeltaIoTSystemStatisticEntry(WirelessLink link, int simulationRun) {
        this.link = link;
        this.simulationRun = simulationRun;
    }

    public WirelessLink getLink() {
        return link;
    }

    public int getSimulationRun() {
        return simulationRun;
    }

}
