package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.function.Consumer;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
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
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.MinIntegerValidator;
import org.palladiosimulator.simexp.ui.workflow.config.debug.BaseLaunchConfigurationTab;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;

public class EvolutionaryAlgorithmConfigurationTab extends BaseLaunchConfigurationTab {
    private static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    private static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private final IModelledOptimizerProvider modelledOptimizerProvider;

    private Text textPopulationSize;
    private Text textMaxGenerations;
    private Text textSteadyFitness;
    private Text textMutationRate;

    public EvolutionaryAlgorithmConfigurationTab(DataBindingContext ctx,
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

                recursiveSetEnabled(container, selectedType == ModelledOptimizationType.EVOLUTIONARY_ALGORITHM);
                modifyListener.modifyText(null);
            }
        });

        Composite simContainer = new Composite(container, SWT.NONE);
        simContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout simContainerLayout = new GridLayout();
        simContainerLayout.marginWidth = 0;
        simContainer.setLayout(simContainerLayout);

        createConfig(simContainer, modifyListener);
        createLimits(simContainer, modifyListener);
    }

    private void createConfig(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Configuration");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        Label populationSizeLabel = new Label(container, SWT.NONE);
        populationSizeLabel.setText("Population size:");
        textPopulationSize = new Text(container, SWT.BORDER);
        textPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPopulationSize.addModifyListener(modifyListener);

        Label mutationRateLabel = new Label(container, SWT.NONE);
        mutationRateLabel.setText("Mutation rate:");
        textMutationRate = new Text(container, SWT.BORDER);
        textMutationRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMutationRate.addModifyListener(modifyListener);
    }

    private void createLimits(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Limits");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        Label maxGenerationsLabel = new Label(container, SWT.NONE);
        maxGenerationsLabel.setText("Max generations:");
        textMaxGenerations = new Text(container, SWT.BORDER);
        textMaxGenerations.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMaxGenerations.addModifyListener(modifyListener);

        Label steadyFitnessLabel = new Label(container, SWT.NONE);
        steadyFitnessLabel.setText("Steady fitness:");
        textSteadyFitness = new Text(container, SWT.BORDER);
        textSteadyFitness.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textSteadyFitness.addModifyListener(modifyListener);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.POPULATION_SIZE, SimulationConstants.DEFAULT_POPULATION_SIZE);
    }

    @Override
    protected void doInitializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        initializeConfigFrom(configuration, ctx);
        initializeLimitsFrom(configuration, ctx);
    }

    private void initializeConfigFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
        IObservableValue<String> popSizeTarget = WidgetProperties.text(SWT.Modify)
            .observe(textPopulationSize);
        IObservableValue<Integer> popSizeModel = ConfigurationProperties.integer(SimulationConstants.POPULATION_SIZE)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> popSizeUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        popSizeUpdateStrategy.setBeforeSetValidator(new MinIntegerValidator("Population size", 1));
        Binding popSizeBindValue = ctx.bindValue(popSizeTarget, popSizeModel, popSizeUpdateStrategy, null);
        ControlDecorationSupport.create(popSizeBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> mutationRateTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMutationRate);
        IObservableValue<Double> mutationRateModel = ConfigurationProperties
            .value(SimulationConstants.MUTATION_RATE)
            .observe(configuration);
        UpdateValueStrategy<String, Double> mutationRateUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        // mutationRateUpdateStrategy.setBeforeSetValidator(new MinIntegerValidator("Mutation rate",
        // 1));
        Binding mutationRateBindValue = ctx.bindValue(mutationRateTarget, mutationRateModel, mutationRateUpdateStrategy,
                null);
        ControlDecorationSupport.create(mutationRateBindValue, SWT.TOP | SWT.RIGHT);
    }

    private void initializeLimitsFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
        IObservableValue<String> maxGenTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMaxGenerations);
        IObservableValue<Integer> maxGenModel = ConfigurationProperties
            .integer(SimulationConstants.MAX_GENERATIONS, false)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> customMaxGenUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        customMaxGenUpdateStrategy.setConverter(StringToNumberConverter.toInteger(false));
        Binding maxGenBindValue = ctx.bindValue(maxGenTarget, maxGenModel, customMaxGenUpdateStrategy, null);
        ControlDecorationSupport.create(maxGenBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> steadyFitnessTarget = WidgetProperties.text(SWT.Modify)
            .observe(textSteadyFitness);
        IObservableValue<Integer> steadyFitnessModel = ConfigurationProperties
            .integer(SimulationConstants.STEADY_FITNESS, false)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> steadyFitnessUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        steadyFitnessUpdateStrategy.setConverter(StringToNumberConverter.toInteger(false));
        Binding steadyFitnessBindValue = ctx.bindValue(steadyFitnessTarget, steadyFitnessModel,
                steadyFitnessUpdateStrategy, null);
        ControlDecorationSupport.create(steadyFitnessBindValue, SWT.TOP | SWT.RIGHT);
    }

    @Override
    public Image getImage() {
        return ImageRegistryHelper.getTabImage(PLUGIN_ID, CONFIGURATION_TAB_IMAGE_PATH);
    }

    @Override
    public String getName() {
        return "Evolutionary Algorithm Configuration";
    }

    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.EvolutionaryAlgorithmConfigurationTab";
    }
}