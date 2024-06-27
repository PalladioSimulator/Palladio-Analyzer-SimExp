package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.MoteContext.WirelessLink;

public interface IStatisticSink {
    void onRunStart(int run);

    void onRunFinish(int run);

    void onEntry(int run, WirelessLink link);

    void finalize();
}
