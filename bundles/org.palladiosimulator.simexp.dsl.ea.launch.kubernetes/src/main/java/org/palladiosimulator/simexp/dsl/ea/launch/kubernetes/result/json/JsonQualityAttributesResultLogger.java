package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result.json;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result.csv.CsvResultLogger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result.json.TaskResult.OptimizableParam;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IResultHandler;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonQualityAttributesResultLogger implements IResultHandler {
    private static final Logger LOGGER = Logger.getLogger(CsvResultLogger.class);

    private final Path qaPath;
    private final Gson gson;

    public JsonQualityAttributesResultLogger(Path qaPath) {
        this.qaPath = qaPath;
        this.gson = new GsonBuilder() //
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    }

    @Override
    public synchronized void process(List<OptimizableValue<?>> optimizableValues, JobResult result) {
        Path taskPath = buildTaskPath(result);
        try (Writer writer = Files.newBufferedWriter(taskPath)) {
            List<OptimizableParam<?>> optimizablePairs = buildOptimizablePairs(optimizableValues);
            TaskResult taskResult = new TaskResult(optimizablePairs, result);
            gson.toJson(taskResult, writer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private List<OptimizableParam<?>> buildOptimizablePairs(List<OptimizableValue<?>> optimizableValues) {
        List<OptimizableParam<?>> optimizablePairs = new ArrayList<>();
        for (OptimizableValue<?> optimizable : optimizableValues) {
            String name = optimizable.getOptimizable()
                .getName();
            OptimizableParam<?> param = new OptimizableParam<>(name, optimizable.getValue());
            optimizablePairs.add(param);
        }
        return optimizablePairs;
    }

    private Path buildTaskPath(JobResult result) {
        String taskName = result.id.replaceAll("\\s+", "_");
        String fileName = String.format("Result_%s.json", taskName);
        Path taskPath = qaPath.resolve(fileName);
        return taskPath;
    }

    @Override
    public void dispose() {
    }
}
