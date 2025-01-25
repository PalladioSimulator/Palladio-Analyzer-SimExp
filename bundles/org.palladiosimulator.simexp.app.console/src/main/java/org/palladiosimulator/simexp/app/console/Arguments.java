package org.palladiosimulator.simexp.app.console;

import java.nio.file.Path;

public class Arguments {
    private final Path resultFile;
    private final String projectName;
    private final String launchConfig;
    private final Path optimizablesFile;

    public Arguments(String projectName, String launchConfig, Path resultFile, Path optimizablesFile) {
        this.projectName = projectName;
        this.launchConfig = launchConfig;
        this.resultFile = resultFile;
        this.optimizablesFile = optimizablesFile;
    }

    public Path getResultFile() {
        return resultFile;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLaunchConfig() {
        return launchConfig;
    }

    public Path getOptimizables() {
        return optimizablesFile;
    }

}
