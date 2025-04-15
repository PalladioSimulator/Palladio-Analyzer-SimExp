package org.palladiosimulator.simexp.app.console;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
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
import org.palladiosimulator.simexp.app.console.simulation.SimulationExecutor;

public class SimExpApplication implements IApplication {
    private static final Integer EXIT_FAILURE_INIT = Integer.valueOf(1);
    public static final Integer EXIT_FAILURE = Integer.valueOf(2);

    private Logger logger;

    @Override
    public Object start(IApplicationContext context) throws Exception {
        initLogging();
        logger = Logger.getLogger(SimExpApplication.class);

        logger.info("SimExp running...");
        try {
            if (!init()) {
                return EXIT_FAILURE_INIT;
            }

            IPath instanceLocation = Platform.getLocation();
            Path instancePath = Paths.get(instanceLocation.toOSString());
            logger.info(String.format("instance path: %s", instancePath));
            Arguments arguments = parseCommandLine();
            validateCommandLine(arguments);

            // Also registers for open project events.
            ILaunchManager launchManager = DebugPlugin.getDefault()
                .getLaunchManager();

            SimulationExecutor simulationExecutor = new SimulationExecutor(launchManager);
            simulationExecutor.runSimulation(arguments, instancePath);
            return IApplication.EXIT_OK;
        } catch (Exception e) {
            logger.error(String.format("exception running: %s", getClass().getSimpleName()), e);
        } finally {
            logger.info("SimExp finished");
        }
        return EXIT_FAILURE;
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
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
    }

    private boolean init() throws IOException {
        registerTerminationHandlers();
        return lockInstanceLocation();
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
