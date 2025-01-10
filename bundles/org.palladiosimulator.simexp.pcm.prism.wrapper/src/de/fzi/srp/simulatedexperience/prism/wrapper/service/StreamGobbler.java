package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

public class StreamGobbler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(StreamGobbler.class);

    private final InputStream inputStream;
    private final Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            bufferedReader.lines()
                .forEach(consumer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
