package org.palladiosimulator.simexp.core.store;

public interface BaseSimulatedExperienceAccessor extends AutoCloseable {

    @Override
    void close();
}
