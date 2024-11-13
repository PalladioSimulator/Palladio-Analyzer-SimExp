package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import java.io.IOException;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public interface IConfigurationStatisticSink {
    void initialize() throws IOException;

    void onRunStart(int run) throws IOException;

    void onRunFinish(int run) throws IOException;

    void onEntry(int run, WirelessLink link) throws IOException;

    void finalize() throws IOException;
}
