package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableList;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.RewardType;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConditionalUpdateListStrategy;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConditionalUpdateValueStrategy;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.UpdateStrategyController;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ControllableValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.EnumEnabler;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.FileURIValidator;

import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimulatorConfiguration {
    private SelectObservableValue<SimulationEngine> simulationEngineTarget;
    private SelectObservableValue<QualityObjective> qualityObjectiveTarget;
    private SelectObservableValue<RewardType> rewardTypeTarget;

    private Text textMonitorRepository;
    private ListViewer monitors;

    private WritableList<String> moduleFilesTarget;
    private WritableList<String> propertyFilesTarget;

    public void createControl(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        Composite simulationParent = new Composite(parent, SWT.NONE);
        simulationParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout containerLayout = new GridLayout(2, false);
        containerLayout.marginWidth = 0;
        simulationParent.setLayout(containerLayout);

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
        createPcmTab(pcmDetails, ctx, modifyListener);
        engineDetailsMap.put(SimulationEngine.PCM, pcmDetails);
        Composite prismDetails = createEngineDetailsComposite(simulationDetails, SimulationEngine.PRISM);
        createPrismTab(prismDetails, modifyListener);
        engineDetailsMap.put(SimulationEngine.PRISM, prismDetails);

        Group rewardGroup = new Group(simulationEngineContainer, SWT.NONE);
        rewardGroup.setText(SimulationConstants.REWARD_TYPE);
        rewardGroup.setLayout(new RowLayout(SWT.VERTICAL));
        rewardTypeTarget = new SelectObservableValue<>();
        for (RewardType rewardType : RewardType.values()) {
            Button button = new Button(rewardGroup, SWT.RADIO);
            button.setText(rewardType.getName());
            ISWTObservableValue<Boolean> observeable = WidgetProperties.buttonSelection()
                .observe(button);
            rewardTypeTarget.addOption(rewardType, observeable);
        }
        ISideEffect.create(() -> {
            return rewardTypeTarget.getValue();
        }, new Consumer<RewardType>() {

            @Override
            public void accept(RewardType selectedEngine) {
                modifyListener.modifyText(null);
            }
        });
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

    private void createPcmTab(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        final Group qualityObjectivesGroup = new Group(parent, SWT.NONE);
        qualityObjectivesGroup.setText(SimulationConstants.QUALITY_OBJECTIVE);
        qualityObjectivesGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

        Map<QualityObjective, List<String>> simulationKindMonitorItems = new HashMap<>();
        simulationKindMonitorItems.put(QualityObjective.PERFORMANCE, Arrays.asList("System Response Time"));
        simulationKindMonitorItems.put(QualityObjective.RELIABILITY, Arrays.asList("System Response Time"));
        simulationKindMonitorItems.put(QualityObjective.PERFORMABILITY,
                Arrays.asList("System Response Time", "System ExecutionResultType"));

        qualityObjectiveTarget = new SelectObservableValue<>();
        for (QualityObjective kind : QualityObjective.values()) {
            Button button = new Button(qualityObjectivesGroup, SWT.RADIO);
            button.setText(kind.getName());
            ISWTObservableValue<Boolean> observeable = WidgetProperties.buttonSelection()
                .observe(button);
            qualityObjectiveTarget.addOption(kind, observeable);
        }
        qualityObjectiveTarget.setValue(QualityObjective.PERFORMANCE);

        ISideEffect.create(() -> {
            return qualityObjectiveTarget.getValue();
        }, new Consumer<QualityObjective>() {

            @Override
            public void accept(QualityObjective selectedKind) {
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

        final Shell shell = parent.getShell();

        textMonitorRepository = new Text(parent, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(parent, modifyListener, "Monitor Repository File",
                ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION, textMonitorRepository,
                "Select Monitor Repository File", shell, ModelFileTypeConstants.EMPTY_STRING);

        final Group monitorsGroup = new Group(parent, SWT.NONE);
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
        IObservableFactory<QualityObjective, IObservableList<String>> detailFactory = new IObservableFactory<>() {

            @Override
            public IObservableList<String> createObservable(QualityObjective master) {
                IObservableList<String> listModel = new WritableList<>(simulationKindMonitorItems.get(master),
                        String.class);
                return listModel;
            }
        };
        IObservableList<String> monitorsModel = MasterDetailObservables.detailList(qualityObjectiveTarget,
                detailFactory, String.class);
        ctx.bindList(monitorsTarget, monitorsModel, new UpdateListStrategy<>(UpdateValueStrategy.POLICY_NEVER), null);
    }

    private void createPrismTab(Composite parent, ModifyListener modifyListener) {
        Group modulesParent = new Group(parent, SWT.NONE);
        modulesParent.setLayout(new GridLayout(2, false));
        modulesParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        modulesParent.setText("Module Files");
        moduleFilesTarget = createPrismList(modulesParent, modifyListener, "Module File",
                ModelFileTypeConstants.PRISM_MODULE_FILE_EXTENSION);

        Group propertiesParent = new Group(parent, SWT.NONE);
        propertiesParent.setLayout(new GridLayout(2, false));
        propertiesParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        propertiesParent.setText("Property Files");
        propertyFilesTarget = createPrismList(propertiesParent, modifyListener, "Property File",
                ModelFileTypeConstants.PRISM_PROPERTY_FILE_EXTENSION);
    }

    private WritableList<String> createPrismList(Composite parent, ModifyListener modifyListener, String type,
            String[] extension) {
        ListViewer listViewer = new ListViewer(parent, SWT.SINGLE | SWT.BORDER);
        listViewer.getControl()
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ObservableListContentProvider<String> modulesObservableInput = new ObservableListContentProvider<>();
        listViewer.setContentProvider(modulesObservableInput);
        WritableList<String> filesInput = new WritableList<>();
        listViewer.setInput(filesInput);

        Composite buttonParent = new Composite(parent, SWT.NONE);
        buttonParent.setLayout(new GridLayout());
        buttonParent.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));

        Button addButton = new Button(buttonParent, SWT.PUSH);
        addButton.setText("Add...");
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EditRepositoryDialog dialog = new EditRepositoryDialog(parent.getShell(), "Add " + type, type,
                        extension);
                if (dialog.open() == Window.OK) {
                    String uri = dialog.getRepositoryModelUri();
                    filesInput.add(uri);
                    modifyListener.modifyText(null);
                }
            }
        });
        Button editButton = new Button(buttonParent, SWT.PUSH);
        editButton.setText("Edit...");
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = listViewer.getStructuredSelection();
                if (selection.isEmpty()) {
                    return;
                }
                String selectedUri = (String) selection.getFirstElement();
                EditRepositoryDialog dialog = new EditRepositoryDialog(parent.getShell(), "Edit " + type, type,
                        extension, selectedUri);
                if (dialog.open() == Window.OK) {
                    String uri = dialog.getRepositoryModelUri();
                    int index = filesInput.indexOf(selectedUri);
                    filesInput.remove(index);
                    filesInput.add(index, uri);
                    modifyListener.modifyText(null);
                }
            }
        });
        Button removeButton = new Button(buttonParent, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = listViewer.getStructuredSelection();
                if (selection.isEmpty()) {
                    return;
                }
                String selectedUri = (String) selection.getFirstElement();
                filesInput.remove(selectedUri);
                modifyListener.modifyText(null);
            }
        });

        return filesInput;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.SIMULATION_ENGINE,
                SimulationConstants.DEFAULT_SIMULATION_ENGINE.name());
        configuration.setAttribute(SimulationConstants.REWARD_TYPE, SimulationConstants.DEFAULT_REWARD_TYPE.name());
    }

    public void initializeFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
        IObservableValue<SimulationEngine> simulationEngineModel = ConfigurationProperties
            .enummeration(SimulationConstants.SIMULATION_ENGINE, SimulationEngine.class)
            .observe(configuration);
        UpdateValueStrategy<SimulationEngine, SimulationEngine> simulationEngineUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(simulationEngineTarget, simulationEngineModel, simulationEngineUpdateStrategy, null);

        initializeFromPCM(configuration, ctx);
        initializeFromPRISM(configuration, ctx);

        IObservableValue<RewardType> rewardTypeModel = ConfigurationProperties
            .enummeration(SimulationConstants.REWARD_TYPE, RewardType.class)
            .observe(configuration);
        UpdateValueStrategy<RewardType, RewardType> rewardTypeUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(rewardTypeTarget, rewardTypeModel, rewardTypeUpdateStrategy, null);
    }

    private void initializeFromPCM(ILaunchConfiguration configuration, DataBindingContext ctx) {
        UpdateStrategyController pcmUpdateController = new UpdateStrategyController() {

            @Override
            public boolean isEnabled() {
                return simulationEngineTarget.getValue() == SimulationEngine.PCM;
            }
        };
        ControllableValidator.Enabled isPcmEnabled = new EnumEnabler<>(SimulationEngine.PCM, simulationEngineTarget);

        IObservableValue<QualityObjective> qualityObjectiveModel = ConfigurationProperties
            .enummeration(SimulationConstants.QUALITY_OBJECTIVE, QualityObjective.class)
            .observe(configuration);
        UpdateValueStrategy<QualityObjective, QualityObjective> qualityObjectiveUpdateStrategy = new ConditionalUpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, pcmUpdateController);
        ctx.bindValue(qualityObjectiveTarget, qualityObjectiveModel, qualityObjectiveUpdateStrategy,
                new ConditionalUpdateValueStrategy<>(pcmUpdateController));

        IObservableValue<String> monitorRepositoryTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMonitorRepository);
        IObservableValue<String> monitorRepositoryModel = ConfigurationProperties
            .string(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE)
            .observe(configuration);
        UpdateValueStrategy<String, String> monitorRepositoryUpdateStrategy = new ConditionalUpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, pcmUpdateController);
        IValidator<String> monitorRepositoryValidator = new ControllableValidator<>(
                new CompoundStringValidator(
                        Arrays
                            .asList(new FileURIValidator("Monitor Repository File"),
                                    new ExtensionValidator("Monitor Repository File",
                                            ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION[0]))),
                isPcmEnabled);
        monitorRepositoryUpdateStrategy.setBeforeSetValidator(monitorRepositoryValidator);
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
    }

    private void initializeFromPRISM(ILaunchConfiguration configuration, DataBindingContext ctx) {
        UpdateStrategyController prismUpdateController = new UpdateStrategyController() {

            @Override
            public boolean isEnabled() {
                return simulationEngineTarget.getValue() == SimulationEngine.PRISM;
            }
        };

        IObservableList<String> moduleFilesModel = ConfigurationProperties
            .list(ModelFileTypeConstants.PRISM_MODULE_FILE)
            .observe(configuration);
        UpdateListStrategy<String, String> moduleFilesUpdateStrategyTargetToModel = new ConditionalUpdateListStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, prismUpdateController);
        UpdateListStrategy<String, String> moduleFilesUpdateStrategyModelToTarget = new ConditionalUpdateListStrategy<>(
                prismUpdateController);
        Binding moduleFilesBindValue = ctx.bindList(moduleFilesTarget, moduleFilesModel,
                moduleFilesUpdateStrategyTargetToModel, moduleFilesUpdateStrategyModelToTarget);
        ControlDecorationSupport.create(moduleFilesBindValue, SWT.TOP | SWT.RIGHT);

        IObservableList<String> propertyFilesModel = ConfigurationProperties
            .list(ModelFileTypeConstants.PRISM_PROPERTY_FILE)
            .observe(configuration);
        UpdateListStrategy<String, String> propertyFilesUpdateStrategyTargetToModel = new ConditionalUpdateListStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT, prismUpdateController);
        UpdateListStrategy<String, String> propertyFilesUpdateStrategyModelToTarget = new ConditionalUpdateListStrategy<>(
                prismUpdateController);
        Binding propertyFilesBindValue = ctx.bindList(propertyFilesTarget, propertyFilesModel,
                propertyFilesUpdateStrategyTargetToModel, propertyFilesUpdateStrategyModelToTarget);
        ControlDecorationSupport.create(propertyFilesBindValue, SWT.TOP | SWT.RIGHT);
    }
}
