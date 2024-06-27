package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public class DeltaIotCSVSystemConfigurationStatisticSink implements IConfigurationStatisticSink {
    private final Path outputPath;

    private CSVPrinter printer;

    public DeltaIotCSVSystemConfigurationStatisticSink(Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void initialize() {
        String[] HEADERS = { "Run", "Link", "Power", "Distribution" };

        CSVFormat csvFormat = CSVFormat.newFormat(';')
            .withHeader(HEADERS)
            .withRecordSeparator("\r\n");

        try {
            Writer writer = Files.newBufferedWriter(outputPath);
            printer = new CSVPrinter(writer, csvFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEntry(int run, WirelessLink link) {
        try {
            printer.printRecord(run, link.pcmLink.getEntityName(), link.transmissionPower, link.distributionFactor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finalize() {
        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            printer = null;
        }
    }

    @Override
    public void onRunStart(int run) {
    }

    @Override
    public void onRunFinish(int run) {
    }
}
