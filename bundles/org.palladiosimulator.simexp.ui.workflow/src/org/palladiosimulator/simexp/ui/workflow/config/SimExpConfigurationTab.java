package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableList;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulationKind;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationObservableEnumValue;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationObservableIntegerValue;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationObservableListValue;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationObservableValue;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.FileURIValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.MinIntegerValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.NotEmptyValidator;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpConfigurationTab extends AbstractLaunchConfigurationTab {
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    public static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private final DataBindingContext ctx;

    private Text textSimulationID;
    private Text textNumberOfRuns;
    private Text textNumerOfSimulationsPerRun;

    // private Map<SimulationEngine, Button> simulationEngineMap;
    private SelectObservableValue<SimulationEngine> simulationEngineTarget;
    // private Map<SimulationEngine, Composite> engineDetailsMap;
    // private Map<SimulationKind, Button> simulationKindMap;
    private SelectObservableValue<SimulationKind> simulationKindTarget;

    private Text textMonitorRepository;
    private ListViewer monitors;
    private Text textFailureScenarioModel;

    private Text textModuleFiles;
    private Text textPropertyFiles;

    public SimExpConfigurationTab() {
        this.ctx = new DataBindingContext();
    }

    @Override
    public void createControl(Composite parent) {
        ModifyListener modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                for (Binding binding : ctx.getBindings()) {
                    binding.validateTargetToModel();
                }
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };
        Composite container = new Composite(parent, SWT.NONE);
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
        simulationParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simulationParent.setLayout(new GridLayout(2, false));

        Composite simulationEngineContainer = new Composite(simulationParent, SWT.NONE);
        simulationEngineContainer.setLayoutData(new GridData(SWT.None, SWT.TOP, false, true));
        simulationEngineContainer.setLayout(new GridLayout());

        Group simulationEngineGroup = new Group(simulationEngineContainer, SWT.NONE);
        simulationEngineGroup.setText(SimulationConstants.SIMULATION_ENGINE);
        simulationEngineGroup.setLayout(new RowLayout(SWT.VERTICAL));
        // simulationEngineMap = new HashMap<>();
        simulationEngineTarget = new SelectObservableValue<>();
        Map<SimulationEngine, Composite> engineDetailsMap = new HashMap<>();
        Composite simulationDetails = new Composite(simulationParent, SWT.NONE);
        for (SimulationEngine engine : SimulationEngine.values()) {
            Button button = new Button(simulationEngineGroup, SWT.RADIO);
            button.setText(engine.getName());
            // button.setData(engine);
            // simulationEngineMap.put(engine, button);
            ISWTObservableValue<Boolean> observeable = WidgetProperties.buttonSelection()
                .observe(button);
            simulationEngineTarget.addOption(engine, observeable);
        }

        ISideEffect.create(() -> {
            return simulationEngineTarget.getValue();
        }, new Consumer<SimulationEngine>() {

            @Override
            public void accept(SimulationEngine selectedEngine) {
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
                modifyListener.modifyText(null);
            }
        });

        simulationDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simulationDetails.setLayout(new GridLayout());

        Composite pcmDetails = createEngineDetailsComposite(simulationDetails, SimulationEngine.PCM);
        createPcmTab(pcmDetails, modifyListener);
        engineDetailsMap.put(SimulationEngine.PCM, pcmDetails);
        Composite prismDetails = createEngineDetailsComposite(simulationDetails, SimulationEngine.PRISM);
        // createPrismTab(prismDetails, modifyListener);
        engineDetailsMap.put(SimulationEngine.PRISM, prismDetails);
    }

    private Composite createEngineDetailsComposite(Composite parent, SimulationEngine engine) {
        Group content = new Group(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.exclude = true;
        content.setLayoutData(layoutData);
        content.setVisible(false);
        content.setLayout(new GridLayout());
        content.setText(engine.getName());
        return content;
    }

    private void createPrismTab(Composite parent, ModifyListener modifyListener) {
        textModuleFiles = new Text(parent, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(parent, modifyListener, "Module Files",
                ModelFileTypeConstants.PRISM_MODULE_FILE_EXTENSION, textModuleFiles, "Select Module Files", getShell(),
                true, true, ModelFileTypeConstants.EMPTY_STRING, true);

        textPropertyFiles = new Text(parent, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(parent, modifyListener, "Property Files",
                ModelFileTypeConstants.PRISM_PROPERTY_FILE_EXTENSION, textPropertyFiles, "Select Property Files",
                getShell(), true, true, ModelFileTypeConstants.EMPTY_STRING, true);
    }

    private void createPcmTab(Composite content, ModifyListener modifyListener) {
        final Group qualityObjectivesGroup = new Group(content, SWT.NONE);
        qualityObjectivesGroup.setText(SimulationConstants.QUALITY_OBJECTIVE);
        qualityObjectivesGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

        Map<SimulationKind, List<String>> simulationKindMonitorItems = new HashMap<>();
        simulationKindMonitorItems.put(SimulationKind.PERFORMANCE, Arrays.asList("System Response Time"));
        simulationKindMonitorItems.put(SimulationKind.RELIABILITY, Arrays.asList("System Response Time"));
        simulationKindMonitorItems.put(SimulationKind.PERFORMABILITY,
                Arrays.asList("System Response Time", "System ExecutionResultType"));
        simulationKindMonitorItems.put(SimulationKind.MODELLED, Collections.emptyList());

        // simulationKindMap = new HashMap<>();
        simulationKindTarget = new SelectObservableValue<>();
        for (SimulationKind kind : SimulationKind.values()) {
            Button button = new Button(qualityObjectivesGroup, SWT.RADIO);
            button.setText(kind.getName());
            // simulationKindMap.put(kind, button);

            ISWTObservableValue<Boolean> observeable = WidgetProperties.buttonSelection()
                .observe(button);
            simulationKindTarget.addOption(kind, observeable);
        }

        ISideEffect.create(() -> {
            return simulationKindTarget.getValue();
        }, new Consumer<SimulationKind>() {

            @Override
            public void accept(SimulationKind selectedKind) {
                if (selectedKind == null) {
                    return;
                }
                modifyListener.modifyText(null);
                /*
                 * List<String> items = simulationKindMonitorItems.get(selectedKind);
                 * monitors.setInput(items);
                 */
                /*
                 * IObservableList<String> listModel = Properties.<String> selfList(String.class)
                 * .observe(items); monitors.setInput(listModel);
                 */
            }
        });

        textMonitorRepository = new Text(content, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(content, modifyListener, "Monitor Repository File",
                ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION, textMonitorRepository,
                "Select Monitor Repository File",

                getShell(), ModelFileTypeConstants.EMPTY_STRING);

        final Group monitorsGroup = new Group(content, SWT.NONE);
        monitorsGroup.setText("Monitors");
        monitorsGroup.setLayout(new GridLayout(1, false));
        monitorsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        monitors = new ListViewer(monitorsGroup, SWT.MULTI | SWT.BORDER);
        monitors.getControl()
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ObservableListContentProvider<String> observableInput = new ObservableListContentProvider<>();
        monitors.setContentProvider(observableInput);

        ISWTObservableList<String> monitorsTarget = WidgetProperties.items()
            .observe(monitors.getList());
        IObservableFactory<SimulationKind, IObservableList<String>> detailFactory = new IObservableFactory<>() {

            @Override
            public IObservableList<String> createObservable(SimulationKind master) {
                IObservableList<String> listModel = new WritableList<>(simulationKindMonitorItems.get(master),
                        String.class);
                return listModel;
            }
        };
        IObservableList<String> monitorsModel = MasterDetailObservables.detailList(simulationKindTarget, detailFactory,
                String.class);
        ctx.bindList(monitorsTarget, monitorsModel, new UpdateListStrategy<>(UpdateValueStrategy.POLICY_NEVER), null);

        // TODO: remove
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
        /*
         * try { textSimulationID.setText(
         * configuration.getAttribute(SimulationConstants.SIMULATION_ID,
         * SimulationConstants.EMPTY_STRING)); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Simulation ID", e.getMessage()); }
         */
        IObservableValue<String> simulationIdTarget = WidgetProperties.text(SWT.Modify)
            .observe(textSimulationID);
        IObservableValue<String> simulationIdModel = new ConfigurationObservableValue(configuration,
                SimulationConstants.SIMULATION_ID);
        UpdateValueStrategy<String, String> simulationIdUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        simulationIdUpdateStrategy.setBeforeSetValidator(new NotEmptyValidator("Simulation ID"));
        Binding simulationIdBindValue = ctx.bindValue(simulationIdTarget, simulationIdModel, simulationIdUpdateStrategy,
                null);
        ControlDecorationSupport.create(simulationIdBindValue, SWT.TOP | SWT.RIGHT);

        /*
         * try { int numberOfRuns = configuration.getAttribute(SimulationConstants.NUMBER_OF_RUNS,
         * SimulationConstants.DEFAULT_NUMBER_OF_RUNS);
         * textNumberOfRuns.setText(String.valueOf(numberOfRuns)); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Number of runs", e.getMessage()); }
         */
        IObservableValue<String> numberOfRunsTarget = WidgetProperties.text(SWT.Modify)
            .observe(textNumberOfRuns);
        IObservableValue<Integer> numberOfRunsModel = new ConfigurationObservableIntegerValue(configuration,
                SimulationConstants.NUMBER_OF_RUNS);
        UpdateValueStrategy<String, Integer> numberOfRunsUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        numberOfRunsUpdateStrategy.setBeforeSetValidator(new MinIntegerValidator("Number of runs", 1));
        Binding numberOfRunsBindValue = ctx.bindValue(numberOfRunsTarget, numberOfRunsModel, numberOfRunsUpdateStrategy,
                null);
        ControlDecorationSupport.create(numberOfRunsBindValue, SWT.TOP | SWT.RIGHT);

        /*
         * try { int numberOfSimulationsPerRun = configuration.getAttribute(
         * SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN,
         * SimulationConstants.DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN);
         * textNumerOfSimulationsPerRun.setText(String.valueOf(numberOfSimulationsPerRun)); } catch
         * (CoreException e) { LaunchConfigPlugin.errorLogger(getName(),
         * "Number of simulations per run", e.getMessage()); }
         */
        IObservableValue<String> numberOfSimulationsPerRunTarget = WidgetProperties.text(SWT.Modify)
            .observe(textNumerOfSimulationsPerRun);
        IObservableValue<Integer> numberOfSimulationsPerRunModel = new ConfigurationObservableIntegerValue(
                configuration, SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN);
        UpdateValueStrategy<String, Integer> numberOfSimulationsPerRunUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        numberOfSimulationsPerRunUpdateStrategy
            .setBeforeSetValidator(new MinIntegerValidator("Number of simulations per run", 1));
        Binding numberOfSimulationsPerRunBindValue = ctx.bindValue(numberOfSimulationsPerRunTarget,
                numberOfSimulationsPerRunModel, numberOfSimulationsPerRunUpdateStrategy, null);
        ControlDecorationSupport.create(numberOfSimulationsPerRunBindValue, SWT.TOP | SWT.RIGHT);

        /*
         * try { String selectedEngineName =
         * configuration.getAttribute(SimulationConstants.SIMULATION_ENGINE,
         * SimulationConstants.DEFAULT_SIMULATION_ENGINE.getName()); SimulationEngine selectedEngine
         * = SimulationEngine.fromName(selectedEngineName); Button engineButton =
         * simulationEngineMap.get(selectedEngine); engineButton.setSelection(true); Composite
         * detailsComposite = engineDetailsMap.get(selectedEngine); GridData layoutData = (GridData)
         * detailsComposite.getLayoutData(); layoutData.exclude = false;
         * detailsComposite.setVisible(true); Composite detailsParent =
         * detailsComposite.getParent(); detailsParent.layout(); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage()); }
         */
        IObservableValue<SimulationEngine> simulationEngineModel = new ConfigurationObservableEnumValue<>(configuration,
                SimulationConstants.SIMULATION_ENGINE, SimulationEngine.class);
        UpdateValueStrategy<SimulationEngine, SimulationEngine> simulationEngineUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(simulationEngineTarget, simulationEngineModel, simulationEngineUpdateStrategy, null);

        /*
         * try { String selectedQualityObjective =
         * configuration.getAttribute(SimulationConstants.QUALITY_OBJECTIVE,
         * SimulationConstants.DEFAULT_QUALITY_OBJECTIVE.getName()); SimulationKind configured =
         * SimulationKind.fromName(selectedQualityObjective); Button button =
         * simulationKindMap.get(configured); button.setSelection(true);
         * button.notifyListeners(SWT.Selection, null); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage()); }
         */
        IObservableValue<SimulationKind> simulationKindModel = new ConfigurationObservableEnumValue<>(configuration,
                SimulationConstants.QUALITY_OBJECTIVE, SimulationKind.class);
        UpdateValueStrategy<SimulationKind, SimulationKind> simulationKindUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(simulationKindTarget, simulationKindModel, simulationKindUpdateStrategy, null);

        /*
         * try { textMonitorRepository.setText(configuration.getAttribute(ModelFileTypeConstants.
         * MONITOR_REPOSITORY_FILE, ModelFileTypeConstants.EMPTY_STRING)); } catch (CoreException e)
         * { LaunchConfigPlugin.errorLogger(getName(), "Monitor Repository File", e.getMessage()); }
         */
        IObservableValue<String> monitorRepositoryTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMonitorRepository);
        IObservableValue<String> monitorRepositoryModel = new ConfigurationObservableValue(configuration,
                ModelFileTypeConstants.MONITOR_REPOSITORY_FILE);
        UpdateValueStrategy<String, String> monitorRepositoryUpdateStrategy = createUpdateStrategy(
                "Monitor Repository File", ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION[0]);
        Binding monitorRepositoryBindValue = ctx.bindValue(monitorRepositoryTarget, monitorRepositoryModel,
                monitorRepositoryUpdateStrategy, null);
        ControlDecorationSupport.create(monitorRepositoryBindValue, SWT.TOP | SWT.RIGHT);

        ISWTObservableList<String> monitorTarget = WidgetProperties.items()
            .observe(monitors.getList());
        IObservableList<String> monitorModel = new ConfigurationObservableListValue(configuration,
                ModelFileTypeConstants.MONITORS);
        UpdateListStrategy<String, String> monitorsTargetToModel = new UpdateListStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        UpdateListStrategy<String, String> monitorsModelToTarget = new UpdateListStrategy<>(
                UpdateValueStrategy.POLICY_NEVER);
        ctx.bindList(monitorTarget, monitorModel, monitorsTargetToModel, monitorsModelToTarget);

        /*
         * try { textFailureScenarioModel.setText(configuration
         * .getAttribute(ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE,
         * ModelFileTypeConstants.EMPTY_STRING)); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Failure Scenario File", e.getMessage()); }
         * 
         * try { textModuleFiles.setText(configuration.getAttribute(ModelFileTypeConstants.
         * PRISM_MODULE_FILE, ModelFileTypeConstants.EMPTY_STRING)); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Prism Module File", e.getMessage()); }
         * 
         * try { textPropertyFiles.setText(configuration.getAttribute(ModelFileTypeConstants.
         * PRISM_PROPERTY_FILE, ModelFileTypeConstants.EMPTY_STRING)); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Prism Property FIle", e.getMessage()); }
         */

        ctx.updateTargets();
    }

    private UpdateValueStrategy<String, String> createUpdateStrategy(String field, String extension) {
        UpdateValueStrategy<String, String> updateValueStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        updateValueStrategy.setBeforeSetValidator(new CompoundStringValidator(
                Arrays.asList(new FileURIValidator(field), new ExtensionValidator(field, extension))));
        return updateValueStrategy;
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        /*
         * configuration.setAttribute(SimulationConstants.SIMULATION_ID,
         * textSimulationID.getText());
         * 
         * int numberOfRuns = Integer.parseInt(textNumberOfRuns.getText());
         * configuration.setAttribute(SimulationConstants.NUMBER_OF_RUNS, numberOfRuns); int
         * numberOfSimulationsPerRun = Integer.parseInt(textNumerOfSimulationsPerRun.getText());
         * configuration.setAttribute(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN,
         * numberOfSimulationsPerRun);
         * 
         * SimulationEngine simulationEngine = getSelectedSimulationEngine();
         * configuration.setAttribute(SimulationConstants.SIMULATION_ENGINE,
         * simulationEngine.getName());
         * 
         * for (SimulationKind kind : SimulationKind.values()) { Button button =
         * simulationKindMap.get(kind); if (button.getSelection()) {
         * configuration.setAttribute(SimulationConstants.QUALITY_OBJECTIVE, kind.getName()); break;
         * } }
         * 
         * configuration.setAttribute(ModelFileTypeConstants.MONITORS,
         * Arrays.asList(monitors.getItems()));
         * configuration.setAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE,
         * textMonitorRepository.getText());
         * configuration.setAttribute(ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE,
         * textFailureScenarioModel.getText());
         * configuration.setAttribute(ModelFileTypeConstants.PRISM_PROPERTY_FILE,
         * Arrays.asList(textPropertyFiles.getText() .split(";")));
         * configuration.setAttribute(ModelFileTypeConstants.PRISM_MODULE_FILE,
         * Arrays.asList(textModuleFiles.getText() .split(";")));
         */
        ctx.updateModels();
    }

    private SimulationEngine getSelectedSimulationEngine() {
        /*
         * for (Map.Entry<SimulationEngine, Button> entry : simulationEngineMap.entrySet()) { Button
         * button = entry.getValue(); if (button.getSelection()) { SimulationEngine simulationEngine
         * = (SimulationEngine) button.getData(); return simulationEngine; } }
         */
        throw new RuntimeException("no radio button selected");
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        for (ValidationStatusProvider statusProvider : ctx.getValidationStatusProviders()) {
            IStatus validationStatus = statusProvider.getValidationStatus()
                .getValue();
            if (!validationStatus.isOK()) {
                setErrorMessage(validationStatus.getMessage());
                return false;
            }
        }
        setErrorMessage(null);

        /*
         * setErrorMessage(null);
         * 
         * if (textSimulationID.getText() .isBlank()) { setErrorMessage("Simulation ID is missing");
         * return false; }
         * 
         * if (textNumberOfRuns.getText() .isBlank()) {
         * setErrorMessage("Number of runs is missing"); return false; }
         * 
         * try { int numberOfRuns = Integer.parseInt(textNumberOfRuns.getText());
         * 
         * if (numberOfRuns < 0) { setErrorMessage("Number of runs must not be negative"); return
         * false; } } catch (Exception e) { setErrorMessage("Number of runs must be an integer");
         * return false; }
         * 
         * if (textNumerOfSimulationsPerRun.getText() .isBlank()) {
         * setErrorMessage("Number of simulations per run is missing"); return false; }
         * 
         * try { int numberOfSimulationsPerRun =
         * Integer.parseInt(textNumerOfSimulationsPerRun.getText());
         * 
         * if (numberOfSimulationsPerRun < 0) {
         * setErrorMessage("Number of simulations per run must not be negative"); return false; } }
         * catch (NumberFormatException e) {
         * setErrorMessage("Number of simulations per run must be an integer"); return false; }
         * 
         * SimulationEngine simulationEngine = getSelectedSimulationEngine(); if (simulationEngine
         * == SimulationEngine.PCM) { if
         * (!TabHelper.validateFilenameExtension(textMonitorRepository.getText(),
         * ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION)) {
         * setErrorMessage("Monitor Repository is missing."); return false; }
         * 
         * } else if (simulationEngine == SimulationEngine.PRISM) { if (!textModuleFiles.getText()
         * .isBlank()) { String[] moduleFiles = textModuleFiles.getText() .split(";"); for (String
         * moduleFile : moduleFiles) { if (!TabHelper.validateFilenameExtension(moduleFile,
         * ModelFileTypeConstants.PRISM_MODULE_FILE_EXTENSION)) {
         * setErrorMessage("Invalid prism module file"); return false; } } }
         * 
         * if (!textPropertyFiles.getText() .isBlank()) { String[] propertyFiles =
         * textPropertyFiles.getText() .split(";"); for (String propertyFile : propertyFiles) { if
         * (!TabHelper.validateFilenameExtension(propertyFile,
         * ModelFileTypeConstants.PRISM_PROPERTY_FILE_EXTENSION)) {
         * setErrorMessage("Invalid prism property file"); return false; } } } }
         */

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