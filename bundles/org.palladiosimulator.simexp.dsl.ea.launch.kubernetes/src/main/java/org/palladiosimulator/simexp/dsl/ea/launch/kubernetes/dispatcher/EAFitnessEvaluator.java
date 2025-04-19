package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.CachingWorkspaceEntryFactory;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.ITaskManager;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IWorkspaceEntryFactory;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask.WorkspaceEntry;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.WorkspaceEntryFactory;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EAFitnessEvaluator implements IEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(EAFitnessEvaluator.class);

    private final ITaskManager taskManager;
    private final ITaskSender taskSender;
    private final String launcherName;
    private final List<Path> projectPaths;
    private final ClassLoader classloader;
    private final IWorkspaceEntryFactory workspaceEntryFactory;

    private int count = 0;

    public EAFitnessEvaluator(ITaskManager taskManager, ITaskSender taskSender, String launcherName,
            List<Path> projectPaths, ClassLoader classloader) {
        this.taskManager = taskManager;
        this.taskSender = taskSender;
        this.launcherName = launcherName;
        this.projectPaths = projectPaths;
        this.classloader = classloader;
        this.workspaceEntryFactory = new CachingWorkspaceEntryFactory(new WorkspaceEntryFactory());
    }

    @Override
    public Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) throws IOException {
        ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread()
            .setContextClassLoader(classloader);
        try {
            OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
            JobTask task = createTask(optimizableValues);
            Gson logGson = new GsonBuilder().serializeNulls()
                .setPrettyPrinting()
                .create();
            String logMessage = logGson.toJson(task);
            LOGGER.debug(String.format("created task %s: '%s'", task.id, logMessage));

            SettableFutureTask<Optional<Double>> future = new SettableFutureTask<>(() -> {
            }, Optional.empty());
            taskSender.sendTask(task, optimizableValueToString.asString(optimizableValues));
            taskManager.newTask(task.id, future, optimizableValues);
            return future;
        } finally {
            Thread.currentThread()
                .setContextClassLoader(oldContextClassLoader);
        }
    }

    private JobTask createTask(List<OptimizableValue<?>> optimizableValues) throws IOException {
        JobTask task = new JobTask();
        task.id = getTaskId();
        task.workspacePath = "/workspace";
        /*
         * task.workspaceArgument = "-w"; Integer duration = 10; task.command =
         * String.format("/app/sim_test.sh -d %d", duration);
         */

        task.workspaceArgument = "-data";
        List<String> arguments = new ArrayList<>();
        arguments.add("-resultFile");
        arguments.add("result.json");
        arguments.add("-optimizablesFile");
        arguments.add("optimizableValues.json");
        arguments.add("-launchConfig");
        arguments.add(launcherName);
        for (Path projectFolder : projectPaths) {
            arguments.add("-projectName");
            String projectName = projectFolder.getFileName()
                .toString();
            arguments.add(projectName);
        }
        task.command = String.format("/simexp/simexp_console %s", StringUtils.join(arguments, " "));
        // task.command = "/usr/bin/sleep 300";

        WorkspaceEntry optimizableFileEntry = workspaceEntryFactory.createOptimizableFile(optimizableValues);
        task.workspaceEntries.add(optimizableFileEntry);
        for (Path projectFolder : projectPaths) {
            WorkspaceEntry projectArchive = workspaceEntryFactory.createProjectArchive(projectFolder);
            task.workspaceEntries.add(projectArchive);
        }
        return task;
    }

    private synchronized String getTaskId() {
        return String.format("Task %d", ++count);
    }
}
