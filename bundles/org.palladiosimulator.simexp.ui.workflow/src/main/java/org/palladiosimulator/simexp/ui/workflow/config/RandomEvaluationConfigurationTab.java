package org.palladiosimulator.simexp.ui.workflow.config;

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
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
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

        createConfig(simContainer, modifyListener);
        createResources(simContainer, modifyListener);
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
        configuration.setAttribute(SimulationConstants.MEMORY_USAGE, SimulationConstants.DEFAULT_MEMORY_USAGE);
    }

    @Override
    protected void doInitializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        SelectObservableValue<ModelledOptimizationType> modelledOptimizationTypeTarget = modelledOptimizerProvider
            .getModelledOptimizationType();
        ControllableValidator.Enabled isEAEnabled = new EnumEnabler<>(ModelledOptimizationType.EVOLUTIONARY_ALGORITHM,
                modelledOptimizationTypeTarget);

        initializeConfigFrom(configuration, ctx, isEAEnabled);
        initializeResourcesFrom(configuration, ctx, isEAEnabled);
    }

    private void initializeConfigFrom(ILaunchConfiguration configuration, DataBindingContext ctx, Enabled isEAEnabled) {
        IObservableValue<String> errorRewardTarget = WidgetProperties.text(SWT.Modify)
            .observe(textErrorReward);
        IObservableValue<Double> errorRewardModel = ConfigurationProperties
            .value(SimulationConstants.ERROR_REWARD, true)
            .observe(configuration);
        UpdateValueStrategy<String, Double> errorRewardUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        IValidator<Double> errorRewardValidator = new ControllableValidator<>(new NotNullValidator<>("Reward on error"),
                isEAEnabled);
        errorRewardUpdateStrategy.setBeforeSetValidator(errorRewardValidator);
        Binding errorRewardBindValue = ctx.bindValue(errorRewardTarget, errorRewardModel, errorRewardUpdateStrategy,
                null);
        ControlDecorationSupport.create(errorRewardBindValue, SWT.TOP | SWT.RIGHT);
    }

    private void initializeResourcesFrom(ILaunchConfiguration configuration, DataBindingContext ctx,
            Enabled isEAEnabled) {
        IObservableValue<String> memoryUsageTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMemoryUsage);
        IObservableValue<Integer> memoryUsageModel = ConfigurationProperties.integer(SimulationConstants.MEMORY_USAGE)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> memoryUsageUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        IValidator<Integer> memoryUsageValidator = new ControllableValidator<>(
                new MinNumberValidator<>("Memory usage", 1), isEAEnabled);
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