package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.console.api.OptimizableValues;
import org.palladiosimulator.simexp.console.api.OptimizableValues.BoolEntry;
import org.palladiosimulator.simexp.console.api.OptimizableValues.DoubleEntry;
import org.palladiosimulator.simexp.console.api.OptimizableValues.IntEntry;
import org.palladiosimulator.simexp.console.api.OptimizableValues.StringEntry;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask.WorkspaceEntry;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class WorkspaceEntryFactory implements IWorkspaceEntryFactory {
    private static final Logger LOGGER = Logger.getLogger(WorkspaceEntryFactory.class);

    @Override
    public WorkspaceEntry createOptimizableFile(List<OptimizableValue<?>> optimizableValues) throws IOException {
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

    @Override
    public WorkspaceEntry createFile(Path path) throws IOException {
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

    @Override
    public WorkspaceEntry createProjectArchive(Path projectFolder) throws IOException {
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
