package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.RandomSampleType;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ControllableValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ControllableValidator.Enabled;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.EnumEnabler;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.MinNumberValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.NotNullValidator;
import org.palladiosimulator.simexp.ui.workflow.config.debug.BaseLaunchConfigurationTab;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;

public class RandomEvaluationConfigurationTab extends BaseLaunchConfigurationTab {
    private static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    private static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private final IModelledOptimizerProvider modelledOptimizerProvider;
    private SelectObservableValue<RandomSampleType> randomSampleTypeTarget;

    private Text textSampleCount;
    private Text textErrorReward;
    private Text textMemoryUsage;

    public RandomEvaluationConfigurationTab(DataBindingContext ctx,
            IModelledOptimizerProvider modelledOptimizerProvider) {
        super(ctx);
        this.modelledOptimizerProvider = modelledOptimizerProvider;
    }

    @Override
    public void doCreateControl(Composite parent, DataBindingContext ctx) {
        ModifyListener modifyListener = new SimExpModifyListener();

        Composite container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout());

        SelectObservableValue<ModelledOptimizationType> modelledOptimizationTypeTarget = modelledOptimizerProvider
            .getModelledOptimizationType();

        ISideEffect.create(() -> {
            return modelledOptimizationTypeTarget.getValue();
        }, new Consumer<ModelledOptimizationType>() {

            @Override
            public void accept(ModelledOptimizationType selectedType) {
                if (selectedType == null) {
                    return;
                }

                recursiveSetEnabled(container, selectedType == ModelledOptimizationType.RANDOM);
                modifyListener.modifyText(null);
            }
        });

        Composite simContainer = new Composite(container, SWT.NONE);
        simContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout simContainerLayout = new GridLayout();
        simContainerLayout.marginWidth = 0;
        simContainer.setLayout(simContainerLayout);

