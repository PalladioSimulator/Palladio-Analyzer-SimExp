package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.ITaskManager;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask.WorkspaceEntry;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;

public class EAFitnessEvaluator implements IEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(EAFitnessEvaluator.class);

    private final ITaskManager taskManager;
    private final Channel channel;
    private final String outQueueName;
    private final List<Path> projectPaths;
    private final ClassLoader classloader;

    private int count = 0;

    public EAFitnessEvaluator(ITaskManager taskManager, Channel channel, String outQueueName, List<Path> projectPaths,
            ClassLoader classloader) {
        this.taskManager = taskManager;
        this.channel = channel;
        this.outQueueName = outQueueName;
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
            LOGGER.info(String.format("evaluate: %s", optimizableValueToString.asString(optimizableValues)));
            JobTask task = createTask(optimizableValues);
            Gson logGson = new GsonBuilder().serializeNulls()
                .setPrettyPrinting()
                .create();
            String logMessage = logGson.toJson(task);
            LOGGER.debug(String.format("created task %s: '%s'", task.id, logMessage));

            SettableFutureTask<Optional<Double>> future = new SettableFutureTask<>(() -> {
            }, Optional.empty());
            sendTask(task);
            taskManager.newTask(task.id, future);
            return future;
        } finally {
            Thread.currentThread()
                .setContextClassLoader(oldContextClassLoader);
        }
    }

    private JobTask createTask(List<OptimizableValue<?>> optimizableValues) throws IOException {
        WorkspaceEntry fileEntry = createFile(
                Paths.get("/mnt/develop/zd745/palladio/tmp/kubernetes/kubernetes_client/log4j2.xml"));

        JobTask task = new JobTask();
        task.id = getTaskId();
        task.workspacePath = "/workspace";
        task.workspaceArgument = "-w";
        Integer duration = 10;
        task.command = String.format("/app/sim_test.sh -d %d", duration);
        for (Path projectFolder : projectPaths) {
            WorkspaceEntry projectArchive = createProjectArchive(projectFolder);
            task.workspaceEntries.add(projectArchive);
        }
        task.workspaceEntries.add(fileEntry);
        task.launcherName = "deltaiot";
        return task;
    }

    private synchronized String getTaskId() {
        return String.format("Task %d", count++);
    }

    private void sendTask(JobTask task) throws IOException {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .create();

        String message = gson.toJson(task);
        channel.basicPublish("", outQueueName, null, message.getBytes(StandardCharsets.UTF_8));
        LOGGER.info(String.format("Sent task: %s", task.id));
    }

    private WorkspaceEntry createProjectArchive(Path projectFolder) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tar = new TarArchiveOutputStream(new BZip2CompressorOutputStream(bos))) {
            PathMatcher pathMatcher = projectFolder.getFileSystem()
                .getPathMatcher("glob:**/target");
            addFileToTarGz(tar, projectFolder, Paths.get(""), pathMatcher);
        }
        byte[] projectArchive = bos.toByteArray();
        return new WorkspaceEntry(WorkspaceEntry.Kind.ARCHIVE, projectFolder.getFileName()
            .toString(), "bz2", projectArchive);
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

    private void addFileToTarGz(TarArchiveOutputStream tar, Path path, Path base, PathMatcher ignoreMatcher)
            throws IOException {
        Path entryName = base.resolve(path.getFileName());
        ArchiveEntry tarEntry = tar.createArchiveEntry(path, entryName.getFileName()
            .toString());
        tar.putArchiveEntry(tarEntry);
        if (Files.isRegularFile(path)) {
            try (InputStream is = Files.newInputStream(path)) {
                IOUtils.copy(is, tar);
                tar.closeArchiveEntry();
            }
        } else {
            tar.closeArchiveEntry();
            File[] children = path.toFile()
                .listFiles();
            if (children != null) {
                for (File child : children) {
                    Path childPath = child.toPath();
                    if (!ignoreMatcher.matches(childPath)) {
                        addFileToTarGz(tar, childPath.toAbsolutePath(), entryName, ignoreMatcher);
                    }
                }
            }
        }
    }

}
