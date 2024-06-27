package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public class DeltaIotSystemStatisticConsoleSink implements IStatisticSink {
    private static final Logger LOGGER = Logger.getLogger(DeltaIotSystemStatisticConsoleSink.class);

    public DeltaIotSystemStatisticConsoleSink() {
    }

    @Override
    public void onRunStart(int run) {
        LOGGER.info("******** Network configuration of " + run + " *******");
    }

    @Override
    public void onRunFinish(int run) {
        LOGGER.info("******** END *******");
    }

    @Override
    public void onEntry(DeltaIoTSystemStatisticEntry entry) {
        WirelessLink link = entry.getLink();
        LOGGER.info(String.format("Link: %1s, Power: %2s, SNR:  %3s, Dist.: %4s", link.pcmLink.getEntityName(),
                link.transmissionPower, link.SNR, link.distributionFactor));
    }

    @Override
    public void finalize() {
    }
}