        createSampleControl(simContainer, modifyListener);
        createConfig(simContainer, modifyListener);
        createResources(simContainer, modifyListener);
    }

    private void createSampleControl(Composite parent, ModifyListener modifyListener) {
        Group simulationParent = new Group(parent, SWT.NONE);
        simulationParent.setText("Sample control");
        simulationParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout containerLayout = new GridLayout(2, false);
        containerLayout.marginWidth = 0;
        simulationParent.setLayout(containerLayout);

        Composite randomSampleTypeContainer = new Composite(simulationParent, SWT.NONE);
        randomSampleTypeContainer.setLayoutData(new GridData(SWT.None, SWT.TOP, false, true));
        randomSampleTypeContainer.setLayout(new GridLayout());
        // randomSampleTypeContainer.setBackground(new Color(0, 127, 0));

        Group randomSampleTypeGroup = new Group(randomSampleTypeContainer, SWT.NONE);
        randomSampleTypeGroup.setText(SimulationConstants.RANDOM_SAMPLE_TYPE);
        randomSampleTypeGroup.setLayout(new RowLayout(SWT.VERTICAL));
        randomSampleTypeTarget = new SelectObservableValue<>();
        Map<RandomSampleType, Composite> randomSampleDetailsMap = new HashMap<>();
        Composite randomSampleDetails = new Composite(simulationParent, SWT.NONE);
        for (RandomSampleType engine : RandomSampleType.values()) {
            Button button = new Button(randomSampleTypeGroup, SWT.RADIO);
            button.setText(engine.getName());
            ISWTObservableValue<Boolean> observeable = WidgetProperties.buttonSelection()
                .observe(button);
            randomSampleTypeTarget.addOption(engine, observeable);
        }

        ISideEffect.create(() -> {
            return randomSampleTypeTarget.getValue();
        }, new Consumer<RandomSampleType>() {

            @Override
            public void accept(RandomSampleType selectedrandomSampleType) {
                for (Map.Entry<RandomSampleType, Composite> entry : randomSampleDetailsMap.entrySet()) {
                    Composite detailsComposite = entry.getValue();
                    GridData layoutData = (GridData) detailsComposite.getLayoutData();
                    if (selectedrandomSampleType == entry.getKey()) {
                        layoutData.exclude = false;
                        detailsComposite.setEnabled(true);
                    } else {
                        layoutData.exclude = true;
                        detailsComposite.setEnabled(false);
                    }
                }
                randomSampleDetails.layout();
                modifyListener.modifyText(null);
            }
        });

        randomSampleDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        randomSampleDetails.setLayout(new GridLayout());

        Group container = new Group(randomSampleDetails, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));
        randomSampleDetailsMap.put(RandomSampleType.COUNT, container);

        Label errorDefaultLabel = new Label(container, SWT.NONE);
        errorDefaultLabel.setText("Sample count:");
        textSampleCount = new Text(container, SWT.BORDER);
        textSampleCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textSampleCount.addModifyListener(modifyListener);
    }

    private void createConfig(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Configuration");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        Label errorDefaultLabel = new Label(container, SWT.NONE);
        errorDefaultLabel.setText("Reward on error:");
        textErrorReward = new Text(container, SWT.BORDER);
        textErrorReward.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textErrorReward.addModifyListener(modifyListener);
    }

    private void createResources(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Resources");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        Label memoryUsageLabel = new Label(container, SWT.NONE);
        memoryUsageLabel.setText("Memory usage (GiB):");
        textMemoryUsage = new Text(container, SWT.BORDER);
        textMemoryUsage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMemoryUsage.addModifyListener(modifyListener);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.RANDOM_SAMPLE_TYPE,
                SimulationConstants.DEFAULT_RANDOM_SAMPLE_TYPE.name());
        configuration.setAttribute(SimulationConstants.MEMORY_USAGE, SimulationConstants.DEFAULT_MEMORY_USAGE);
    }

    @Override
    protected void doInitializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        SelectObservableValue<ModelledOptimizationType> modelledOptimizationTypeTarget = modelledOptimizerProvider
            .getModelledOptimizationType();
        ControllableValidator.Enabled isREEnabled = new EnumEnabler<>(ModelledOptimizationType.RANDOM,
                modelledOptimizationTypeTarget);

        initializeSampleControlFrom(configuration, ctx, isREEnabled);
        initializeConfigFrom(configuration, ctx, isREEnabled);
        initializeResourcesFrom(configuration, ctx, isREEnabled);
    }

    private void initializeSampleControlFrom(ILaunchConfiguration configuration, DataBindingContext ctx,
            Enabled isREEnabled) {
        IObservableValue<RandomSampleType> randomSampleTypeModel = ConfigurationProperties
            .enummeration(SimulationConstants.RANDOM_SAMPLE_TYPE, RandomSampleType.class)
            .observe(configuration);
        UpdateValueStrategy<RandomSampleType, RandomSampleType> randomSampleTypeStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(randomSampleTypeTarget, randomSampleTypeModel, randomSampleTypeStrategy, null);

        IObservableValue<String> sampleCountTarget = WidgetProperties.text(SWT.Modify)
            .observe(textSampleCount);
        IObservableValue<Integer> sampleCountModel = ConfigurationProperties
            .integer(SimulationConstants.RANDOM_SAMPLE_COUNT)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> sampleCountUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        IValidator<Integer> popSizeValidator = new ControllableValidator<>(
                new MinNumberValidator<>("Random sample count", 1), isREEnabled);
        sampleCountUpdateStrategy.setBeforeSetValidator(popSizeValidator);
        Binding sampleCountBindValue = ctx.bindValue(sampleCountTarget, sampleCountModel, sampleCountUpdateStrategy,
                null);
        ControlDecorationSupport.create(sampleCountBindValue, SWT.TOP | SWT.RIGHT);
    }

    private void initializeConfigFrom(ILaunchConfiguration configuration, DataBindingContext ctx, Enabled isREEnabled) {
        IObservableValue<String> errorRewardTarget = WidgetProperties.text(SWT.Modify)
            .observe(textErrorReward);
        IObservableValue<Double> errorRewardModel = ConfigurationProperties
            .value(SimulationConstants.ERROR_REWARD, true)
            .observe(configuration);
        UpdateValueStrategy<String, Double> errorRewardUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        IValidator<Double> errorRewardValidator = new ControllableValidator<>(new NotNullValidator<>("Reward on error"),
                isREEnabled);
        errorRewardUpdateStrategy.setBeforeSetValidator(errorRewardValidator);
        Binding errorRewardBindValue = ctx.bindValue(errorRewardTarget, errorRewardModel, errorRewardUpdateStrategy,
                null);
        ControlDecorationSupport.create(errorRewardBindValue, SWT.TOP | SWT.RIGHT);
    }

    private void initializeResourcesFrom(ILaunchConfiguration configuration, DataBindingContext ctx,
            Enabled isREEnabled) {
        IObservableValue<String> memoryUsageTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMemoryUsage);
        IObservableValue<Integer> memoryUsageModel = ConfigurationProperties.integer(SimulationConstants.MEMORY_USAGE)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> memoryUsageUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        IValidator<Integer> memoryUsageValidator = new ControllableValidator<>(
                new MinNumberValidator<>("Memory usage", 1), isREEnabled);
        memoryUsageUpdateStrategy.setBeforeSetValidator(memoryUsageValidator);
        Binding memoryUsageBindValue = ctx.bindValue(memoryUsageTarget, memoryUsageModel, memoryUsageUpdateStrategy,
                null);
        ControlDecorationSupport.create(memoryUsageBindValue, SWT.TOP | SWT.RIGHT);
    }

    @Override
    public Image getImage() {
        return ImageRegistryHelper.getTabImage(PLUGIN_ID, CONFIGURATION_TAB_IMAGE_PATH);
    }

    @Override
    public String getName() {
        return "Random Evaluation Configuration";
    }

    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.RandomEvaluationConfigurationTab";
    }
}