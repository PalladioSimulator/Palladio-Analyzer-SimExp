package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

import com.google.common.collect.Lists;

public class DeltaIotSystemConfigurationCSVWriter implements IStatisticSink {
    private final Path outputPath;
    private final List<DeltaIoTSystemStatisticEntry> sysConfigurations;

    public DeltaIotSystemConfigurationCSVWriter(Path outputPath) {
        this.outputPath = outputPath;
        this.sysConfigurations = Lists.newArrayList();
    }

    @Override
    public void onEntry(DeltaIoTSystemStatisticEntry entry) {
        sysConfigurations.add(entry);
    }

    @Override
    public void finalize() {
        try {
            writeOut(sysConfigurations);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sysConfigurations.clear();
        }
    }

    private void writeOut(List<DeltaIoTSystemStatisticEntry> entries) throws IOException {
        String[] HEADERS = { "Run", "Link", "Power", "Distribution" };

        CSVFormat csvFormat = CSVFormat.newFormat(';')
            .withHeader(HEADERS)
            .withRecordSeparator("\r\n");

        try (Writer writer = Files.newBufferedWriter(outputPath)) {
            try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
                for (DeltaIoTSystemStatisticEntry entry : entries) {
                    WirelessLink link = entry.getLink();
                    printer.printRecord(entry.getSimulationRun(), link.pcmLink.getEntityName(), link.transmissionPower,
                            link.distributionFactor);
                }
            }
        }
    }

    @Override
    public void onRunStart(int run) {
    }

    @Override
    public void onRunFinish(int run) {
    }
}
