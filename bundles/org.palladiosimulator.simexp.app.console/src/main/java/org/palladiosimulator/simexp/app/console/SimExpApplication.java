package org.palladiosimulator.simexp.app.console;

import java.io.IOException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.palladiosimulator.simexp.app.console.simulation.OptimizableValues;
import org.palladiosimulator.simexp.app.console.simulation.SimulationExecutor;

import com.google.gson.Gson;

public class SimExpApplication implements IApplication {
    private Logger logger;

    @Override
    public Object start(IApplicationContext context) throws Exception {
        initLogging();
        logger = Logger.getLogger(SimExpApplication.class);

        logger.info("SimExp running...");
        init();
        if (!lockInstanceLocation()) {
            return IApplication.EXIT_OK;
        }

        IPath instanceLocation = Platform.getLocation();
        Path instancePath = Paths.get(instanceLocation.toOSString());
        logger.info(String.format("instance path: %s", instancePath));
        try {
            Arguments arguments = parseCommandLine();
            validateCommandLine(arguments);

            // TODO: remove
            writeOptimizeableValues(arguments.getOptimizables());

            // Also registers for open project events.
            ILaunchManager launchManager = DebugPlugin.getDefault()
                .getLaunchManager();

            SimulationExecutor simulationExecutor = new SimulationExecutor(launchManager);
            simulationExecutor.runSimulation(arguments, instancePath);
        } catch (Exception e) {
            logger.error(String.format("exception running: %s", getClass().getSimpleName()), e);
        } finally {
            logger.info("SimExp finished");
        }
        return IApplication.EXIT_OK;
    }

    private void writeOptimizeableValues(Path optimizablesPath) throws IOException {
        OptimizableValues values = new OptimizableValues();
        OptimizableValues.StringEntry se = new OptimizableValues.StringEntry();
        se.name = "str";
        se.value = "s";
        values.stringValues.add(se);
        OptimizableValues.IntEntry ie = new OptimizableValues.IntEntry();
        ie.name = "int";
        ie.value = 1;
        values.intValues.add(ie);

        try (Writer writer = Files.newBufferedWriter(optimizablesPath, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            gson.toJson(values, writer);
        }
    }

    private void validateCommandLine(Arguments arguments) {
        // TODO: validate command line

    }

    private Arguments parseCommandLine() {
        Path resultFile = null;
        String projectName = null;
        String launchConfig = null;
        Path optimizablesFile = null;

        String[] args = Platform.getCommandLineArgs();
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-resultFile")) {
                i++;
                resultFile = Paths.get(args[i]);
            }
            if (args[i].equals("-projectName")) {
                i++;
                projectName = args[i];
            }
            if (args[i].equals("-launchConfig")) {
                i++;
                launchConfig = args[i];
            }
            if (args[i].equals("-optimizablesFile")) {
                i++;
                optimizablesFile = Paths.get(args[i]);
            }
            i++;
        }
        return new Arguments(projectName, launchConfig, resultFile, optimizablesFile);
    }

    private void initLogging() {
        PatternLayout consoleLayout = new PatternLayout("%d %-5p [%-10t] [%F:%L]: %m%n");
        ConsoleAppender ca = new ConsoleAppender(consoleLayout);
        ca.setThreshold(Level.DEBUG);
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure(ca);
    }

    private void init() {
        registerTerminationHandlers();
    }

    private void registerTerminationHandlers() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread arg0, Throwable arg1) {
                logger.error("Uncaught exception in thread: " + arg0, arg1);
            }
        });
    }

    private boolean lockInstanceLocation() throws IOException {
        Location configLocation = Platform.getConfigurationLocation();
        String instancePath = configLocation.getURL()
            .getPath();
        logger.debug(String.format("configuration path: %s", instancePath));
        if (!configLocation.lock()) {
            logger.error("application is already running");
            return false;
        }
        return true;
    }

    @Override
    public void stop() {
        logger.info("stopped");
    }
}
