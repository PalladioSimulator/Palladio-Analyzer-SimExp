package org.palladiosimulator.simexp.pcm.examples.deltaiot.util;

public interface IStatisticSink {
    void onEntry(DeltaIoTSystemStatisticEntry entry);

    void finalize();
}
