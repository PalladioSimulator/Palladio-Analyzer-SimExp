package org.palladiosimulator.simexp.app.console;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.palladiosimulator.simexp.app.console.launcher.ISimulationLaunch;
import org.palladiosimulator.simexp.core.simulation.ISimulationResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SimulationExecutor {
    private final static Logger LOGGER = Logger.getLogger(SimulationExecutor.class);

    private final ILaunchManager launchManager;
    private final Gson gson;

    public SimulationExecutor(ILaunchManager launchManager) {
        this.launchManager = launchManager;
        this.gson = new GsonBuilder().setPrettyPrinting()
            .create();
    }

    public void runSimulation(Arguments arguments, Path instancePath, Path resultFile) throws IOException {
        ConsoleSimulationResult result = doRunSimulation(arguments, instancePath);
        writeResult(result, resultFile);
    }

    private void writeResult(ConsoleSimulationResult result, Path resultFile) throws IOException {
        try (Writer writer = Files.newBufferedWriter(resultFile, StandardCharsets.UTF_8)) {
            gson.toJson(result, writer);
        }
    }

    private ConsoleSimulationResult doRunSimulation(Arguments arguments, Path instancePath) {
        // TODO: pass to launch delegate
        try {
            OptimizableValues optimizableValues = readOptimizeableValues(arguments.getOptimizables());

            IProject project = prepareSimulation(instancePath, arguments);
            ISimulationResult simulationResult = launchSimulation(launchManager, project, arguments);
            return new ConsoleSimulationResult(simulationResult.getTotalReward());
        } catch (Exception e) {
            LOGGER.error("simulation failed", e);
            return new ConsoleSimulationResult(e.getMessage());
        }
    }

    private OptimizableValues readOptimizeableValues(Path optimizablesPath) throws IOException {
        try (Reader reader = Files.newBufferedReader(optimizablesPath, StandardCharsets.UTF_8)) {
            OptimizableValues value = gson.fromJson(reader, OptimizableValues.class);
            return value;
        }
    }

    private IProject prepareSimulation(Path instancePath, Arguments arguments)
            throws InvocationTargetException, InterruptedException, CoreException {
        Path projectPath = instancePath.resolve(arguments.getProjectName());
        LOGGER.info(String.format("open: %s", projectPath));
        IProject project = openProject(projectPath);
        return project;
    }

    private IProject openProject(Path projectPath) throws CoreException {
        // it is acceptable to use the ResourcesPlugin class
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IProject project = workspace.getRoot()
            .getProject(projectPath.getFileName()
                .toString());

        if (project.exists()) {
            return project;
        }

        if (!project.isOpen()) {
            IProjectDescription desc = project.getWorkspace()
                .newProjectDescription(project.getName());
            project.create(desc, null);
            project.open(null);
        } else {
            project.refreshLocal(IResource.DEPTH_INFINITE, null);
        }

        return project;
    }

    private ISimulationResult launchSimulation(ILaunchManager launchManager, IProject project, Arguments arguments)
            throws CoreException, InterruptedException {
        String launchConfigName = arguments.getLaunchConfig();
        ILaunchConfiguration launchConfiguration = findLaunchConfiguration(launchManager, launchConfigName);
        if (launchConfiguration == null) {
            throw new RuntimeException(
                    String.format("launch config %s not found in: %s", launchConfigName, arguments.getProjectName()));
        }

        String launchMode = ILaunchManager.RUN_MODE;
        LOGGER.info(String.format("experiment start: %s", launchConfiguration.getName()));
        ILaunch launch = launchConfiguration.launch(launchMode, new NullProgressMonitor(), false, false);
        LOGGER.info(String.format("experiment finished: %s", launchConfiguration.getName()));
        ISimulationLaunch simulationLaunch = (ISimulationLaunch) launch;
        ISimulationResult simulationResult = simulationLaunch.getSimulationResult();
        return simulationResult;
    }

    private ILaunchConfiguration findLaunchConfiguration(ILaunchManager launchManager, String launchConfigName)
            throws CoreException {
        for (ILaunchConfiguration lc : launchManager.getLaunchConfigurations()) {
            if (Objects.equals(launchConfigName, lc.getName())) {
                return lc;
            }
        }
        return null;
    }
}
