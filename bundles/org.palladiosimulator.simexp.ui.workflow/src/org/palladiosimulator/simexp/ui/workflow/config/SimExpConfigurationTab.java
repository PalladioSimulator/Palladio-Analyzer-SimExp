package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulationKind;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpConfigurationTab extends AbstractLaunchConfigurationTab {
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    public static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private Text textSimulationID;
    private Text textNumberOfRuns;
    private Text textNumerOfSimulationsPerRun;

    private Map<SimulationEngine, Button> simulationEngineMap;
    private Map<SimulationEngine, Composite> engineDetailsMap;
    private Map<SimulationKind, Button> simulationKindMap;
    private Composite simulationDetails;

    private Text textMonitorRepository;
    private List monitors;
    private Text textFailureScenarioModel;

    private Text textModuleFiles;
    private Text textPropertyFiles;

    private Composite container;
    private ModifyListener modifyListener;

    @Override
    public void createControl(Composite parent) {
        modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };
        container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout());

        final Label simulationIDLabel = new Label(container, SWT.NONE);
        simulationIDLabel.setText("Simulation-ID:");

        textSimulationID = new Text(container, SWT.BORDER);
        textSimulationID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textSimulationID.addModifyListener(modifyListener);

        final Label numberOfRunsLabel = new Label(container, SWT.NONE);
        numberOfRunsLabel.setText("Number of runs:");

        textNumberOfRuns = new Text(container, SWT.BORDER);
        textNumberOfRuns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textNumberOfRuns.addModifyListener(modifyListener);

        final Label numberOfSimulationsPerRunLabel = new Label(container, SWT.NONE);
        numberOfSimulationsPerRunLabel.setText("Number of simulations per run:");

        textNumerOfSimulationsPerRun = new Text(container, SWT.BORDER);
        textNumerOfSimulationsPerRun.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textNumerOfSimulationsPerRun.addModifyListener(modifyListener);

        Composite simulationParent = new Composite(container, SWT.BORDER);
        simulationParent.setBackground(new Color(128, 0, 128));
        simulationParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simulationParent.setLayout(new GridLayout(2, false));

        Group simulationEngineGroup = new Group(simulationParent, SWT.NONE);
        simulationEngineGroup.setText(SimulationConstants.SIMULATION_ENGINE);
        simulationEngineGroup.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        simulationEngineGroup.setLayout(new RowLayout(SWT.VERTICAL));
        simulationEngineMap = new HashMap<>();
        engineDetailsMap = new HashMap<>();
        simulationDetails = new Composite(simulationParent, SWT.NONE);
        for (SimulationEngine engine : SimulationEngine.values()) {
            Button button = new Button(simulationEngineGroup, SWT.RADIO);
            button.setText(engine.getName());
            button.setData(engine);
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Button selectedButton = (Button) e.widget;
                    SimulationEngine selectedEngine = (SimulationEngine) selectedButton.getData();
                    for (Map.Entry<SimulationEngine, Composite> entry : engineDetailsMap.entrySet()) {
                        Composite detailsComposite = entry.getValue();
                        GridData layoutData = (GridData) detailsComposite.getLayoutData();
                        if (selectedEngine == entry.getKey()) {
                            layoutData.exclude = false;
                            detailsComposite.setVisible(true);
                        } else {
                            layoutData.exclude = true;
                            detailsComposite.setVisible(false);
                        }
                    }
                    simulationDetails.layout();
                }
            });

            simulationEngineMap.put(engine, button);
        }

        simulationDetails.setBackground(new Color(128, 0, 0));
        simulationDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simulationDetails.setLayout(new GridLayout());

        Composite pcmDetails = createEngineDetailsComposite(simulationDetails, SimulationEngine.PCM);
        createPcmTab(pcmDetails);
        pcmDetails.setBackground(new Color(0, 128, 128));
        engineDetailsMap.put(SimulationEngine.PCM, pcmDetails);
        Composite prismDetails = createEngineDetailsComposite(simulationDetails, SimulationEngine.PRISM);
        createPrismTab(prismDetails);
        prismDetails.setBackground(new Color(0, 128, 0));
        engineDetailsMap.put(SimulationEngine.PRISM, prismDetails);
    }

    private Composite createEngineDetailsComposite(Composite parent, SimulationEngine engine) {
        Group content = new Group(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.exclude = true;
        content.setVisible(false);
        content.setLayoutData(layoutData);
        content.setLayout(new GridLayout());
        content.setText(engine.getName());
        return content;
    }

    private void createPrismTab(Composite parent) {
        textModuleFiles = new Text(parent, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(parent, modifyListener, "Module Files",
                ModelFileTypeConstants.PRISM_MODULE_FILE_EXTENSION, textModuleFiles, "Select Module Files", getShell(),
                true, true, ModelFileTypeConstants.EMPTY_STRING, true);

        textPropertyFiles = new Text(parent, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(parent, modifyListener, "Property Files",
                ModelFileTypeConstants.PRISM_PROPERTY_FILE_EXTENSION, textPropertyFiles, "Select Property Files",
                getShell(), true, true, ModelFileTypeConstants.EMPTY_STRING, true);
    }

    private void createPcmTab(Composite content) {
        final Group qualityObjectivesGroup = new Group(content, SWT.NONE);
        qualityObjectivesGroup.setText(SimulationConstants.QUALITY_OBJECTIVE);
        qualityObjectivesGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

        Map<SimulationKind, java.util.List<String>> simulationKindMonitorItems = new HashMap<>();
        simulationKindMonitorItems.put(SimulationKind.PERFORMANCE, Arrays.asList("System Response Time"));
        simulationKindMonitorItems.put(SimulationKind.RELIABILITY, Arrays.asList("System Response Time"));
        simulationKindMonitorItems.put(SimulationKind.PERFORMABILITY,
                Arrays.asList("System Response Time", "System ExecutionResultType"));
        simulationKindMonitorItems.put(SimulationKind.MODELLED, Collections.emptyList());

        simulationKindMap = new HashMap<>();
        for (SimulationKind kind : SimulationKind.values()) {
            Button button = new Button(qualityObjectivesGroup, SWT.RADIO);
            button.setText(kind.getName());
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (button.getSelection()) {
                        java.util.List<String> items = simulationKindMonitorItems.get(kind);
                        monitors.setItems(items.toArray(new String[0]));
                    }
                    setDirty(true);
                    updateLaunchConfigurationDialog();
                }
            });
            simulationKindMap.put(kind, button);
        }

        textMonitorRepository = new Text(content, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(content, modifyListener, "Monitor Repository File",
                ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION, textMonitorRepository,
                "Select Monitor Repository File", getShell(), ModelFileTypeConstants.EMPTY_STRING);

        final Group monitorsGroup = new Group(content, SWT.NONE);
        monitorsGroup.setText("Monitors");
        monitorsGroup.setLayout(new GridLayout(1, false));
        monitorsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        monitors = new List(monitorsGroup, SWT.MULTI | SWT.BORDER);
        monitors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite failureComposite = new Composite(content, SWT.NONE);
        failureComposite.setLayout(new GridLayout());
        failureComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));

        textFailureScenarioModel = new Text(failureComposite, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(failureComposite, modifyListener, "Failure Scenario File",
                ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE_EXTENSION, textFailureScenarioModel,
                "Select Failure Scenario File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            textSimulationID.setText(
                    configuration.getAttribute(SimulationConstants.SIMULATION_ID, SimulationConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Simulation ID", e.getMessage());
        }

        try {
            int numberOfRuns = configuration.getAttribute(SimulationConstants.NUMBER_OF_RUNS,
                    SimulationConstants.DEFAULT_NUMBER_OF_RUNS);
            textNumberOfRuns.setText(String.valueOf(numberOfRuns));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Number of runs", e.getMessage());
        }

        try {
            int numberOfSimulationsPerRun = configuration.getAttribute(
                    SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN,
                    SimulationConstants.DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN);
            textNumerOfSimulationsPerRun.setText(String.valueOf(numberOfSimulationsPerRun));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Number of simulations per run", e.getMessage());
        }

        try {
            String selectedEngineName = configuration.getAttribute(SimulationConstants.SIMULATION_ENGINE,
                    SimulationConstants.DEFAULT_SIMULATION_ENGINE.getName());
            /*
             * Arrays.stream(simulationEngineTabFolder.getItems()) .filter(item -> item.getText()
             * .equals(selectedEngine)) .findAny()
             * .ifPresent(simulationEngineTabFolder::setSelection);
             */
            SimulationEngine selectedEngine = SimulationEngine.fromName(selectedEngineName);
            Button engineButton = simulationEngineMap.get(selectedEngine);
            engineButton.setSelection(true);
            Composite detailsComposite = engineDetailsMap.get(selectedEngine);
            GridData layoutData = (GridData) detailsComposite.getLayoutData();
            layoutData.exclude = false;
            detailsComposite.setVisible(true);
            simulationDetails.layout();
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage());
        }

        try {
            String selectedQualityObjective = configuration.getAttribute(SimulationConstants.QUALITY_OBJECTIVE,
                    SimulationConstants.DEFAULT_QUALITY_OBJECTIVE.getName());
            SimulationKind configured = SimulationKind.fromName(selectedQualityObjective);
            Button button = simulationKindMap.get(configured);
            button.setSelection(true);
            button.notifyListeners(SWT.Selection, null);
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage());
        }

        try {
            textMonitorRepository.setText(configuration.getAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Monitor Repository File", e.getMessage());
        }

        try {
            textFailureScenarioModel.setText(configuration
                .getAttribute(ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Failure Scenario File", e.getMessage());
        }

        try {
            textModuleFiles.setText(configuration.getAttribute(ModelFileTypeConstants.PRISM_MODULE_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Prism Module File", e.getMessage());
        }

        try {
            textPropertyFiles.setText(configuration.getAttribute(ModelFileTypeConstants.PRISM_PROPERTY_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Prism Property FIle", e.getMessage());
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.SIMULATION_ID, textSimulationID.getText());

        int numberOfRuns = Integer.parseInt(textNumberOfRuns.getText());
        configuration.setAttribute(SimulationConstants.NUMBER_OF_RUNS, numberOfRuns);
        int numberOfSimulationsPerRun = Integer.parseInt(textNumerOfSimulationsPerRun.getText());
        configuration.setAttribute(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN, numberOfSimulationsPerRun);

        SimulationEngine simulationEngine = getSelectedSimulationEngine();
        configuration.setAttribute(SimulationConstants.SIMULATION_ENGINE, simulationEngine.getName());

        for (SimulationKind kind : SimulationKind.values()) {
            Button button = simulationKindMap.get(kind);
            if (button.getSelection()) {
                configuration.setAttribute(SimulationConstants.QUALITY_OBJECTIVE, kind.getName());
                break;
            }
        }

        configuration.setAttribute(ModelFileTypeConstants.MONITORS, Arrays.asList(monitors.getItems()));
        configuration.setAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE, textMonitorRepository.getText());
        configuration.setAttribute(ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE,
                textFailureScenarioModel.getText());
        configuration.setAttribute(ModelFileTypeConstants.PRISM_PROPERTY_FILE, Arrays.asList(textPropertyFiles.getText()
            .split(";")));
        configuration.setAttribute(ModelFileTypeConstants.PRISM_MODULE_FILE, Arrays.asList(textModuleFiles.getText()
            .split(";")));
    }

    private SimulationEngine getSelectedSimulationEngine() {
        for (Map.Entry<SimulationEngine, Button> entry : simulationEngineMap.entrySet()) {
            Button button = entry.getValue();
            if (button.getSelection()) {
                SimulationEngine simulationEngine = (SimulationEngine) button.getData();
                return simulationEngine;
            }
        }
        throw new RuntimeException("no radio button selected");
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        setErrorMessage(null);

        if (textSimulationID.getText()
            .isBlank()) {
            setErrorMessage("Simulation ID is missing");
            return false;
        }

        if (textNumberOfRuns.getText()
            .isBlank()) {
            setErrorMessage("Number of runs is missing");
            return false;
        }

        try {
            int numberOfRuns = Integer.parseInt(textNumberOfRuns.getText());

            if (numberOfRuns < 0) {
                setErrorMessage("Number of runs must not be negative");
                return false;
            }
        } catch (Exception e) {
            setErrorMessage("Number of runs must be an integer");
            return false;
        }

        if (textNumerOfSimulationsPerRun.getText()
            .isBlank()) {
            setErrorMessage("Number of simulations per run is missing");
            return false;
        }

        try {
            int numberOfSimulationsPerRun = Integer.parseInt(textNumerOfSimulationsPerRun.getText());

            if (numberOfSimulationsPerRun < 0) {
                setErrorMessage("Number of simulations per run must not be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            setErrorMessage("Number of simulations per run must be an integer");
            return false;
        }

        SimulationEngine simulationEngine = getSelectedSimulationEngine();
        if (simulationEngine == SimulationEngine.PCM) {
            if (!TabHelper.validateFilenameExtension(textMonitorRepository.getText(),
                    ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION)) {
                setErrorMessage("Monitor Repository is missing.");
                return false;
            }

        } else if (simulationEngine == SimulationEngine.PRISM) {
            if (!textModuleFiles.getText()
                .isBlank()) {
                String[] moduleFiles = textModuleFiles.getText()
                    .split(";");
                for (String moduleFile : moduleFiles) {
                    if (!TabHelper.validateFilenameExtension(moduleFile,
                            ModelFileTypeConstants.PRISM_MODULE_FILE_EXTENSION)) {
                        setErrorMessage("Invalid prism module file");
                        return false;
                    }
                }
            }

            if (!textPropertyFiles.getText()
                .isBlank()) {
                String[] propertyFiles = textPropertyFiles.getText()
                    .split(";");
                for (String propertyFile : propertyFiles) {
                    if (!TabHelper.validateFilenameExtension(propertyFile,
                            ModelFileTypeConstants.PRISM_PROPERTY_FILE_EXTENSION)) {
                        setErrorMessage("Invalid prism property file");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public Image getImage() {
        return ImageRegistryHelper.getTabImage(PLUGIN_ID, CONFIGURATION_TAB_IMAGE_PATH);
    }

    @Override
    public String getName() {
        return "Simulation Configuration";
    }

    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.SimExpSimulationConfigurationTab";
    }
}