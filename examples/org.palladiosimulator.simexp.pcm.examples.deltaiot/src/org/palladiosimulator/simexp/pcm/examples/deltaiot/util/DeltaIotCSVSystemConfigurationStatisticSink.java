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
    public void initialize() throws IOException {
        String[] HEADERS = { "Run", "Link", "Power", "Distribution" };

        CSVFormat csvFormat = CSVFormat.newFormat(';')
            .withHeader(HEADERS)
            .withRecordSeparator("\r\n");

        Writer writer = Files.newBufferedWriter(outputPath);
        printer = new CSVPrinter(writer, csvFormat);
    }

    @Override
    public void onEntry(int run, WirelessLink link) throws IOException {
        printer.printRecord(run, link.pcmLink.getEntityName(), link.transmissionPower, link.distributionFactor);
    }

    @Override
    public void finalize() throws IOException {
        try {
            printer.close();
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
