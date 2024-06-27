package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
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

    private final static String CSV_DELIMITER = ";";
    private final static String SIMULATED_EXPERIENCE_BASE_FOLDER = Paths.get(ResourcesPlugin.getWorkspace()
        .getRoot()
        .getLocation()
        .toString(), "resource", "DeltaIoT")
        .toString();
    private final static String FILE_SUFFIX = "Configurations.csv";

    private final String strategyId;
    private final List<String> configurations;
    private final List<DeltaIoTSystemStatisticEntry> sysConfigurations;
    private final SimulationParameters simulationParameters;

    private int run;

    public SystemConfigurationTracker(String strategyId, SimulationParameters simulationParameters) {
        this.strategyId = strategyId;
        this.simulationParameters = simulationParameters;
        this.configurations = Lists.newArrayList();
        this.sysConfigurations = Lists.newArrayList();
        this.run = 0;
    }

    public void registerAndPrintNetworkConfig(SharedKnowledge knowledge) {
        if (configurations.isEmpty()) {
            registerHeader();
        }

        printStartTracking();

        var moteFiler = new MoteContextFilter(knowledge);
        for (MoteContext eachMote : moteFiler.getAllMoteContexts()) {
            for (WirelessLink eachLink : eachMote.links) {
                print(eachLink);
                register(eachLink);
            }
        }

        printFinishTracking();

        run++;
    }

    public void saveNetworkConfigs() {
        var location = getFileLocation(strategyId);
        var csvOutputFile = new File(location);

        var csvFileExists = csvOutputFile.exists();
        if (csvFileExists == false) {
            try {
                csvOutputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(location, csvFileExists))) {
            if (csvFileExists) {
                configurations.remove(0);
            }
            configurations.forEach(pw::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetTrackedValues() {
        configurations.clear();
        run = 0;
    }

    public boolean isLastRun() {
        return run == (simulationParameters.getNumberOfSimulationsPerRun() - 1);
    }

    private void registerHeader() {
        var header = new StringBuilder().append("Run")
            .append(CSV_DELIMITER)
            .append("Link")
            .append(CSV_DELIMITER)
            .append("Power")
            .append(CSV_DELIMITER)
            .append("Distribution")
            .toString();
        configurations.add(header);
    }

    private void register(WirelessLink link) {
        String configuration = new StringBuilder().append(run)
            .append(CSV_DELIMITER)
            .append(link.pcmLink.getEntityName())
            .append(CSV_DELIMITER)
            .append(link.transmissionPower)
            .append(CSV_DELIMITER)
            .append(link.distributionFactor)
            .toString();
        configurations.add(configuration);

        DeltaIoTSystemStatisticEntry configItem = new DeltaIoTSystemStatisticEntry(link, run);
        sysConfigurations.add(configItem);
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

    private String getFileLocation(String strategyId) {
        var csvFileName = strategyId + FILE_SUFFIX;
        return Paths.get(SIMULATED_EXPERIENCE_BASE_FOLDER, csvFileName)
            .toString();
    }

}
