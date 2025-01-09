package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<T> {
    void accept(T t) throws IOException;
}
