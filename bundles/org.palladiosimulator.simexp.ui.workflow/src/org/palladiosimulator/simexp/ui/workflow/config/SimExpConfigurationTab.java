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
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConditionalUpdateListStrategy;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConditionalUpdateValueStrategy;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationObservableArrayValue;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.UpdateStrategyController;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.conversion.ArrayToStringConverter;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.conversion.StringToArrayConverter;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.FileURIValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.MinIntegerValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.NotEmptyValidator;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpConfigurationTab extends AbstractLaunchConfigurationTab {
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    public static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private final DataBindingContext ctx;

    private Text textSimulationID;
    private Text textNumberOfRuns;
    private Text textNumerOfSimulationsPerRun;

    private SelectObservableValue<SimulationEngine> simulationEngineTarget;
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
        simulationEngineTarget = new SelectObservableValue<>();
        Map<SimulationEngine, Composite> engineDetailsMap = new HashMap<>();
        Composite simulationDetails = new Composite(simulationParent, SWT.NONE);
        for (SimulationEngine engine : SimulationEngine.values()) {
            Button button = new Button(simulationEngineGroup, SWT.RADIO);
            button.setText(engine.getName());
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
        createPrismTab(prismDetails, modifyListener);
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

        simulationKindTarget = new SelectObservableValue<>();
        for (SimulationKind kind : SimulationKind.values()) {
            Button button = new Button(qualityObjectivesGroup, SWT.RADIO);
            button.setText(kind.getName());
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
                "Select Monitor Repository File", getShell(), ModelFileTypeConstants.EMPTY_STRING);

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
        configuration.setAttribute(SimulationConstants.NUMBER_OF_RUNS, SimulationConstants.DEFAULT_NUMBER_OF_RUNS);
        configuration.setAttribute(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN,
                SimulationConstants.DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN);
        configuration.setAttribute(SimulationConstants.SIMULATION_ENGINE,
                SimulationConstants.DEFAULT_SIMULATION_ENGINE.name());
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        IObservableValue<String> simulationIdTarget = WidgetProperties.text(SWT.Modify)
            .observe(textSimulationID);
        IObservableValue<String> simulationIdModel = ConfigurationProperties.string(SimulationConstants.SIMULATION_ID)
            .observe(configuration);
        UpdateValueStrategy<String, String> simulationIdUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        simulationIdUpdateStrategy.setBeforeSetValidator(new NotEmptyValidator("Simulation ID"));
        Binding simulationIdBindValue = ctx.bindValue(simulationIdTarget, simulationIdModel, simulationIdUpdateStrategy,
                null);
        ControlDecorationSupport.create(simulationIdBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> numberOfRunsTarget = WidgetProperties.text(SWT.Modify)
            .observe(textNumberOfRuns);
        IObservableValue<Integer> numberOfRunsModel = ConfigurationProperties
            .integer(SimulationConstants.NUMBER_OF_RUNS)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> numberOfRunsUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        numberOfRunsUpdateStrategy.setBeforeSetValidator(new MinIntegerValidator("Number of runs", 1));
        Binding numberOfRunsBindValue = ctx.bindValue(numberOfRunsTarget, numberOfRunsModel, numberOfRunsUpdateStrategy,
                null);
        ControlDecorationSupport.create(numberOfRunsBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> numberOfSimulationsPerRunTarget = WidgetProperties.text(SWT.Modify)
            .observe(textNumerOfSimulationsPerRun);
        IObservableValue<Integer> numberOfSimulationsPerRunModel = ConfigurationProperties
            .integer(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> numberOfSimulationsPerRunUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        numberOfSimulationsPerRunUpdateStrategy
            .setBeforeSetValidator(new MinIntegerValidator("Number of simulations per run", 1));
        Binding numberOfSimulationsPerRunBindValue = ctx.bindValue(numberOfSimulationsPerRunTarget,
                numberOfSimulationsPerRunModel, numberOfSimulationsPerRunUpdateStrategy, null);
        ControlDecorationSupport.create(numberOfSimulationsPerRunBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<SimulationEngine> simulationEngineModel = ConfigurationProperties
            .enummeration(SimulationConstants.SIMULATION_ENGINE, SimulationEngine.class)
            .observe(configuration);
        UpdateValueStrategy<SimulationEngine, SimulationEngine> simulationEngineUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(simulationEngineTarget, simulationEngineModel, simulationEngineUpdateStrategy, null);

        UpdateStrategyController pcmUpdateController = new UpdateStrategyController() {

            @Override
            public boolean isEnabled() {
                return simulationEngineTarget.getValue() == SimulationEngine.PCM;
            }
        };
        UpdateStrategyController prismUpdateController = new UpdateStrategyController() {

            @Override
            public boolean isEnabled() {
                return simulationEngineTarget.getValue() == SimulationEngine.PRISM;
            }
        };
        initializeFromPCM(configuration, pcmUpdateController);
        initializeFromPRISM(configuration, prismUpdateController);

        ctx.updateTargets();
    }

    private void initializeFromPCM(ILaunchConfiguration configuration, UpdateStrategyController pcmUpdateController) {
        IObservableValue<SimulationKind> simulationKindModel = ConfigurationProperties
            .enummeration(SimulationConstants.QUALITY_OBJECTIVE, SimulationKind.class)
            .observe(configuration);
        UpdateValueStrategy<SimulationKind, SimulationKind> simulationKindUpdateStrategy = new ConditionalUpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, pcmUpdateController);
        ctx.bindValue(simulationKindTarget, simulationKindModel, simulationKindUpdateStrategy,
                new ConditionalUpdateValueStrategy<>(pcmUpdateController));

        IObservableValue<String> monitorRepositoryTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMonitorRepository);
        IObservableValue<String> monitorRepositoryModel = ConfigurationProperties
            .string(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE)
            .observe(configuration);
        UpdateValueStrategy<String, String> monitorRepositoryUpdateStrategy = new ConditionalUpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, pcmUpdateController);
        monitorRepositoryUpdateStrategy.setBeforeSetValidator(new CompoundStringValidator(
                Arrays.asList(new FileURIValidator("Monitor Repository File"), new ExtensionValidator(
                        "Monitor Repository File", ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION[0]))));
        Binding monitorRepositoryBindValue = ctx.bindValue(monitorRepositoryTarget, monitorRepositoryModel,
                monitorRepositoryUpdateStrategy, new ConditionalUpdateValueStrategy<>(pcmUpdateController));
        ControlDecorationSupport.create(monitorRepositoryBindValue, SWT.TOP | SWT.RIGHT);

        ISWTObservableList<String> monitorTarget = WidgetProperties.items()
            .observe(monitors.getList());
        IObservableList<String> monitorModel = ConfigurationProperties.list(ModelFileTypeConstants.MONITORS)
            .observe(configuration);
        UpdateListStrategy<String, String> monitorsTargetToModel = new ConditionalUpdateListStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, pcmUpdateController);
        UpdateListStrategy<String, String> monitorsModelToTarget = new ConditionalUpdateListStrategy<>(
                UpdateValueStrategy.POLICY_NEVER, pcmUpdateController);
        ctx.bindList(monitorTarget, monitorModel, monitorsTargetToModel, monitorsModelToTarget);

        /*
         * try { textFailureScenarioModel.setText(configuration
         * .getAttribute(ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE,
         * ModelFileTypeConstants.EMPTY_STRING)); } catch (CoreException e) {
         * LaunchConfigPlugin.errorLogger(getName(), "Failure Scenario File", e.getMessage()); }
         */
    }

    private void initializeFromPRISM(ILaunchConfiguration configuration,
            UpdateStrategyController prismUpdateController) {
        IObservableValue<String> moduleFilesTarget = WidgetProperties.text(SWT.Modify)
            .observe(textModuleFiles);
        IObservableValue<String[]> moduleFilesModel = new ConfigurationObservableArrayValue(configuration,
                ModelFileTypeConstants.PRISM_MODULE_FILE);
        UpdateValueStrategy<String, String[]> moduleFilesUpdateStrategyTargetToModel = new ConditionalUpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, prismUpdateController);
        moduleFilesUpdateStrategyTargetToModel.setConverter(new StringToArrayConverter());
        UpdateValueStrategy<String[], String> moduleFilesUpdateStrategyModelToTarget = ConditionalUpdateValueStrategy
            .create(new ArrayToStringConverter(), prismUpdateController);
        Binding moduleFilesBindValue = ctx.bindValue(moduleFilesTarget, moduleFilesModel,
                moduleFilesUpdateStrategyTargetToModel, moduleFilesUpdateStrategyModelToTarget);
        ControlDecorationSupport.create(moduleFilesBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> propertyFilesTarget = WidgetProperties.text(SWT.Modify)
            .observe(textPropertyFiles);
        IObservableValue<String[]> propertyFilesModel = new ConfigurationObservableArrayValue(configuration,
                ModelFileTypeConstants.PRISM_PROPERTY_FILE);
        UpdateValueStrategy<String, String[]> propertyFilesUpdateStrategyTargetToModel = new ConditionalUpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, prismUpdateController);
        propertyFilesUpdateStrategyTargetToModel.setConverter(new StringToArrayConverter());
        UpdateValueStrategy<String[], String> propertyFilesUpdateStrategyModelToTarget = ConditionalUpdateValueStrategy
            .create(new ArrayToStringConverter(), prismUpdateController);
        Binding propertyFilesBindValue = ctx.bindValue(propertyFilesTarget, propertyFilesModel,
                propertyFilesUpdateStrategyTargetToModel, propertyFilesUpdateStrategyModelToTarget);
        ControlDecorationSupport.create(propertyFilesBindValue, SWT.TOP | SWT.RIGHT);
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        ctx.updateModels();
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