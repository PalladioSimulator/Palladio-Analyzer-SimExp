package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

public interface IStatisticSink {
    void onRunStart(int run);

    void onRunFinish(int run);

    void onEntry(int run, DeltaIoTSystemStatisticEntry entry);

    void finalize();
}
