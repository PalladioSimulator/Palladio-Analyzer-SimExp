package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public class DeltaIotConsoleSystemConfigurationStatisticSink implements IConfigurationStatisticSink {
    private static final Logger LOGGER = Logger.getLogger(DeltaIotConsoleSystemConfigurationStatisticSink.class);

    @Override
    public void initialize() {
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
    public void onEntry(int run, WirelessLink link) {
        LOGGER.info(String.format("Link: %1s, Power: %2s, SNR:  %3s, Dist.: %4s", link.pcmLink.getEntityName(),
                link.transmissionPower, link.SNR, link.distributionFactor));
    }

    @Override
    public void finalize() {
    }
}
