package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

public interface IStatisticSink {
    void onRunStart(int run);

    void onRunFinish(int run);

    void onEntry(DeltaIoTSystemStatisticEntry entry);

    void finalize();
}
