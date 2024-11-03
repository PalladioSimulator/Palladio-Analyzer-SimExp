package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public class DeltaIotConsoleSystemConfigurationStatisticSink implements IConfigurationStatisticSink {
    private static final Logger LOGGER = Logger.getLogger(DeltaIotConsoleSystemConfigurationStatisticSink.class);

    private final List<WirelessLink> links = new ArrayList<>();

    @Override
    public void initialize() {
    }

    @Override
    public void onRunStart(int run) {
        LOGGER.info("******** Network configuration *******");
        links.clear();
    }

    @Override
    public void onRunFinish(int run) {
        List<WirelessLink> sortedLinks = links.stream()
            .sorted((l1, l2) -> l1.pcmLink.getEntityName()
                .compareTo(l2.pcmLink.getEntityName()))
            .collect(Collectors.toList());

        Integer maxName = links.stream()
            .mapToInt(v -> v.pcmLink.getEntityName()
                .length())
            .max()
            .orElseThrow(NoSuchElementException::new);

        for (WirelessLink link : sortedLinks) {
            onEntry(link, maxName);
        }
        LOGGER.info("******** END *******");
    }

    private void onEntry(WirelessLink link, int maxName) {
        LOGGER.info(String.format("Link %-" + String.format("%d", maxName) + "s Power: %2s, SNR: % 22.18f, Dist.: %4s",
                link.pcmLink.getEntityName(), link.transmissionPower, link.SNR, link.distributionFactor));
    }

    @Override
    public void onEntry(int run, WirelessLink link) {
        links.add(link);
    }

    @Override
    public void finalize() {
    }
}
