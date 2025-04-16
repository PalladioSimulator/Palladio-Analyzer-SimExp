package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.console.api.OptimizableValues;
import org.palladiosimulator.simexp.console.api.OptimizableValues.BoolEntry;
import org.palladiosimulator.simexp.console.api.OptimizableValues.DoubleEntry;
import org.palladiosimulator.simexp.console.api.OptimizableValues.IntEntry;
import org.palladiosimulator.simexp.console.api.OptimizableValues.StringEntry;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.ITaskManager;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask.WorkspaceEntry;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.rabbitmq.client.Channel;

public class EAFitnessEvaluator implements IEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(EAFitnessEvaluator.class);

    private final ITaskManager taskManager;
    private final Channel channel;
    private final String outQueueName;
    private final String launcherName;
    private final List<Path> projectPaths;
    private final ClassLoader classloader;

    private int count = 0;

    public EAFitnessEvaluator(ITaskManager taskManager, Channel channel, String outQueueName, String launcherName,
            List<Path> projectPaths, ClassLoader classloader) {
        this.taskManager = taskManager;
        this.channel = channel;
        this.outQueueName = outQueueName;
        this.launcherName = launcherName;
        this.projectPaths = projectPaths;
        this.classloader = classloader;
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
            sendTask(task, optimizableValueToString.asString(optimizableValues));
            taskManager.newTask(task.id, future);
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
        task.command = String.format("/simexp/simexp %s", StringUtils.join(arguments, " "));
        // task.command = "/usr/bin/sleep 300";

        WorkspaceEntry optimizableFileEntry = createOptimizableFile(optimizableValues);
        task.workspaceEntries.add(optimizableFileEntry);
        for (Path projectFolder : projectPaths) {
            WorkspaceEntry projectArchive = createProjectArchive(projectFolder);
            task.workspaceEntries.add(projectArchive);
        }
        return task;
    }

    private synchronized String getTaskId() {
        return String.format("Task %d", count++);
    }

    private void sendTask(JobTask task, String description) throws IOException {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .create();

        String message = gson.toJson(task);
        channel.basicPublish("", outQueueName, null, message.getBytes(StandardCharsets.UTF_8));
        LOGGER.info(String.format("Sent task: %s [%s]", task.id, description));
    }

    private WorkspaceEntry createOptimizableFile(List<OptimizableValue<?>> optimizableValues) throws IOException {
        Path optimizableFile = Paths.get("optimizableValues.json");
        OptimizableValues values = convertToJsonStructure(optimizableValues);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Gson gson = new Gson();
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8))) {
            gson.toJson(values, OptimizableValues.class, writer);
        }
        byte[] content = bos.toByteArray();
        return new WorkspaceEntry(WorkspaceEntry.Kind.FILE, optimizableFile.getFileName()
            .toString(), "", content);
    }

    private OptimizableValues convertToJsonStructure(List<OptimizableValue<?>> optimizableValues) {
        OptimizableValues values = new OptimizableValues();
        for (OptimizableValue<?> value : optimizableValues) {
            Optimizable optimizable = value.getOptimizable();
            DataType dataType = optimizable.getDataType();
            switch (dataType) {
            case BOOL:
                values.boolValues.add(new BoolEntry(optimizable.getName(), (Boolean) value.getValue()));
                break;
            case STRING:
                values.stringValues.add(new StringEntry(optimizable.getName(), (String) value.getValue()));
                break;
            case INT:
                values.intValues.add(new IntEntry(optimizable.getName(), (Integer) value.getValue()));
                break;
            case DOUBLE:
                values.doubleValues.add(new DoubleEntry(optimizable.getName(), (Double) value.getValue()));
                break;
            default:
                throw new RuntimeException("Unsupported type: " + dataType);
            }
        }
        return values;
    }

    private WorkspaceEntry createFile(Path path) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (OutputStream os = new BufferedOutputStream(bos)) {
            try (InputStream is = Files.newInputStream(path)) {
                IOUtils.copy(is, os);
            }
        }
        byte[] fileEntry = bos.toByteArray();
        return new WorkspaceEntry(WorkspaceEntry.Kind.FILE, path.getFileName()
            .toString(), "", fileEntry);
    }

    private WorkspaceEntry createProjectArchive(Path projectFolder) throws IOException {
        LOGGER.info(String.format("create tar archive for: %s", projectFolder));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileSystem fileSystem = projectFolder.getFileSystem();
        List<PathMatcher> pathMatchers = Arrays.asList(fileSystem.getPathMatcher("glob:**/target"),
                fileSystem.getPathMatcher("glob:**/bin"));
        try (TarArchiveOutputStream tar = new TarArchiveOutputStream(new BZip2CompressorOutputStream(bos))) {
            tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            File[] children = projectFolder.toFile()
                .listFiles();
            for (File child : children) {
                addFileToTarGz(tar, child.toPath(), Paths.get(""), pathMatchers);
            }
        }
        byte[] projectArchive = bos.toByteArray();
        return new WorkspaceEntry(WorkspaceEntry.Kind.ARCHIVE, projectFolder.getFileName()
            .toString(), "bz2", projectArchive);
    }

    private void addFileToTarGz(TarArchiveOutputStream tar, Path path, Path base, List<PathMatcher> ignoreMatchers)
            throws IOException {
        Path entryName = base.resolve(path.getFileName());
        ArchiveEntry tarEntry = tar.createArchiveEntry(path, entryName.toString());
        tar.putArchiveEntry(tarEntry);
        if (Files.isRegularFile(path)) {
            LOGGER.debug(String.format("add tar entry %s -> %s", entryName, path));
            try (InputStream is = Files.newInputStream(path)) {
                IOUtils.copy(is, tar);
                tar.closeArchiveEntry();
            }
        } else {
            tar.closeArchiveEntry();
            LOGGER.debug(String.format("add tar dir entry %s -> %s", entryName, path));
            File[] children = path.toFile()
                .listFiles();
            if (children != null) {
                for (File child : children) {
                    Path childPath = child.toPath();
                    if (!ignorePath(childPath, ignoreMatchers)) {
                        addFileToTarGz(tar, childPath.toAbsolutePath(), entryName, ignoreMatchers);
                    }
                }
            }
        }
    }

    private boolean ignorePath(Path path, List<PathMatcher> ignoreMatchers) {
        for (PathMatcher ignoreMatcher : ignoreMatchers) {
            if (ignoreMatcher.matches(path)) {
                return true;
            }
        }
        return false;
    }

}
