package org.palladiosimulator.simexp.workflow.launcher;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.palladiosimulator.envdyn.api.entity.bn.SamplerLogger;
import org.palladiosimulator.envdyn.api.entity.bn.SamplerLoggerDispatcher;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;

public class SamplerLoggerFile implements SamplerLogger {
    private static final Logger LOGGER = Logger.getLogger(SamplerLoggerDispatcher.class);

    private final Writer writer;

    private int counter = 0;

    public SamplerLoggerFile(String simulationID) throws IOException {
        IPath workspaceBasePath = ResourcesPlugin.getWorkspace()
            .getRoot()
            .getLocation();
        Path outputBasePath = Paths.get(workspaceBasePath.toString());
        Path logFolder = outputBasePath.resolve("log");
        Path logFile = logFolder.resolve(simulationID + ".samples");
        this.writer = Files.newBufferedWriter(logFile);
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public synchronized void onSample(GroundRandomVariable variable, Object value) {
        try {
            writer.write(String.format("% 8d sample of %-50s: %s\n", ++counter, variable.getEntityName(), value));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
