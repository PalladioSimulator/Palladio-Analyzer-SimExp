package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent;

import java.util.concurrent.FutureTask;

public class SettableFutureTask<V> extends FutureTask<V> {

    public SettableFutureTask(Runnable runnable, V result) {
        super(runnable, result);
    }

    public void setResult(V v) {
        set(v);
    }
}
