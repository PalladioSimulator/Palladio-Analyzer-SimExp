package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.function.Consumer;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.MinIntegerValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.NotEmptyValidator;
import org.palladiosimulator.simexp.ui.workflow.config.debug.BaseLaunchConfigurationTab;
import org.palladiosimulator.simexp.workflow.trafo.ITrafoNameProvider;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;

public class SimExpConfigurationTab extends BaseLaunchConfigurationTab {
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    public static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private final TransformationConfiguration transformationConfiguration;
    private final SimulatorConfiguration simulatorConfiguration;
    private final ModelledSimulatorConfiguration modelledSimulatorConfiguration;

    private Text textSimulationID;
    private Text textNumberOfRuns;
    private Text textNumerOfSimulationsPerRun;
    private SelectObservableValue<SimulatorType> simulatorTypeTarget;
    // private Text textSModel;

    public SimExpConfigurationTab(DataBindingContext ctx, IModelValueProvider modelValueProvider,
            ITrafoNameProvider trafoNameProvider) {
        super(ctx);
        this.transformationConfiguration = new TransformationConfiguration(modelValueProvider, trafoNameProvider);
        this.simulatorConfiguration = new SimulatorConfiguration();
        this.modelledSimulatorConfiguration = new ModelledSimulatorConfiguration();
    }

    @Override
    public void doCreateControl(Composite parent, DataBindingContext ctx) {
        ModifyListener modifyListener = new SimExpModifyListener();

        Composite container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout());

        Composite simContainer = new Composite(container, SWT.NONE);
        simContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout simContainerLayout = new GridLayout(2, false);
        simContainerLayout.marginWidth = 0;
        simContainer.setLayout(simContainerLayout);

        createSimulation(simContainer, modifyListener);
        transformationConfiguration.createControl(simContainer, ctx, modifyListener);
        createSimulatorType(container, ctx, modifyListener);
        simulatorConfiguration.createControl(container, ctx, modifyListener);
    }

    private void createSimulation(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Basics");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
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
    }

    private void createSimulatorType(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        Composite simulatorTypeParent = new Composite(parent, SWT.NONE);
        simulatorTypeParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        simulatorTypeParent.setLayout(new GridLayout(2, false));

        Group simulatorTypeGroup = new Group(simulatorTypeParent, SWT.NONE);
        simulatorTypeGroup.setText(SimulationConstants.SIMULATOR_TYPE);
        simulatorTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        simulatorTypeGroup.setLayout(new GridLayout());

        Composite comp = new Composite(simulatorTypeGroup, SWT.NONE);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        comp.setLayout(new GridLayout(2, false));

        simulatorTypeTarget = new SelectObservableValue<>();
        Button buttonCustom = new Button(comp, SWT.RADIO);
        buttonCustom.setText(SimulatorType.CUSTOM.getName());
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        buttonCustom.setLayoutData(gd);
        ISWTObservableValue<Boolean> customObserveable = WidgetProperties.buttonSelection()
            .observe(buttonCustom);
        simulatorTypeTarget.addOption(SimulatorType.CUSTOM, customObserveable);

        Button buttonModelled = new Button(comp, SWT.RADIO);
        buttonModelled.setText(SimulatorType.MODELLED.getName());

        Composite modelledContainer = modelledSimulatorConfiguration.createControl(simulatorTypeParent, ctx,
                modifyListener);

        ISWTObservableValue<Boolean> modelledObserveable = WidgetProperties.buttonSelection()
            .observe(buttonModelled);
        simulatorTypeTarget.addOption(SimulatorType.MODELLED, modelledObserveable);

        ISideEffect.create(() -> {
            return simulatorTypeTarget.getValue();
        }, new Consumer<SimulatorType>() {

            @Override
            public void accept(SimulatorType selectedSimulatorType) {
                if (selectedSimulatorType == null) {
                    return;
                }

                recursiveSetEnabled(modelledContainer, selectedSimulatorType == SimulatorType.MODELLED);
                modifyListener.modifyText(null);
            }
        });
    }

    private void recursiveSetEnabled(Control ctrl, boolean enabled) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                recursiveSetEnabled(c, enabled);
            }
        } else {
            ctrl.setEnabled(enabled);
        }
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.NUMBER_OF_RUNS, SimulationConstants.DEFAULT_NUMBER_OF_RUNS);
        configuration.setAttribute(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN,
                SimulationConstants.DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN);

        configuration.setAttribute(SimulationConstants.SIMULATOR_TYPE,
                SimulationConstants.DEFAULT_SIMULATOR_TYPE.name());

        modelledSimulatorConfiguration.setDefaults(configuration);
        simulatorConfiguration.setDefaults(configuration);
    }

    @Override
    protected void doInitializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        initializeSimulationFrom(configuration, ctx);
        transformationConfiguration.initializeFrom(configuration, ctx);
        initializeSimulatorTypeFrom(configuration, ctx);
        simulatorConfiguration.initializeFrom(configuration, ctx);
    }

    private void initializeSimulationFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
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
    }

    private void initializeSimulatorTypeFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
        IObservableValue<SimulatorType> simulatorTypeModel = ConfigurationProperties
            .enummeration(SimulationConstants.SIMULATOR_TYPE, SimulatorType.class)
            .observe(configuration);
        UpdateValueStrategy<SimulatorType, SimulatorType> simulatorTypeUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(simulatorTypeTarget, simulatorTypeModel, simulatorTypeUpdateStrategy, null);
        modelledSimulatorConfiguration.initializeFrom(configuration, ctx, simulatorTypeTarget);
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