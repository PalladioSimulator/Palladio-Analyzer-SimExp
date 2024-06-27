package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public interface IConfigurationStatisticSink {
    void onRunStart(int run);

    void onRunFinish(int run);

    void onEntry(int run, WirelessLink link);

    void finalize();
}
