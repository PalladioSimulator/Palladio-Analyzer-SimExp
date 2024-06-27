package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.MoteContextFilter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

import com.google.common.collect.Lists;

/*
 * This is a helper class for tracking the system configurations of deltaiot and thus only relevant for validation purposes.
 */
public class SystemConfigurationTracker {

    private static final Logger LOGGER = Logger.getLogger(SystemConfigurationTracker.class.getName());

    private final List<IStatisticSink> statisticSinks;
    private final SimulationParameters simulationParameters;

    private int run;

    public SystemConfigurationTracker(String strategyId, SimulationParameters simulationParameters) {
        this.simulationParameters = simulationParameters;
        this.statisticSinks = Lists.newArrayList();
        this.run = 0;
    }

    public void addStatisticSink(IStatisticSink sink) {
        statisticSinks.add(sink);
    }

    public void registerAndPrintNetworkConfig(SharedKnowledge knowledge) {
        printStartTracking();

        var moteFiler = new MoteContextFilter(knowledge);
        for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
            for (WirelessLink eachLink : eachMote.links) {
                print(eachLink);
                DeltaIoTSystemStatisticEntry statEntry = new DeltaIoTSystemStatisticEntry(eachLink, run);
                for (IStatisticSink sink : statisticSinks) {
                    sink.onEntry(statEntry);
                }
            }
        }

        printFinishTracking();

        run++;
    }

    public void saveNetworkConfigs() {
        for (IStatisticSink sink : statisticSinks) {
            sink.finalize();
        }
    }

    public void resetTrackedValues() {
        run = 0;
    }

    public boolean isLastRun() {
        return run == (simulationParameters.getNumberOfSimulationsPerRun() - 1);
    }

    private void printFinishTracking() {
        LOGGER.info("******** END *******");
    }

    private void printStartTracking() {
        LOGGER.info("******** Network configuration of " + run + " *******");
    }

    private void print(WirelessLink eachLink) {
        LOGGER.info(String.format("Link: %1s, Power: %2s, SNR:  %3s, Dist.: %4s", eachLink.pcmLink.getEntityName(),
                eachLink.transmissionPower, eachLink.SNR, eachLink.distributionFactor));
    }
}
