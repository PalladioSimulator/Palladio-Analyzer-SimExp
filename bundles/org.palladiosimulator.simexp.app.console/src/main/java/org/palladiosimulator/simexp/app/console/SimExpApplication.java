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
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;

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

            prepareSimulation(instancePath, arguments);

            // processing ....
        } catch (Exception e) {
            logger.error(String.format("exception running: %s", getClass().getSimpleName()), e);
        } finally {
            logger.info("SimExp finished");
        }
        return IApplication.EXIT_OK;
    }

    private void prepareSimulation(Path instancePath, Arguments arguments) {
        Path projectPath = instancePath.resolve(arguments.getProjectName());
        logger.info(String.format("importing: %s", projectPath));
        // TODO:
    }

    private void validateCommandLine(Arguments arguments) {
        // TODO: validate command line

    }

    private Arguments parseCommandLine() {
        Path resultFile = null;
        String projectName = null;
        String launchConfig = null;

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
            i++;
        }
        return new Arguments(projectName, launchConfig, resultFile);
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
        // TODO Auto-generated method stub

    }
}
