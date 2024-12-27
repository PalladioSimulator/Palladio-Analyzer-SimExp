package org.palladiosimulator.simexp.dsl.ea.optimizer.research;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ParameterOptimizationVisualizer {

    private static String filePath = "C:\\Eigene_Dateien\\puffer\\print.txt";

    public static void exportToJSON(ConcurrentLinkedDeque<ParameterConfig> data) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting()
            .create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }

}
