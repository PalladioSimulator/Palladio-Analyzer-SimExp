package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import java.util.List;

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

    private final List<IConfigurationStatisticSink> configurationStatisticSinks;
    private final SimulationParameters simulationParameters;

    private int run;

    public SystemConfigurationTracker(SimulationParameters simulationParameters) {
        this.simulationParameters = simulationParameters;
        this.configurationStatisticSinks = Lists.newArrayList();
        this.run = 0;
    }

    public void addStatisticSink(IConfigurationStatisticSink sink) {
        configurationStatisticSinks.add(sink);
    }

    public void prepareNetworkConfig() {
        if (run > 0) {
            return;
        }
        for (IConfigurationStatisticSink sink : configurationStatisticSinks) {
            sink.initialize();
        }
    }

    public void processNetworkConfig(SharedKnowledge knowledge) {
        runStart();

        var moteFiler = new MoteContextFilter(knowledge);
        for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
            for (WirelessLink eachLink : eachMote.links) {
                for (IConfigurationStatisticSink sink : configurationStatisticSinks) {
                    sink.onEntry(run, eachLink);
                }
            }
        }

        runFinish();

        run++;

        if (isLastRun()) {
            saveNetworkConfigs();
            run = 0;
        }
    }

    private void saveNetworkConfigs() {
        for (IConfigurationStatisticSink sink : configurationStatisticSinks) {
            sink.finalize();
        }
    }

    private boolean isLastRun() {
        return run == (simulationParameters.getNumberOfSimulationsPerRun() - 1);
    }

    private void runStart() {
        for (IConfigurationStatisticSink sink : configurationStatisticSinks) {
            sink.onRunStart(run);
        }
    }

    private void runFinish() {
        for (IConfigurationStatisticSink sink : configurationStatisticSinks) {
            sink.onRunFinish(run);
        }
    }
}
