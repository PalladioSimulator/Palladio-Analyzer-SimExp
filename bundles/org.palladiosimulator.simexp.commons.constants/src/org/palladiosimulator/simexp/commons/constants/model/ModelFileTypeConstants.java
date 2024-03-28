package org.palladiosimulator.simexp.commons.constants.model;

public class ModelFileTypeConstants {

    public static final String EMPTY_STRING = "";

    public static final String ALLOCATION_FILE = "allocationFile";
    public static final String USAGE_FILE = "usageFile";
    public static final String MONITOR_REPOSITORY_FILE = "monitorRepositoryFile";
    public static final String SMODEL_FILE = "smodelFile";
    public static final String EXPERIMENTS_FILE = "experimentsFile";
    public static final String STATIC_MODEL_FILE = "staticModelFile";
    public static final String DYNAMIC_MODEL_FILE = "dynamicModelFile";
    public static final String MONITORS = "monitors";
    public static final String FAILURE_SCENARIO_MODEL_FILE = "failureScenarioFile";
    public static final String PRISM_MODULE_FILE = "prismModuleFile";
    public static final String PRISM_PROPERTY_FILE = "prismPropertyFile";

    public static final String[] ALLOCATION_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.ALLOCATION_EXTENSION };
    public static final String[] USAGEMODEL_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.USAGEMODEL_EXTENSION };
    public static final String[] MONITOR_REPOSITORY_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.MONITOR_REPOSITORY_EXTENSION };
    public static final String[] SMODEL_FILE_EXTENSION = new String[] { "*." + ModelTypeConstants.SMODEL_EXTENSION };
    public static final String[] EXPERIMENTS_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.EXPERIMENTS_EXTENSION };
    public static final String[] STATIC_MODEL_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.STATIC_MODEL_EXTENSION };
    public static final String[] DYNAMIC_MODEL_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.DYNAMIC_MODEL_EXTENSION };
    public static final String[] FAILURE_SCENARIO_MODEL_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.FAILURE_SCENARIO_MODEL_EXTENSION };
    public static final String[] PRISM_MODULE_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.PRISM_MODULE_EXTENSION };
    public static final String[] PRISM_PROPERTY_FILE_EXTENSION = new String[] {
            "*." + ModelTypeConstants.PRISM_PROPERTY_EXTENSION };
}
